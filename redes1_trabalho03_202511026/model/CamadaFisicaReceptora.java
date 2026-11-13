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

public class CamadaFisicaReceptora {

    private CamadaEnlaceDadosReceptora camadaEnlaceDadosReceptora;
    private int[] fluxoBits;

    public CamadaFisicaReceptora(
            CamadaEnlaceDadosReceptora camadaEnlaceDadosReceptora) {

        this.camadaEnlaceDadosReceptora = camadaEnlaceDadosReceptora;
    }

    public void tipoDeDecodificacao(ArrayList<Boolean> fluxoBrutoDeBits, int escolha, int tipoEnquadramento, int tipoControleDeErro) {

        fluxoBits = null;

        if (fluxoBrutoDeBits == null) {
            return;
        }

        switch (escolha) {

            case 0:
                decodificacaoBinaria(fluxoBrutoDeBits);
                break;

            case 1:
                decodificacaoManchester(fluxoBrutoDeBits);
                break;

            case 2:
                decodificacaoManchesterDiferencial(fluxoBrutoDeBits);
                break;

            case 3:
                decodificacaoManchesterComViolacao(fluxoBrutoDeBits, tipoControleDeErro);
                return;

            default:
                throw new IllegalArgumentException(
                        "Tipo de codificação inválido.");
        }

        if (fluxoBits != null) {
          camadaEnlaceDadosReceptora.camadaDeEnlaceDadosReceptoraControleDeErro(fluxoBits, tipoEnquadramento, tipoControleDeErro);
        }
    }

    // ------------------------------------------------------------
    // CODIFICACAO BINARIA
    // ------------------------------------------------------------

    public void decodificacaoBinaria(
            ArrayList<Boolean> fluxoBrutoDeBits) {

        if (fluxoBrutoDeBits == null) {
            fluxoBits = null;
            return;
        }

        fluxoBits = new int[fluxoBrutoDeBits.size()];

        for (int i = 0; i < fluxoBrutoDeBits.size(); i++) {

            Boolean bit = fluxoBrutoDeBits.get(i);

            if (bit == null) {
                fluxoBits = null;
                return;
            }

            fluxoBits[i] = bit ? 1 : 0;
        }
    }

    // ------------------------------------------------------------
    // CODIFICACAO MANCHESTER
    // ------------------------------------------------------------

    public void decodificacaoManchester(
            ArrayList<Boolean> fluxoBrutoDeBits) {

        if (fluxoBrutoDeBits == null ||
                fluxoBrutoDeBits.size() % 2 != 0) {

            fluxoBits = null;
            return;
        }

        fluxoBits = new int[fluxoBrutoDeBits.size() / 2];

        int indexDecodificado = 0;

        for (int i = 0; i < fluxoBrutoDeBits.size(); i += 2) {

            Boolean primeiro = fluxoBrutoDeBits.get(i);
            Boolean segundo = fluxoBrutoDeBits.get(i + 1);

            if (primeiro == null || segundo == null) {
                fluxoBits = null;
                return;
            }

            if (!primeiro && segundo) {

                fluxoBits[indexDecodificado++] = 0;

            } else if (primeiro && !segundo) {

                fluxoBits[indexDecodificado++] = 1;

            } else {

                // 00 ou 11: par físico inválido.
                fluxoBits = null;
                return;
            }
        }
    }

    // ------------------------------------------------------------
    // CODIFICACAO MANCHESTER DIFERENCIAL
    // ------------------------------------------------------------

    public void decodificacaoManchesterDiferencial(
            ArrayList<Boolean> fluxoBrutoDeBits) {

        if (fluxoBrutoDeBits == null ||
                fluxoBrutoDeBits.size() % 2 != 0) {

            fluxoBits = null;
            return;
        }

        fluxoBits = new int[fluxoBrutoDeBits.size() / 2];

        int indexDecodificado = 0;

        // Mesmo nível inicial utilizado pela transmissora.
        boolean nivelAnterior = false;

        for (int i = 0; i < fluxoBrutoDeBits.size(); i += 2) {

            Boolean primeiroNivel = fluxoBrutoDeBits.get(i);
            Boolean segundoNivel = fluxoBrutoDeBits.get(i + 1);

            if (primeiroNivel == null || segundoNivel == null) {
                fluxoBits = null;
                return;
            }

            // Deve existir transição no meio do período.
            if (primeiroNivel.equals(segundoNivel)) {

                // 00 ou 11: par físico inválido.
                fluxoBits = null;
                return;
            }

            /*
             * 0 = houve transição no início do período.
             * 1 = não houve transição no início do período.
             */
            boolean bitAtual = (nivelAnterior == primeiroNivel);

            fluxoBits[indexDecodificado++] = bitAtual ? 1 : 0;

            // O segundo nível será a referência do próximo bit.
            nivelAnterior = segundoNivel;
        }
    }

    // ------------------------------------------------------------
    // MANCHESTER COM VIOLACAO DA CAMADA FISICA
    // ------------------------------------------------------------

    public void decodificacaoManchesterComViolacao(ArrayList<Boolean> fluxoBrutoDeBits, int tipoControleDeErro) {

        if (fluxoBrutoDeBits == null ||
                fluxoBrutoDeBits.isEmpty() ||
                fluxoBrutoDeBits.size() % 2 != 0) {

            camadaEnlaceDadosReceptora
                    .mensagemDeErroDeTransmissao();
            return;
        }

        ArrayList<Integer> fluxoSimbolos = new ArrayList<>();

        for (int i = 0; i < fluxoBrutoDeBits.size(); i += 2) {

            Boolean primeiro = fluxoBrutoDeBits.get(i);
            Boolean segundo = fluxoBrutoDeBits.get(i + 1);

            if (primeiro == null || segundo == null) {

                camadaEnlaceDadosReceptora
                        .mensagemDeErroDeTransmissao();
                return;
            }

            if (!primeiro && !segundo) {

                // 00 -> Delimitador por violação.
                fluxoSimbolos.add(2);

            } else if (!primeiro && segundo) {

                // 01 -> Bit 0.
                fluxoSimbolos.add(0);

            } else if (primeiro && !segundo) {

                // 10 -> Bit 1.
                fluxoSimbolos.add(1);

            } else {

                // 11 -> Violação física inválida.
                camadaEnlaceDadosReceptora
                        .mensagemDeErroDeTransmissao();
                return;
            }
        }

        /*
         * Os símbolos são encaminhados para a camada de enlace.
         * O controle de erro deve acontecer antes do desenquadramento.
         */
        camadaEnlaceDadosReceptora.camadaDeEnlaceDadosReceptoraDesenquadro(
        fluxoSimbolos, tipoControleDeErro);
    }
    
    public void mensagemDeErroDeTransmissao() {
    camadaEnlaceDadosReceptora.mensagemDeErroDeTransmissao();
    }
}

