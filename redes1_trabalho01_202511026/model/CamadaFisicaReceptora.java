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

public class CamadaFisicaReceptora{
  private CamadaAplicacaoReceptora camadaAplicacaoReceptora;
  private ArrayList<Integer> codigosAscii;
  
  public CamadaFisicaReceptora(CamadaAplicacaoReceptora camadaAplicacaoReceptora){
    this.camadaAplicacaoReceptora = camadaAplicacaoReceptora;
  }
  
  public void tipoDeDecodificacao(ArrayList<Boolean> fluxoBrutoDeBits, int escolha){
    switch(escolha){
      case 0:
        decodificacaoBinaria(fluxoBrutoDeBits);
        break;
      case 1:
        decodificacaoManchester(fluxoBrutoDeBits);
        break;
      case 2:
        decodificacaoManchesterDiferencial(fluxoBrutoDeBits);
        break;
    }
    if (codigosAscii != null) {
      camadaAplicacaoReceptora.juntarCaracteres(codigosAscii);
    }
  }
  
  public void decodificacaoBinaria(ArrayList<Boolean> fluxoBrutoDeBits) {
    if (fluxoBrutoDeBits == null) {
        return;
    }
    if (codigosAscii != null) {
      camadaAplicacaoReceptora.juntarCaracteres(codigosAscii);
    }

    this.codigosAscii = new ArrayList<Integer>();

    int caractere = 0;
    int bitCount = 0;

    for (Boolean bit : fluxoBrutoDeBits) {
        // Evita NullPointerException se algum elemento interno da lista for null
        boolean valorBit = (bit != null) && bit;

        // Desloca os bits e insere o novo bit na posição 0
        caractere = (caractere << 1) | (valorBit ? 1 : 0);
        bitCount++;

        // A cada 8 bits completos, adiciona ao atributo da classe
        if (bitCount == 8) {
            this.codigosAscii.add(caractere);
            caractere = 0;
            bitCount = 0;
        }
    }
  }
  
  public void decodificacaoManchester(ArrayList<Boolean> fluxoBrutoDeBits) {
    if (fluxoBrutoDeBits == null) {
        return;
    }

    this.codigosAscii = new ArrayList<Integer>();

    int caractere = 0;
    int bitCount = 0;

    for (int i = 0; i + 1 < fluxoBrutoDeBits.size(); i += 2) {

      boolean primeiro = fluxoBrutoDeBits.get(i);
      boolean segundo = fluxoBrutoDeBits.get(i + 1);

      boolean bit;

      if (!primeiro && segundo) {
        bit = false; // 01 -> 0
      } else if (primeiro && !segundo) {
        bit = true;  // 10 -> 1
      } else {
        // Par Manchester inválido
        continue;
      }

      caractere = (caractere << 1) | (bit ? 1 : 0);
      bitCount++;

      if (bitCount == 8) {
        this.codigosAscii.add(caractere);
        caractere = 0;
        bitCount = 0;
      }
    }
  }

  
  public void decodificacaoManchesterDiferencial(
    ArrayList<Boolean> fluxoBrutoDeBits) {

    if (fluxoBrutoDeBits == null) {
        return;
    }

    this.codigosAscii = new ArrayList<Integer>();

    int caractere = 0;
    int bitCount = 0;

    // Mesmo nivel inicial utilizado pela transmissora
    boolean nivelAnterior = false;

    for (int i = 0; i + 1 < fluxoBrutoDeBits.size(); i += 2) {

      boolean primeiroNivel = fluxoBrutoDeBits.get(i);
      boolean segundoNivel = fluxoBrutoDeBits.get(i + 1);

      boolean bitAtual;

      // 0 = transição no início
      // 1 = sem transição no início
      if (nivelAnterior != primeiroNivel) {
        bitAtual = false;
      } else {
        bitAtual = true;
      }

      caractere = (caractere << 1) | (bitAtual ? 1 : 0);
      bitCount++;

      // O final deste bit será o nível anterior
      // para o próximo bit.
      nivelAnterior = segundoNivel;

      if (bitCount == 8) {
        this.codigosAscii.add(caractere);

        caractere = 0;
        bitCount = 0;
      }
    }
  }

}
