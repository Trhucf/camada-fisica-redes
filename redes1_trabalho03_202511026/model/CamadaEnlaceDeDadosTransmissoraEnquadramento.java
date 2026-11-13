/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 29/09/2026
* Ultima alteracao.: 08/11/2026
* Nome.............: Enlace de Dados
* Funcao...........: Enquadramento de dados para transmissão
*************************************************************** */

package model;

import java.util.ArrayList;

public class CamadaEnlaceDeDadosTransmissoraEnquadramento {

  public CamadaEnlaceDeDadosTransmissoraEnquadramento() {
  }

  // ENQUADRAMENTO POR CONTAGEM DE CARACTERES

  public ArrayList<Boolean> contagemDeCaracteres(boolean[] bits) {

    ArrayList<Boolean> quadro = new ArrayList<>();

    if (bits == null || bits.length == 0) {
      return quadro;
    }

    final int TAMANHO_CARACTERE = 8;
    final int TAMANHO_MAX_PAYLOAD = 4 * TAMANHO_CARACTERE;

    if (bits.length % TAMANHO_CARACTERE != 0) {
      throw new IllegalArgumentException("A quantidade de bits deve ser multipla de 8.");
    }

    int posicao = 0;

    while (posicao < bits.length) {

      int bitsRestantes = bits.length - posicao;

      int bitsQuadro = Math.min(bitsRestantes, TAMANHO_MAX_PAYLOAD);

      int tamanhoAtual = (bitsQuadro / TAMANHO_CARACTERE) + 1;

      // Cabeçalho de 8 bits.
      adicionarByte(quadro, tamanhoAtual);

      // Payload.
      for (int i = 0; i < bitsQuadro; i++) {
        quadro.add(bits[posicao++]);
      }
    }

    return quadro;
  }

  // ENQUADRAMENTO POR INSERÇÃO DE BYTES

  public ArrayList<Boolean> insercaoDeBytes(boolean[] bits) {

    ArrayList<Boolean> quadro = new ArrayList<>();

    if (bits == null || bits.length == 0) {
      return quadro;
    }

    if (bits.length % 8 != 0) {
      throw new IllegalArgumentException("A quantidade de bits deve ser multipla de 8");
    }

    final int FLAG = 35;   // '#'
    final int ESCAPE = 38; // '&'

    int posicao = 0;

    // Inicia o primeiro quadro.
    adicionarByte(quadro, FLAG);

    while (posicao < bits.length) {

      int caractere = lerByte(bits, posicao);

      posicao += 8;

      // Escapa os caracteres reservados.
      if (caractere == FLAG || caractere == ESCAPE) {
        adicionarByte(quadro, ESCAPE);
      }

      adicionarByte(quadro, caractere);

      // O espaço encerra o quadro após ser incluído no payload.
      if (caractere == 32) {

        adicionarByte(quadro, FLAG);

        // Inicia o próximo quadro.
        adicionarByte(quadro, FLAG);
      }
    }

    // Encerra o último quadro.
    adicionarByte(quadro, FLAG);

    return quadro;
  }

  // LÊ UM BYTE NO FORMATO MSB PRIMEIRO.

  private int lerByte(boolean[] bits, int inicio) {

    int valor = 0;

    for (int i = 0; i < 8; i++) {

      valor = (valor << 1) | (bits[inicio + i] ? 1 : 0);
    }

    return valor;
  }

  // ADICIONA UM BYTE AO QUADRO.

  private void adicionarByte(ArrayList<Boolean> quadro, int valor) {

    for (int i = 7; i >= 0; i--) {

      quadro.add(((valor >> i) & 1) == 1);
    }
  }

  // ENQUADRAMENTO POR INSERÇÃO DE BITS (BIT STUFFING)

  public ArrayList<Boolean> bitStuffing(boolean[] bits) {

    ArrayList<Boolean> quadro = new ArrayList<>();

    if (bits == null || bits.length == 0) {
      return quadro;
    }

    final int TAMANHO_MAX_PAYLOAD = 32;

    for (int i = 0; i < bits.length; i += TAMANHO_MAX_PAYLOAD) {

      int fim = Math.min(i + TAMANHO_MAX_PAYLOAD, bits.length);

      // Flag de início.
      adicionarFlag(quadro);

      // Payload com bit stuffing.
      processarQuadro(bits, i, fim, quadro);

      // Flag de encerramento.
      adicionarFlag(quadro);
    }

    return quadro;
  }

  private void processarQuadro(boolean[] bits, int inicio, int fim, ArrayList<Boolean> quadro) {

    int contadorUns = 0;

    for (int i = inicio; i < fim; i++) {

      boolean bit = bits[i];

      quadro.add(bit);

      if (bit) {

        contadorUns++;

        if (contadorUns == 5) {

          // Insere zero após cinco uns consecutivos.
          quadro.add(false);

          contadorUns = 0;
        }

      } else {
        contadorUns = 0;
      }
    }
  }

  // FLAG 01111110

  private void adicionarFlag(ArrayList<Boolean> fluxo) {

    fluxo.add(false);

    for (int i = 0; i < 6; i++) {
      fluxo.add(true);
    }

    fluxo.add(false);
  }

  // ENQUADRAMENTO POR VIOLAÇÃO DA CAMADA FÍSICA

  public ArrayList<Integer> violacaoCamadaFisica(boolean[] bits) {

    ArrayList<Integer> fluxoSimbolos = new ArrayList<>();

    if (bits == null || bits.length == 0) {
      return fluxoSimbolos;
    }

    final int TAMANHO_MAX_PAYLOAD = 32;
    final int FLAG_VIOLACAO = 2;

    for (int i = 0; i < bits.length; i += TAMANHO_MAX_PAYLOAD) {

      int fim = Math.min(i + TAMANHO_MAX_PAYLOAD, bits.length);

      // Delimitador de início.
      fluxoSimbolos.add(FLAG_VIOLACAO);

      // Payload.
      for (int j = i; j < fim; j++) {

        fluxoSimbolos.add(bits[j] ? 1 : 0);
      }

      // Delimitador de encerramento.
        fluxoSimbolos.add(FLAG_VIOLACAO);
      }

    return fluxoSimbolos;
  }
}
