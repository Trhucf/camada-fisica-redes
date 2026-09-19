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

public class CamadaFisicaTransmissora {
  private MeioDeComunicacao meioDeComunicacao;
  private ArrayList<Boolean> fluxoBrutoDeBits = new ArrayList<Boolean>();
  
  public CamadaFisicaTransmissora(MeioDeComunicacao meioDeComunicacao){
    this.meioDeComunicacao = meioDeComunicacao;
  }
  
  /* ***************************************************************
  * Metodo: tipoDeCodificacao
  * Funcao: passar array de inteiros para ser codificado da forma escolhida
  * Parametros: mensagem (o que vai ser codificado), escolha(como vai ser codificado)
  * Retorno: vazio
  *************************************************************** */
  public void tipoDeCodificacao(ArrayList<Integer> quadro, int escolha){
    fluxoBrutoDeBits.clear();
    switch(escolha){
      case 0:
        codificacaoBinaria(quadro);
        break;
      case 1:
        codificacaoManchester(quadro);
        break;
      case 2:
        codificacaoManchesterDiferencial(quadro);
        break;
    }
    if(fluxoBrutoDeBits != null)
      meioDeComunicacao.transferencia(fluxoBrutoDeBits, escolha);
  }
  
  public void codificacaoBinaria(ArrayList<Integer> quadro){
    int mascara = 1 << 31;
    for(int i = 0; i < quadro.size(); i++){ //percorre array de inteiros comprimidos
      int operando1 = quadro.get(i);
      for(int bit = 1; bit <= 32; bit++){ //percorre cada um dos bits de um inteiro
        fluxoBrutoDeBits.add((operando1 & mascara) == 0 ? false : true);
        operando1 <<= 1;
      } 
    }
  }
  
  public void codificacaoManchester(ArrayList<Integer> quadro){
    int mascara = 1 << 31;
    for(int i = 0; i < quadro.size(); i++){ //percorre array de inteiros comprimidos
      int operando1 = quadro.get(i);
      for(int bit = 1; bit <= 32; bit++){ //percorre cada um dos bits de um inteiro
        if((operando1 & mascara) == 0){
          sinalBaixoAlto();
          
        } else {
          sinalAltoBaixo();
        
        }
        operando1 <<= 1;
      } 
    }
  }
  
  public void codificacaoManchesterDiferencial(ArrayList<Integer> quadro) {
    int mascara = 1 << 31;

    // Nivel inicial do sinal
    boolean nivelAtual = false;

    for (int i = 0; i < quadro.size(); i++) {
      int operando1 = quadro.get(i);

      for (int bit = 1; bit <= 32; bit++) {

        boolean bitAtual = (operando1 & mascara) != 0;

        // 0 = transição no início
        if (!bitAtual) {
          nivelAtual = !nivelAtual;
        }

        // Primeira metade do bit
        boolean primeiroNivel = nivelAtual;

        // Transição obrigatória no meio
        nivelAtual = !nivelAtual;

        // Segunda metade do bit
        boolean segundoNivel = nivelAtual;

        if (!primeiroNivel && segundoNivel) {
          sinalBaixoAlto(); // 0 -> 1
        } else {
          sinalAltoBaixo(); // 1 -> 0
        }

        operando1 <<= 1;
      }
    }
  }


  
  public void sinalAltoBaixo(){
    fluxoBrutoDeBits.add(true);
    fluxoBrutoDeBits.add(false);
  }
  
  public void sinalBaixoAlto(){
    fluxoBrutoDeBits.add(false);
    fluxoBrutoDeBits.add(true);
  }
}
