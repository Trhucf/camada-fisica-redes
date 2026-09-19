/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package model;

import java.util.ArrayList;

public class CamadaAplicacaoReceptora{
  private AplicacaoReceptora aplicacaoReceptora;

  public CamadaAplicacaoReceptora(AplicacaoReceptora aplicacaoReceptora){
   this.aplicacaoReceptora = aplicacaoReceptora;
  }
  
  public void juntarCaracteres(ArrayList<Integer> codigosAscii){
    ArrayList<Character> letras = new ArrayList<Character>();
  
    for(int i = 0; i < codigosAscii.size(); i++){
      letras.add((char) codigosAscii.get(i).intValue());
    }
    StringBuilder mensagem = new StringBuilder();
    for (char c : letras) {
      mensagem.append(c);
    }
    String resultado = mensagem.toString();
    aplicacaoReceptora.passarMensagem(resultado);
  }
}

