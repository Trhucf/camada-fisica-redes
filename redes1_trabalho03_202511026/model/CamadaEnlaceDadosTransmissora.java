/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 29/09/2026
* Ultima alteracao.: 08/11/2026
* Nome.............: Enlace de Dados
* Funcao...........: Enquadramento de dados para transmissão
*************************************************************** */



package model;

import java.util.ArrayList;

public class CamadaEnlaceDadosTransmissora {

    private final CamadaFisicaTransmissora camadaFisicaTransmissora;
    private final CamadaEnlaceDeDadosTransmissoraEnquadramento enquadramento;

    private ArrayList<Boolean> quadro;
    private ArrayList<Integer> fluxoSimbolos;

    private static final int TAMANHO_CRC = 32;
    private static final int TAMANHO_BLOCO_CRC = 40;
    private static final int TAMANHO_BLOCO_HAMMING = 32;

    private static final String POLINOMIO_CRC =
            "100000100110000010001110110110111";

    public CamadaEnlaceDadosTransmissora(
            CamadaFisicaTransmissora camadaFisicaTransmissora,
            CamadaEnlaceDeDadosTransmissoraEnquadramento enquadramento) {

        this.camadaFisicaTransmissora = camadaFisicaTransmissora;
        this.enquadramento = enquadramento;
    }

    // Construtor alternativo para compatibilidade com chamadas antigas.
    public CamadaEnlaceDadosTransmissora(
            CamadaFisicaTransmissora camadaFisicaTransmissora) {

        this(camadaFisicaTransmissora,
             new CamadaEnlaceDeDadosTransmissoraEnquadramento());
    }

    public void camadaDeEnquadramento(
            boolean[] bits,
            int tipoCodificacao,
            int tipoEnquadramento,
            int tipoControleDeErro,
            int probabilidadeErro) {

        quadro = null;
        fluxoSimbolos = null;

        camadaEnlaceDeDadosTransmissoraEnquadramento(
                bits, tipoEnquadramento);

        if (tipoEnquadramento == 3) {

            if (fluxoSimbolos != null && !fluxoSimbolos.isEmpty()) {

                camadaEnlaceDeDadosTransmissoraControleDeErro(
                        tipoControleDeErro);

                camadaFisicaTransmissora.codificacaoManchesterComViolacao(
                        fluxoSimbolos, 3, 3,
                        tipoControleDeErro, probabilidadeErro);
            }

        } else if (quadro != null && !quadro.isEmpty()) {

            camadaEnlaceDeDadosTransmissoraControleDeErro(
                    tipoControleDeErro);

            camadaFisicaTransmissora.tipoDeCodificacao(
                    quadro, tipoCodificacao, tipoEnquadramento,
                    tipoControleDeErro, probabilidadeErro);
        }
    }

    public void camadaEnlaceDeDadosTransmissoraEnquadramento(
            boolean[] bits, int tipoEnquadramento) {

        if (bits == null || bits.length == 0) {
            return;
        }

        switch (tipoEnquadramento) {

            case 0:
                quadro = enquadramento.contagemDeCaracteres(bits);
                break;

            case 1:
                quadro = enquadramento.insercaoDeBytes(bits);
                break;

            case 2:
                quadro = enquadramento.bitStuffing(bits);
                break;

            case 3:
                fluxoSimbolos = enquadramento.violacaoCamadaFisica(bits);
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de enquadramento inválido.");
        }
    }

    // =========================================================
    // CONTROLE DE ERRO
    // =========================================================

    public void camadaEnlaceDeDadosTransmissoraControleDeErro(
            int tipoControleDeErro) {

        if (fluxoSimbolos != null) {
            aplicarControleNosSimbolos(tipoControleDeErro);
            return;
        }

        if (quadro == null || quadro.isEmpty()) {
            return;
        }

        switch (tipoControleDeErro) {

            case 0:
                quadro = adicionarParidade(quadro, true, 32);
                break;

            case 1:
                quadro = adicionarParidade(quadro, false, 32);
                break;

            case 2:
                quadro = adicionarCRCEmBlocos(quadro);
                break;

            case 3:
                quadro = adicionarHammingEmBlocos(quadro);
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de controle de erro inválido.");
        }
    }

    // =========================================================
    // PARIDADE
    // =========================================================

    private ArrayList<Boolean> adicionarParidade(
            ArrayList<Boolean> dados,
            boolean paridadePar,
            int tamanhoBloco) {

        ArrayList<Boolean> resultado = new ArrayList<>();

        for (int inicio = 0; inicio < dados.size();
                inicio += tamanhoBloco) {

            int fim = Math.min(inicio + tamanhoBloco, dados.size());
            int quantidadeUns = 0;

            for (int i = inicio; i < fim; i++) {

                boolean bit = dados.get(i);
                resultado.add(bit);

                if (bit) {
                    quantidadeUns++;
                }
            }

            boolean bitParidade;

            if (paridadePar) {
                bitParidade = quantidadeUns % 2 != 0;
            } else {
                bitParidade = quantidadeUns % 2 == 0;
            }

            resultado.add(bitParidade);
        }

        return resultado;
    }

    // =========================================================
    // CRC
    // =========================================================

    private ArrayList<Boolean> adicionarCRCEmBlocos(
            ArrayList<Boolean> dados) {

        ArrayList<Boolean> resultado = new ArrayList<>();

        for (int inicio = 0; inicio < dados.size();
                inicio += TAMANHO_BLOCO_CRC) {

            int fim = Math.min(
                    inicio + TAMANHO_BLOCO_CRC, dados.size());

            ArrayList<Boolean> bloco = new ArrayList<>(
                    dados.subList(inicio, fim));

            resultado.addAll(adicionarCRC(bloco));
        }

        return resultado;
    }

    private ArrayList<Boolean> adicionarCRC(
            ArrayList<Boolean> dados) {

        int tamanho = dados.size();

        int[] copia = new int[tamanho + TAMANHO_CRC];

        for (int i = 0; i < tamanho; i++) {
            copia[i] = dados.get(i) ? 1 : 0;
        }

        for (int i = 0; i < tamanho; i++) {

            if (copia[i] == 1) {

                for (int j = 0; j < POLINOMIO_CRC.length(); j++) {

                    copia[i + j] ^=
                            POLINOMIO_CRC.charAt(j) - '0';
                }
            }
        }

        ArrayList<Boolean> resultado = new ArrayList<>();

        // Dados originais.
        resultado.addAll(dados);

        // Resto da divisão.
        for (int i = tamanho; i < copia.length; i++) {
            resultado.add(copia[i] == 1);
        }

        return resultado;
    }

    // =========================================================
    // HAMMING ESTENDIDO (SECDED)
    // =========================================================

    private ArrayList<Boolean> adicionarHammingEmBlocos(
            ArrayList<Boolean> dados) {

        ArrayList<Boolean> resultado = new ArrayList<>();

        for (int inicio = 0; inicio < dados.size();
                inicio += TAMANHO_BLOCO_HAMMING) {

            int fim = Math.min(
                    inicio + TAMANHO_BLOCO_HAMMING, dados.size());

            ArrayList<Boolean> bloco = new ArrayList<>(
                    dados.subList(inicio, fim));

            resultado.addAll(codificarHamming(bloco));
        }

        return resultado;
    }

    
private ArrayList<Boolean> codificarHamming(
        ArrayList<Boolean> dados) {

    int quantidadeDados = dados.size();

    // Calcula a quantidade necessária de bits de paridade.
    int quantidadeParidades = calcularQuantidadeParidades(
            quantidadeDados);

    int tamanhoCodigo = quantidadeDados + quantidadeParidades;

    int[] codigo = new int[tamanhoCodigo + 1];

    int indiceDados = 0;

    // Insere os dados nas posições que não são potências de 2.
    for (int posicao = 1; posicao <= tamanhoCodigo; posicao++) {

        if (!ehPotenciaDeDois(posicao)) {
            codigo[posicao] = dados.get(indiceDados++) ? 1 : 0;
        }
    }

    // Calcula as paridades Hamming.
    for (int paridade = 1;
            paridade <= (1 << (quantidadeParidades - 1));
            paridade *= 2) {

        int quantidadeUns = 0;

        for (int posicao = 1;
                posicao <= tamanhoCodigo; posicao++) {

            if ((posicao & paridade) != 0 &&
                    codigo[posicao] == 1) {

                quantidadeUns++;
            }
        }

        codigo[paridade] = quantidadeUns % 2;
    }

    ArrayList<Boolean> resultado = new ArrayList<>();

    int quantidadeUns = 0;

    // Adiciona o código Hamming.
    for (int posicao = 1;
            posicao <= tamanhoCodigo; posicao++) {

        resultado.add(codigo[posicao] == 1);

        if (codigo[posicao] == 1) {
            quantidadeUns++;
        }
    }

    // Paridade global.
    resultado.add(quantidadeUns % 2 != 0);

    return resultado;
}

private int calcularQuantidadeParidades(int quantidadeDados) {

    int quantidade = 0;

    while ((1 << quantidade) <
            quantidadeDados + quantidade + 1) {

        quantidade++;
    }

    return quantidade;
}

private boolean ehPotenciaDeDois(int valor) {
    return valor > 0 && (valor & (valor - 1)) == 0;
}


    // =========================================================
    // CONTROLE DE ERRO PARA VIOLAÇÃO FÍSICA
    // =========================================================

    private void aplicarControleNosSimbolos(int tipoControle) {

        ArrayList<Integer> resultado = new ArrayList<>();

        int posicao = 0;

        while (posicao < fluxoSimbolos.size()) {

            if (fluxoSimbolos.get(posicao) != 2) {
                throw new IllegalArgumentException(
                        "Delimitador inicial inválido.");
            }

            resultado.add(2);
            posicao++;

            ArrayList<Boolean> dados = new ArrayList<>();

            while (posicao < fluxoSimbolos.size() &&
                    fluxoSimbolos.get(posicao) != 2) {

                int simbolo = fluxoSimbolos.get(posicao++);

                if (simbolo != 0 && simbolo != 1) {
                    throw new IllegalArgumentException(
                            "Símbolo inválido no quadro.");
                }

                dados.add(simbolo == 1);
            }

            if (posicao >= fluxoSimbolos.size()) {
                throw new IllegalArgumentException(
                        "Delimitador final ausente.");
            }

            ArrayList<Boolean> codificados;

            switch (tipoControle) {

                case 0:
                    codificados = adicionarParidade(dados, true, 32);
                    break;

                case 1:
                    codificados = adicionarParidade(dados, false, 32);
                    break;

                case 2:
                    codificados = adicionarCRC(dados);
                    break;

                case 3:
                    codificados = codificarHamming(dados);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Tipo de controle inválido.");
            }

            for (boolean bit : codificados) {
                resultado.add(bit ? 1 : 0);
            }

            // Delimitador final.
            resultado.add(2);
            posicao++;
        }

        fluxoSimbolos = resultado;
    }
}


