/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: Camada Fisica
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */


package model;

public class AplicacaoTransmissora {

    private final CamadaAplicacaoTransmissora camadaAplicacaoTransmissora;

    public AplicacaoTransmissora(
            CamadaAplicacaoTransmissora camadaAplicacaoTransmissora) {

        this.camadaAplicacaoTransmissora = camadaAplicacaoTransmissora;
    }

    public void passarMensagem(
            String mensagem,
            int tipoCodificacao,
            int tipoEnquadramento,
            int tipoControleDeErro,
            int probabilidadeErro) {

        if (mensagem == null || mensagem.isEmpty()) {
            return;
        }

        final int TAMANHO_CARACTERE = 8;

        char[] caracteres = mensagem.toCharArray();

        boolean[] bits = new boolean[
                caracteres.length * TAMANHO_CARACTERE];

        int posicao = 0;

        // Converte cada caractere para 8 bits (ASCII).
        for (char caractere : caracteres) {

            if (caractere > 127) {
                throw new IllegalArgumentException(
                        "A mensagem contém caracteres fora do ASCII.");
            }

            for (int j = 7; j >= 0; j--) {

                bits[posicao++] =
                        ((caractere >> j) & 1) == 1;
            }
        }

        camadaAplicacaoTransmissora.camadaDeAplicacaoTransmissora(
                bits,
                tipoCodificacao,
                tipoEnquadramento,
                tipoControleDeErro,
                probabilidadeErro);
    }
}

