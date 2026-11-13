/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: Camada Fisica
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package model;

import java.util.ArrayList;

public class CamadaFisicaTransmissora {

    private final MeioDeComunicacao meioDeComunicacao;

    private ArrayList<Boolean> fluxoBrutoDeBits =
        new ArrayList<>();

    public CamadaFisicaTransmissora(
        MeioDeComunicacao meioDeComunicacao
    ) {

        this.meioDeComunicacao = meioDeComunicacao;
    }

    // SELECIONA A CODIFICAÇÃO FÍSICA.

    public void tipoDeCodificacao(ArrayList<Boolean> quadro, int escolha, int tipoEnquadramento, int tipoControleDeErro, int probabilidadeErro) {

        if (quadro == null || quadro.isEmpty()) {
            return;
        }

        fluxoBrutoDeBits.clear();

        switch (escolha) {

            case 0:
                codificacaoBinaria(quadro);
                break;

            case 1:
                codificacaoManchester(quadro);
                break;

            case 2:
                codificacaoManchesterDiferencial(quadro);
                break;

            default:
                throw new IllegalArgumentException(
                    "Tipo de codificacao fisica invalido."
                );
        }

        meioDeComunicacao.transferencia(
            fluxoBrutoDeBits,
            escolha,
            tipoEnquadramento,
            tipoControleDeErro,
            probabilidadeErro
        );
    }

    // CODIFICAÇÃO BINÁRIA.

    public void codificacaoBinaria(ArrayList<Boolean> quadro) {

        for (int i = 0; i < quadro.size(); i++) {

            fluxoBrutoDeBits.add(quadro.get(i));
        }
    }

    // CODIFICAÇÃO MANCHESTER.

    public void codificacaoManchester(ArrayList<Boolean> quadro) {

        for (int i = 0; i < quadro.size(); i++) {

            if (!quadro.get(i)) {

                // 0 = baixo para alto.
                sinalBaixoAlto();

            } else {

                // 1 = alto para baixo.
                sinalAltoBaixo();
            }
        }
    }

    // CODIFICAÇÃO MANCHESTER DIFERENCIAL.

    public void codificacaoManchesterDiferencial(
        ArrayList<Boolean> quadro
    ) {

        // Nível inicial do sinal.
        boolean nivelAtual = false;

        for (int i = 0; i < quadro.size(); i++) {

            boolean bitAtual = quadro.get(i);

            // 0 = transição no início.
            if (!bitAtual) {
                nivelAtual = !nivelAtual;
            }

            // Primeira metade do bit.
            boolean primeiroNivel = nivelAtual;

            // Transição obrigatória no meio.
            nivelAtual = !nivelAtual;

            // Segunda metade do bit.
            boolean segundoNivel = nivelAtual;

            if (!primeiroNivel && segundoNivel) {

                sinalBaixoAlto();

            } else {

                sinalAltoBaixo();
            }
        }
    }

    // CODIFICAÇÃO MANCHESTER COM VIOLAÇÃO.

    public void codificacaoManchesterComViolacao(ArrayList<Integer> fluxoSimbolos, int decodificacao, int desenquadramento, int tipoControleDeErro, int probabilidadeErro) {

        if (fluxoSimbolos == null || fluxoSimbolos.isEmpty()) {
            return;
        }

        fluxoBrutoDeBits = new ArrayList<>();

        for (int simbolo : fluxoSimbolos) {

            if (simbolo == 0) {

                // Bit 0: transição válida (01).
                fluxoBrutoDeBits.add(false);
                fluxoBrutoDeBits.add(true);

            } else if (simbolo == 1) {

                // Bit 1: transição válida (10).
                fluxoBrutoDeBits.add(true);
                fluxoBrutoDeBits.add(false);

            } else if (simbolo == 2) {

                // Violação: ausência de transição (00).
                fluxoBrutoDeBits.add(false);
                fluxoBrutoDeBits.add(false);

            } else {

                throw new IllegalArgumentException(
                    "Simbolo invalido no fluxo de violacao."
                );
            }
        }

        // Transmite os sinais pelo meio físico.
        meioDeComunicacao.transferencia(
            fluxoBrutoDeBits,
            decodificacao,
            desenquadramento,
            tipoControleDeErro,
            probabilidadeErro
        );
    }

    // SINAL BAIXO -> ALTO.

    public void sinalBaixoAlto() {

        fluxoBrutoDeBits.add(false);
        fluxoBrutoDeBits.add(true);
    }

    // SINAL ALTO -> BAIXO.

    public void sinalAltoBaixo() {

        fluxoBrutoDeBits.add(true);
        fluxoBrutoDeBits.add(false);
    }
}
