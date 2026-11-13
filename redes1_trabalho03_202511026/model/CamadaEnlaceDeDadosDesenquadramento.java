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

public class CamadaEnlaceDeDadosDesenquadramento {

    // =========================================================
    // 0 - CONTAGEM DE CARACTERES
    // =========================================================

    public ArrayList<Character> deframingContagemDeCaracteres(
            int[] fluxoBits) {

        if (fluxoBits == null || fluxoBits.length == 0 ||
                fluxoBits.length % 8 != 0) {
            return null;
        }

        ArrayList<Integer> codigosAscii = geraCodigosAscii(fluxoBits);

        if (codigosAscii == null) {
            return null;
        }

        ArrayList<Character> letras = new ArrayList<>();

        int ponteiro = 0;

        while (ponteiro < codigosAscii.size()) {

            // Primeiro byte: tamanho total do quadro.
            int tamanhoQuadro = codigosAscii.get(ponteiro);

            // Um byte de controle + até quatro caracteres.
            if (tamanhoQuadro < 2 || tamanhoQuadro > 5 ||
                    ponteiro + tamanhoQuadro > codigosAscii.size()) {
                return null;
            }

            int fimQuadro = ponteiro + tamanhoQuadro;

            // Recupera os caracteres, ignorando o byte de controle.
            for (int i = ponteiro + 1; i < fimQuadro; i++) {

                int codigo = codigosAscii.get(i);

                if (codigo < 0 || codigo > 255) {
                    return null;
                }

                letras.add((char) codigo);
            }

            ponteiro = fimQuadro;
        }

        return letras;
    }

    // =========================================================
    // 1 - INSERÇÃO DE BYTES
    // =========================================================

    public ArrayList<Character> deframingInsercaoDeBytes(
            int[] fluxoBits) {

        if (fluxoBits == null || fluxoBits.length == 0 ||
                fluxoBits.length % 8 != 0) {
            return null;
        }

        ArrayList<Integer> codigosAscii = geraCodigosAscii(fluxoBits);

        if (codigosAscii == null) {
            return null;
        }

        ArrayList<Character> letras = new ArrayList<>();

        boolean dentroDoQuadro = false;
        boolean escape = false;

        for (int codigo : codigosAscii) {

            if (codigo < 0 || codigo > 255) {
                return null;
            }

            char caractere = (char) codigo;

            if (escape) {

                // O transmissor utiliza & para escapar # e &.
                if (caractere != '#' && caractere != '&') {
                    return null;
                }

                letras.add(caractere);
                escape = false;

            } else if (caractere == '&') {

                if (!dentroDoQuadro) {
                    return null;
                }

                escape = true;

            } else if (caractere == '#') {

                dentroDoQuadro = !dentroDoQuadro;

            } else {

                if (!dentroDoQuadro) {
                    return null;
                }

                letras.add(caractere);
            }
        }

        // O fluxo precisa terminar fora do quadro e sem escape pendente.
        if (dentroDoQuadro || escape) {
            return null;
        }

        return letras;
    }

    // =========================================================
    // 2 - BIT STUFFING
    // =========================================================

    public ArrayList<Character> deframingBitStuffing(
            int[] fluxoBits) {

        if (fluxoBits == null || fluxoBits.length < 16) {
            return null;
        }

        ArrayList<Character> letras = new ArrayList<>();
        ArrayList<Integer> bitsPayload = new ArrayList<>();

        boolean dentroDoQuadro = false;
        int contadorUns = 0;
        int quadrosEncontrados = 0;

        for (int i = 0; i < fluxoBits.length; i++) {

            // Verifica a flag 01111110.
            if (eInicioDeFlag(fluxoBits, i)) {

                if (dentroDoQuadro) {

                    // Final do quadro.
                    if (bitsPayload.isEmpty() ||
                            bitsPayload.size() % 8 != 0 ||
                            bitsPayload.size() > 32) {
                        return null;
                    }

                    converterBits(bitsPayload, letras);

                    bitsPayload.clear();
                    dentroDoQuadro = false;
                    quadrosEncontrados++;

                } else {

                    // Início do quadro.
                    dentroDoQuadro = true;
                }

                contadorUns = 0;

                // Pula os oito bits da flag.
                i += 7;
                continue;
            }

            if (dentroDoQuadro) {

                int bit = fluxoBits[i];

                if (bit != 0 && bit != 1) {
                    return null;
                }

                // Depois de cinco 1s, deve existir um 0 inserido.
                if (contadorUns == 5) {

                    if (bit != 0) {
                        return null;
                    }

                    // Descarta o zero inserido pelo transmissor.
                    contadorUns = 0;
                    continue;
                }

                bitsPayload.add(bit);

                if (bit == 1) {
                    contadorUns++;
                } else {
                    contadorUns = 0;
                }
            } else {
                // Não permite bits soltos entre os quadros.
                return null;
            }
        }

        // Verifica se todos os quadros foram fechados.
        if (dentroDoQuadro || !bitsPayload.isEmpty() ||
                quadrosEncontrados == 0) {
            return null;
        }

        return letras;
    }

    // =========================================================
    // 3 - VIOLAÇÃO DA CAMADA FÍSICA
    // =========================================================

    public ArrayList<Character> desenquadramentoViolacaoCamadaFisica(
            ArrayList<Integer> fluxoSimbolos) {

        if (fluxoSimbolos == null || fluxoSimbolos.isEmpty()) {
            return null;
        }

        ArrayList<Character> letras = new ArrayList<>();
        ArrayList<Integer> bitsPayload = new ArrayList<>();

        boolean dentroDoQuadro = false;
        int quadrosEncontrados = 0;

        final int FLAG_VIOLACAO = 2;

        for (int simbolo : fluxoSimbolos) {

            if (simbolo == FLAG_VIOLACAO) {

                if (dentroDoQuadro) {

                    // Final do quadro.
                    if (bitsPayload.isEmpty() ||
                            bitsPayload.size() % 8 != 0 ||
                            bitsPayload.size() > 32) {
                        return null;
                    }

                    converterBits(bitsPayload, letras);

                    bitsPayload.clear();
                    dentroDoQuadro = false;
                    quadrosEncontrados++;

                } else {

                    // Início do quadro.
                    dentroDoQuadro = true;
                }

            } else if (dentroDoQuadro) {

                if (simbolo != 0 && simbolo != 1) {
                    return null;
                }

                bitsPayload.add(simbolo);

            } else {

                // Símbolo fora de um quadro.
                return null;
            }
        }

        if (dentroDoQuadro || !bitsPayload.isEmpty() ||
                quadrosEncontrados == 0) {
            return null;
        }

        return letras;
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    private ArrayList<Integer> geraCodigosAscii(int[] fluxoBits) {

        ArrayList<Integer> codigosAscii = new ArrayList<>();

        int caractere = 0;
        int bitCount = 0;

        for (int bit : fluxoBits) {

            if (bit != 0 && bit != 1) {
                return null;
            }

            caractere = (caractere << 1) | bit;
            bitCount++;

            if (bitCount == 8) {
                codigosAscii.add(caractere);

                caractere = 0;
                bitCount = 0;
            }
        }

        if (bitCount != 0) {
            return null;
        }

        return codigosAscii;
    }

    private boolean eInicioDeFlag(int[] bits, int index) {

        if (index + 7 >= bits.length) {
            return false;
        }

        return bits[index] == 0 &&
               bits[index + 1] == 1 &&
               bits[index + 2] == 1 &&
               bits[index + 3] == 1 &&
               bits[index + 4] == 1 &&
               bits[index + 5] == 1 &&
               bits[index + 6] == 1 &&
               bits[index + 7] == 0;
    }

    private void converterBits(
            ArrayList<Integer> bits,
            ArrayList<Character> letras) {

        for (int i = 0; i + 7 < bits.size(); i += 8) {

            int caractere = 0;

            for (int j = 0; j < 8; j++) {
                caractere = (caractere << 1) | bits.get(i + j);
            }

            letras.add((char) caractere);
        }
    }
}

