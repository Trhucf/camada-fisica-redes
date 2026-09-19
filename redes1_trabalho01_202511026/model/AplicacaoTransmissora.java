/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package model;

public class AplicacaoTransmissora {
  private final CamadaAplicacaoTransmissora camadaAplicacaoTransmissora;
  
  public AplicacaoTransmissora(CamadaAplicacaoTransmissora camadaAplicacaoTransmissora){
    this.camadaAplicacaoTransmissora = camadaAplicacaoTransmissora;
  }
  
  public void passarMensagem(String mensagem, int escolha){
    camadaAplicacaoTransmissora.transformaEmBinario(mensagem, escolha);
  }
}
