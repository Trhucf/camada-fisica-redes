/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package model;

import view.*;
import java.util.ArrayList;

public class MeioDeComunicacao {
  private AnimacaoOnda animacaoOnda;
  private CamadaFisicaReceptora camadaFisicaReceptora;
  private ArrayList<Boolean> fluxoBrutoDeBitsB;
    
  public MeioDeComunicacao(AnimacaoOnda animacaoOnda, CamadaFisicaReceptora camadaFisicaReceptora){
    this.animacaoOnda = animacaoOnda; 
    this.camadaFisicaReceptora = camadaFisicaReceptora;
  }
  
  public void transferencia(ArrayList<Boolean> fluxoBrutoDeBitsA, int escolha){
    fluxoBrutoDeBitsB = fluxoBrutoDeBitsA;
    animacaoOnda.setFluxoBrutoDeBits(fluxoBrutoDeBitsB);
    camadaFisicaReceptora.tipoDeDecodificacao(fluxoBrutoDeBitsB, escolha);
  }
}
