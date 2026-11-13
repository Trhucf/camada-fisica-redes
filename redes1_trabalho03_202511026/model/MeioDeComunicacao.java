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
import java.util.Random;

public class MeioDeComunicacao {
  private AnimacaoOnda animacaoOnda;
  private CamadaFisicaReceptora camadaFisicaReceptora;
  private ArrayList<Boolean> fluxoBrutoDeBitsB;
  private Random random = new Random();
    
  public MeioDeComunicacao(AnimacaoOnda animacaoOnda, CamadaFisicaReceptora camadaFisicaReceptora){
    this.animacaoOnda = animacaoOnda; 
    this.camadaFisicaReceptora = camadaFisicaReceptora;
  }
  
  public void transferencia(ArrayList<Boolean> fluxoBrutoDeBitsA, int escolha, int tipoEnquadramento, int tipoControleDeErro, int probabilidadeErro){
    if (fluxoBrutoDeBitsA == null || fluxoBrutoDeBitsA.isEmpty()) {
      return;
    }

    if (probabilidadeErro < 0 || probabilidadeErro > 100) {
    throw new IllegalArgumentException(
            "A probabilidade de erro deve estar entre 0 e 100.");
    }
    
    fluxoBrutoDeBitsB = new ArrayList<Boolean>();
    
    // 1. Sorteia SE o erro vai ocorrer nesta transmissão
    boolean erro = random.nextInt(100) < probabilidadeErro;
    
    int posicaoDoErro = -1;
    if (erro) {
        posicaoDoErro = random.nextInt(fluxoBrutoDeBitsA.size());
    }
    
    for(int i = 0; i < fluxoBrutoDeBitsA.size(); i++){
      boolean bitAtual = fluxoBrutoDeBitsA.get(i);
      
      if(i == posicaoDoErro){
        fluxoBrutoDeBitsB.add((bitAtual == false) ? true : false);
      } else {
        fluxoBrutoDeBitsB.add(fluxoBrutoDeBitsA.get(i));
      }
      
    }
    animacaoOnda.setFluxoBrutoDeBits(fluxoBrutoDeBitsB);
    camadaFisicaReceptora.tipoDeDecodificacao(fluxoBrutoDeBitsB, escolha, tipoEnquadramento, tipoControleDeErro);
  }
}
