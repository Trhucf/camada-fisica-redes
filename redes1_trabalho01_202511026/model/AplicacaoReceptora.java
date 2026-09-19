/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package model;

import controller.*;

public class AplicacaoReceptora {
  private ControllerCamadaFisicaTransmissora controllerCamadaFisicaTransmissora;
  
  public AplicacaoReceptora(ControllerCamadaFisicaTransmissora controllerCamadaFisicaTransmissora){
    this.controllerCamadaFisicaTransmissora = controllerCamadaFisicaTransmissora;
  }
  
  public void passarMensagem(String mensagem){
    controllerCamadaFisicaTransmissora.exibirMensagem(mensagem);
  }
}
