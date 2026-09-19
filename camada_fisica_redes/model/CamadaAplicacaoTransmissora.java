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

public class CamadaAplicacaoTransmissora {
  private final CamadaFisicaTransmissora camadaFisicaTransmissora;
  
  public CamadaAplicacaoTransmissora(CamadaFisicaTransmissora camadaFisicaTransmissora){
    this.camadaFisicaTransmissora = camadaFisicaTransmissora;
  }
  
  public void transformaEmBinario(String mensagem, int escolha) {
    ArrayList<Integer> fluxoBits = new ArrayList<Integer>();
    char[] chars = mensagem.toCharArray(); //converte a mensagem em um array de caracteres

    for (int i = 0; i < chars.length; i += 4) { 
        int resultado = 0; 

        if (i < chars.length) { //Se a proxima posiçao existe no array, entao
            resultado |= (chars[i] & 0xFF) << 24; //desloca os bits do caractere 24 posicoes para a esquerda e opera com ou
        }
        if (i + 1 < chars.length) {
            resultado |= (chars[i + 1] & 0xFF) << 16;
        }
        if (i + 2 < chars.length) {
            resultado |= (chars[i + 2] & 0xFF) << 8;
        }
        if (i + 3 < chars.length) {
            resultado |= (chars[i + 3] & 0xFF);
        }

        fluxoBits.add(resultado); //adiciona os inteiros comprimidos no array
    }

    camadaFisicaTransmissora.tipoDeCodificacao(fluxoBits, escolha);
  }
}
