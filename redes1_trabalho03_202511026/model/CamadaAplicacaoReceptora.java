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

public class CamadaAplicacaoReceptora{
  private AplicacaoReceptora aplicacaoReceptora;

  public CamadaAplicacaoReceptora(AplicacaoReceptora aplicacaoReceptora){
   this.aplicacaoReceptora = aplicacaoReceptora;
  }
  
  public void juntarCaracteres(ArrayList<Character> letras){
    StringBuilder mensagem = new StringBuilder();
    for (char c : letras) {
      mensagem.append(c);
    }
    String resultado = mensagem.toString();
    aplicacaoReceptora.passarMensagem(resultado);
  }
}

