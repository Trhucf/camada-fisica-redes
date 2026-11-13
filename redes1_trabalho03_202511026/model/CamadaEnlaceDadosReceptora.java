/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 29/09/2026
* Ultima alteracao.: 09/11/2026
* Nome.............: Camada Enlace De Dados
* Funcao...........: Desenquadramento de dados para transmissão
*************************************************************** */


package model;

import java.util.ArrayList;
import controller.ControllerCamadaFisicaTransmissora;

public class CamadaEnlaceDadosReceptora {

    private final CamadaAplicacaoReceptora camadaAplicacaoReceptora;
    private final ControllerCamadaFisicaTransmissora camadaFisicaTransmissora;
    private final CamadaEnlaceDeDadosDesenquadramento desenquadramento;

    private static final int TAMANHO_CRC = 32;
    private static final int BLOCO_CRC = 40;
    private static final int BLOCO_HAMMING = 32;

    private static final String POLINOMIO_CRC =
            "100000100110000010001110110110111";

    public CamadaEnlaceDadosReceptora(
            CamadaAplicacaoReceptora camadaAplicacaoReceptora,
            ControllerCamadaFisicaTransmissora camadaFisicaTransmissora) {

        this.camadaAplicacaoReceptora = camadaAplicacaoReceptora;
        this.camadaFisicaTransmissora = camadaFisicaTransmissora;

        this.desenquadramento =
                new CamadaEnlaceDeDadosDesenquadramento();
    }

    // =========================================================
    // CONTROLE DE ERRO CONVENCIONAL
    // =========================================================

    public void camadaDeEnlaceDadosReceptoraControleDeErro(
            int[] fluxoBits,
            int tipoEnquadramento,
            int tipoControleDeErro) {

        if (fluxoBits == null || fluxoBits.length == 0) {
            mensagemDeErroDeTransmissao();
            return;
        }

        int[] fluxoValidado;

        switch (tipoControleDeErro) {

            case 0:
                fluxoValidado = removerParidade(fluxoBits, true);
                break;

            case 1:
                fluxoValidado = removerParidade(fluxoBits, false);
                break;

            case 2:
                fluxoValidado = removerCRC(fluxoBits);
                break;

            case 3:
                fluxoValidado = removerHamming(fluxoBits);
                break;

            default:
                fluxoValidado = null;
        }

        if (fluxoValidado == null) {
            mensagemDeErroDeTransmissao();
            return;
        }

        camadaDeEnlaceDadosReceptoraDesenquadro(
                fluxoValidado, tipoEnquadramento);
    }

    // =========================================================
    // PARIDADE
    // =========================================================

    private int[] removerParidade(
            int[] fluxoBits, boolean paridadePar) {

        ArrayList<Integer> resultado = new ArrayList<>();

        int inicio = 0;

        while (inicio < fluxoBits.length) {

            int restante = fluxoBits.length - inicio;

            int tamanhoDados;

            if (restante > 33) {
                tamanhoDados = 32;
            } else {
                tamanhoDados = restante - 1;
            }

            if (tamanhoDados < 1 || tamanhoDados > 32) {
                return null;
            }

            int quantidadeUns = 0;

            for (int i = 0; i < tamanhoDados; i++) {

                int bit = fluxoBits[inicio + i];

                if (bit != 0 && bit != 1) {
                    return null;
                }

                if (bit == 1) {
                    quantidadeUns++;
                }

                resultado.add(bit);
            }

            int bitParidade = fluxoBits[inicio + tamanhoDados];

            if (bitParidade != 0 && bitParidade != 1) {
                return null;
            }

            if (bitParidade == 1) {
                quantidadeUns++;
            }

            boolean valido = paridadePar
                    ? quantidadeUns % 2 == 0
                    : quantidadeUns % 2 != 0;

            if (!valido) {
                return null;
            }

            inicio += tamanhoDados + 1;
        }

        return converterParaArray(resultado);
    }

    // =========================================================
    // CRC
    // =========================================================

    private int[] removerCRC(int[] fluxoBits) {

        ArrayList<Integer> resultado = new ArrayList<>();

        int inicio = 0;

        while (inicio < fluxoBits.length) {

            int restante = fluxoBits.length - inicio;

            int tamanhoDados;

            if (restante > BLOCO_CRC + TAMANHO_CRC) {
                tamanhoDados = BLOCO_CRC;
            } else {
                tamanhoDados = restante - TAMANHO_CRC;
            }

            if (tamanhoDados < 1 || tamanhoDados > BLOCO_CRC) {
                return null;
            }

            int tamanhoQuadro = tamanhoDados + TAMANHO_CRC;
            int[] copia = new int[tamanhoQuadro];

            System.arraycopy(
                    fluxoBits, inicio,
                    copia, 0,
                    tamanhoQuadro);

            for (int bit : copia) {
                if (bit != 0 && bit != 1) {
                    return null;
                }
            }

            // Divisão módulo 2.
            for (int i = 0; i < tamanhoDados; i++) {

                if (copia[i] == 1) {

                    for (int j = 0;
                            j < POLINOMIO_CRC.length(); j++) {

                        copia[i + j] ^=
                                POLINOMIO_CRC.charAt(j) - '0';
                    }
                }
            }

            // O resto precisa ser completamente zero.
            for (int i = tamanhoDados;
                    i < tamanhoQuadro; i++) {

                if (copia[i] != 0) {
                    return null;
                }
            }

            // Recupera somente os dados.
            for (int i = 0; i < tamanhoDados; i++) {
                resultado.add(fluxoBits[inicio + i]);
            }

            inicio += tamanhoQuadro;
        }

        return converterParaArray(resultado);
    }

    // =========================================================
    // HAMMING ESTENDIDO (SECDED)
    // =========================================================

    private int[] removerHamming(int[] fluxoBits) {

    ArrayList<Integer> resultado = new ArrayList<>();

    int inicio = 0;

    while (inicio < fluxoBits.length) {

        int restante = fluxoBits.length - inicio;

        int tamanhoBloco;

        // Blocos completos possuem 32 dados + 6 paridades + 1 global.
        if (restante >= 39) {
            tamanhoBloco = 39;
        } else {
            tamanhoBloco = restante;
        }

        int[] bloco = new int[tamanhoBloco];

        System.arraycopy(
                fluxoBits, inicio,
                bloco, 0,
                tamanhoBloco);

        ArrayList<Boolean> dados =
                decodificarHamming(bloco);

        if (dados == null) {
            return null;
        }

        for (boolean bit : dados) {
            resultado.add(bit ? 1 : 0);
        }

        inicio += tamanhoBloco;
    }

    return converterParaArray(resultado);
}


    private ArrayList<Boolean> decodificarHamming(int[] bloco) {

    if (bloco == null || bloco.length < 4 || bloco.length > 39) {
        return null;
    }

    int quantidadeDados = -1;
    int quantidadeParidades = -1;

    // Descobre quantos bits de dados existem no bloco.
    for (int n = 1; n <= 32; n++) {

        int k = calcularQuantidadeParidades(n);

        if (n + k + 1 == bloco.length) {
            quantidadeDados = n;
            quantidadeParidades = k;
            break;
        }
    }

    if (quantidadeDados == -1) {
        return null;
    }

    int tamanhoCodigo = quantidadeDados + quantidadeParidades;

    int[] codigo = new int[tamanhoCodigo + 1];

    for (int i = 1; i <= tamanhoCodigo; i++) {

        int bit = bloco[i - 1];

        if (bit != 0 && bit != 1) {
            return null;
        }

        codigo[i] = bit;
    }

    int paridadeGlobal = bloco[tamanhoCodigo];

    if (paridadeGlobal != 0 && paridadeGlobal != 1) {
        return null;
    }

    // Calcula a síndrome.
    int sindrome = 0;

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

        if (quantidadeUns % 2 != 0) {
            sindrome |= paridade;
        }
    }

    // Verifica a paridade global.
    int quantidadeUns = paridadeGlobal;

    for (int i = 1; i <= tamanhoCodigo; i++) {

        if (codigo[i] == 1) {
            quantidadeUns++;
        }
    }

    boolean erroGlobal = quantidadeUns % 2 != 0;

    if (sindrome != 0 && erroGlobal) {

        // Erro simples em uma posição do código.
        if (sindrome > tamanhoCodigo) {
            return null;
        }

        codigo[sindrome] ^= 1;

    } else if (sindrome == 0 && erroGlobal) {

        // Erro somente no bit de paridade global.
        // Os dados permanecem corretos.

    } else if (sindrome != 0) {

        // Erro duplo detectado, sem possibilidade de correção.
        return null;
    }

    // Recupera somente os bits de dados.
    ArrayList<Boolean> dados = new ArrayList<>();

    for (int posicao = 1;
            posicao <= tamanhoCodigo; posicao++) {

        if (!ehPotenciaDeDois(posicao)) {
            dados.add(codigo[posicao] == 1);
        }
    }

    if (dados.size() != quantidadeDados) {
        return null;
    }

    return dados;
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
    // DESENQUADRAMENTO CONVENCIONAL
    // =========================================================

    public void camadaDeEnlaceDadosReceptoraDesenquadro(
            int[] fluxoBits, int tipoEnquadramento) {

        if (fluxoBits == null) {
            mensagemDeErroDeTransmissao();
            return;
        }

        ArrayList<Character> letras;

        switch (tipoEnquadramento) {

            case 0:
                letras = desenquadramento.deframingContagemDeCaracteres(
                        fluxoBits);
                break;

            case 1:
                letras = desenquadramento.deframingInsercaoDeBytes(
                        fluxoBits);
                break;

            case 2:
                letras = desenquadramento.deframingBitStuffing(
                        fluxoBits);
                break;

            default:
                mensagemDeErroDeTransmissao();
                return;
        }

        if (letras == null) {
            mensagemDeErroDeTransmissao();
            return;
        }

        camadaAplicacaoReceptora.juntarCaracteres(letras);
    }

    // =========================================================
    // VIOLAÇÃO DA CAMADA FÍSICA
    // =========================================================

    public void camadaDeEnlaceDadosReceptoraDesenquadro(
            ArrayList<Integer> fluxoSimbolos,
            int tipoControleDeErro) {

        if (fluxoSimbolos == null || fluxoSimbolos.isEmpty()) {
            mensagemDeErroDeTransmissao();
            return;
        }

        ArrayList<Integer> fluxoValidado = new ArrayList<>();

        int posicao = 0;

        while (posicao < fluxoSimbolos.size()) {

            if (fluxoSimbolos.get(posicao) != 2) {
                mensagemDeErroDeTransmissao();
                return;
            }

            // Delimitador inicial.
            fluxoValidado.add(2);
            posicao++;

            ArrayList<Boolean> quadro = new ArrayList<>();

            while (posicao < fluxoSimbolos.size() &&
                    fluxoSimbolos.get(posicao) != 2) {

                int simbolo = fluxoSimbolos.get(posicao++);

                if (simbolo != 0 && simbolo != 1) {
                    mensagemDeErroDeTransmissao();
                    return;
                }

                quadro.add(simbolo == 1);
            }

            if (posicao >= fluxoSimbolos.size()) {
                mensagemDeErroDeTransmissao();
                return;
            }

            ArrayList<Boolean> dados;

            switch (tipoControleDeErro) {

                case 0:
                    dados = removerParidadeQuadro(quadro, true);
                    break;

                case 1:
                    dados = removerParidadeQuadro(quadro, false);
                    break;

                case 2:
                    dados = removerCRCQuadro(quadro);
                    break;

                case 3:
                    dados = removerHammingQuadro(quadro);
                    break;

                default:
                    dados = null;
            }

            if (dados == null) {
                mensagemDeErroDeTransmissao();
                return;
            }

            for (boolean bit : dados) {
                fluxoValidado.add(bit ? 1 : 0);
            }

            // Delimitador final.
            fluxoValidado.add(2);
            posicao++;
        }

        ArrayList<Character> letras =
                desenquadramento.desenquadramentoViolacaoCamadaFisica(
                        fluxoValidado);

        if (letras == null) {
            mensagemDeErroDeTransmissao();
            return;
        }

        camadaAplicacaoReceptora.juntarCaracteres(letras);
    }

    private ArrayList<Boolean> removerParidadeQuadro(
            ArrayList<Boolean> quadro, boolean paridadePar) {

        if (quadro == null || quadro.size() < 2 ||
                quadro.size() > 33) {
            return null;
        }

        int quantidadeDados = quadro.size() - 1;

        if (quantidadeDados < 8 ||
                quantidadeDados > 32 ||
                quantidadeDados % 8 != 0) {
            return null;
        }

        int uns = 0;

        for (boolean bit : quadro) {
            if (bit) {
                uns++;
            }
        }

        boolean valido = paridadePar
                ? uns % 2 == 0
                : uns % 2 != 0;

        if (!valido) {
            return null;
        }

        return new ArrayList<>(
                quadro.subList(0, quantidadeDados));
    }

    private ArrayList<Boolean> removerCRCQuadro(
            ArrayList<Boolean> quadro) {

        if (quadro == null || quadro.size() < 33 ||
                quadro.size() > 64) {
            return null;
        }

        int tamanhoDados = quadro.size() - TAMANHO_CRC;

        int[] bloco = new int[quadro.size()];

        for (int i = 0; i < quadro.size(); i++) {
            bloco[i] = quadro.get(i) ? 1 : 0;
        }

        if (!validarCRCBloco(bloco, tamanhoDados)) {
            return null;
        }

        return new ArrayList<>(
                quadro.subList(0, tamanhoDados));
    }

    private boolean validarCRCBloco(
            int[] bloco, int tamanhoDados) {

        int[] copia = bloco.clone();

        for (int i = 0; i < tamanhoDados; i++) {

            if (copia[i] == 1) {

                for (int j = 0;
                        j < POLINOMIO_CRC.length(); j++) {

                    copia[i + j] ^=
                            POLINOMIO_CRC.charAt(j) - '0';
                }
            }
        }

        for (int i = tamanhoDados; i < copia.length; i++) {
            if (copia[i] != 0) {
                return false;
            }
        }

        return true;
    }

    private ArrayList<Boolean> removerHammingQuadro(
        ArrayList<Boolean> quadro) {

    if (quadro == null) {
        return null;
    }

    int[] bloco = new int[quadro.size()];

    for (int i = 0; i < quadro.size(); i++) {
        bloco[i] = quadro.get(i) ? 1 : 0;
    }

    return decodificarHamming(bloco);
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private int[] converterParaArray(ArrayList<Integer> lista) {

        int[] resultado = new int[lista.size()];

        for (int i = 0; i < lista.size(); i++) {
            resultado[i] = lista.get(i);
        }

        return resultado;
    }

    public void mensagemDeErroDeTransmissao() {
        camadaFisicaTransmissora.mensagemDeErroDeTransmissao();
    }
}
