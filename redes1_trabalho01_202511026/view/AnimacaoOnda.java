/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */
package view;

import javafx.scene.shape.Polyline;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import java.util.ArrayList;

/* Para essa animação pensei em um array que representasse
* as coordenadas de um ponto em cada uma de suas posições
* as oscilações dessa linha se dão pela alteração da primeira posição
* do array que tem em seguida o conteudo de cada posição sendo passado
* para a proxima sucessivamente. Cada alteração é passada para um frame
* da animação.
*/

public class AnimacaoOnda {
  private final ArrayList<Point2D> pontos = new ArrayList<>();
  private final Polyline onda = new Polyline();

  // Configurações do Sinal
  private final double X_INICIAL = 200.0;
  private final double X_FINAL = 800.0;
  private final double VELOCIDADE_X = 5.0; // Espaçamento horizontal entre pontos
  
  private final int TOTAL_PONTOS = (int) ((X_FINAL - X_INICIAL) / VELOCIDADE_X);

  // Alturas dos níveis lógicos na tela
  private final double Y_BIT_ZERO = 500.0; 
  private final double Y_BIT_UM = 450.0;   

  // Controle de tempo para a troca de bits
  private int frameContador = 0;
  private int posicaoContador = 0;
  private final int FRAMES_POR_BIT = 10;
  private double nivelAtualY = Y_BIT_ZERO; // Começa em 0
  
  private AnimationTimer timer;
  private ArrayList<Boolean> fluxoBrutoDeBits = new ArrayList<>();
  
  /* ***************************************************************
  * Metodo: AnimacaoOnda
  * Funcao: construtor que define regras da animacao da impressao da linha na tela
  * Parametros: vazio
  * Retorno: vazio
*************************************************************** */
  public AnimacaoOnda(){
    linhaSemTransmissao();
    timer = new AnimationTimer() {
      private long lastUpdate = 0;
      @Override
      public void handle(long now) {
        if (now - lastUpdate >= 20_000_000) {
          frameContador++;
          // A cada X frames, pega o boolean da proxima posicao do array
          if (frameContador >= FRAMES_POR_BIT) {
            boolean bitUm = (posicaoContador < fluxoBrutoDeBits.size()) ? fluxoBrutoDeBits.get(posicaoContador) : false; 
            nivelAtualY = bitUm ? Y_BIT_UM : Y_BIT_ZERO;
            frameContador = 0; // Reseta o contador do pulso
            posicaoContador++;
          }

          // O novo ponto sempre entra na posição X fixa da esquerda (0) com a altura do bit atual
          Point2D novoBitPonto = new Point2D(X_INICIAL, nivelAtualY);

          // Executa a função de mover os dados e atualizar a tela
          atualizarSinal(novoBitPonto);
          lastUpdate = now ;
        }
      }
    };
  }
  
  /* ***************************************************************
  * Metodo: getOnda
  * Funcao: retorna linha a ser impressa na tela
  * Parametros: vazio
  * Retorno: linha a ser impressa na tela
  *************************************************************** */
  public Polyline getOnda(){
    return onda;
  }
  
  /* ***************************************************************
  * Metodo: setFluxoBrutoDeBits
  * Funcao: atribuir fluxo de bits a ser transformado em pontos e reniciar contador de posicoes
  * Parametros: mensagem codificada
  * Retorno: vazio
  *************************************************************** */
  public void setFluxoBrutoDeBits(ArrayList<Boolean> fluxoBrutoDeBits){
    this.fluxoBrutoDeBits = fluxoBrutoDeBits;
    posicaoContador = 0;
    animacaoStart();
  }
  
  /* ***************************************************************
  * Metodo: linhaSemTransmissao
  * Funcao: definir uma linha reta para inicio de animacao
  * Parametros: vazio
  * Retorno: vazio
  *************************************************************** */
  public void linhaSemTransmissao(){
    // Apenas pontos retos na base
    for (int i = 0; i < TOTAL_PONTOS; i++) {
      pontos.add(new Point2D(X_INICIAL + (i * VELOCIDADE_X), Y_BIT_ZERO)); //Lembrar de após terminado passar o fluxoBrutoDeBits pro array chamar esse metodo
    }
  }
  
  /* ***************************************************************
  * Metodo: animacaoStart
  * Funcao: inicia impressao de linha na tela
  * Parametros: vazio
  * Retorno: vazio
  *************************************************************** */
  public void animacaoStart(){
    timer.start();
  }
  
  /**
  * Move o sinal para a direita. 
  * O conteúdo passa para o próximo, mas o X de cada posição é recalculado 
  * para manter a linha fixa andando para o lado.
  */
  public void atualizarSinal(Point2D novoPontoInicial) {
    // 1. Move os valores de Y de trás para frente
    for (int i = pontos.size() - 1; i > 0; i--) {
      // Pega o Y do ponto anterior, mas mantém o X fixo da sua própria coluna, 'passa o ponto atual para trás'
      double meuX = pontos.get(i).getX();
      double yDoAnterior = pontos.get(i - 1).getY();
      pontos.set(i, new Point2D(meuX, yDoAnterior));
    }

    // 2. Atualiza a primeira posição com o novo bit gerado
    pontos.set(0, new Point2D(X_INICIAL, novoPontoInicial.getY()));

    // 3. Renderiza o resultado na Polyline
    onda.getPoints().clear();
    for (Point2D p : pontos) {
      onda.getPoints().addAll(p.getX(), p.getY());
    }
  }
}
