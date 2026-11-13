/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: Camada Fisica
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */


package model;

public class CamadaAplicacaoTransmissora {

    private final CamadaEnlaceDadosTransmissora
            camadaEnlaceDadosTransmissora;

    public CamadaAplicacaoTransmissora(
            CamadaEnlaceDadosTransmissora camadaEnlaceDadosTransmissora) {

        this.camadaEnlaceDadosTransmissora =
                camadaEnlaceDadosTransmissora;
    }

    public void camadaDeAplicacaoTransmissora(
            boolean[] bits,
            int tipoCodificacao,
            int tipoEnquadramento,
            int tipoControleDeErro,
            int probabilidadeErro) {

        camadaEnlaceDadosTransmissora.camadaDeEnquadramento(
                bits,
                tipoCodificacao,
                tipoEnquadramento,
                tipoControleDeErro,
                probabilidadeErro);
    }
}

