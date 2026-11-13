/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class TransmissorReceptor {
  private final TextField input;
  private final TextField exibicao;
  private final Line linhaHorizontal = new Line(200, 500, 800, 500);
  private final Line linhaVerticalE = new Line(200, 325, 200, 500);
  private final Line linhaVerticalD = new Line(800, 325, 800, 500);
    
  private final ToggleGroup group = new ToggleGroup();
  private final ToggleGroup groupE = new ToggleGroup();
  private final ToggleGroup groupC = new ToggleGroup();

  private final RadioButton buttonC1;
  private final RadioButton buttonC2;
  private final RadioButton buttonC3;
  private final RadioButton buttonC4;

  private final Label labelControleErro;
    
  private final RadioButton button1;
  private final RadioButton button2;
  private final RadioButton button3;
    
  private final RadioButton buttonE1;
  private final RadioButton buttonE2;
  private final RadioButton buttonE3;
  private final RadioButton buttonE4;
    
  private final Button enviar;

  private final Label labelCodificacao;
  private final Label labelEnquadramento;
  private final Label labelProbabilidade;
  private final Slider sliderErro;
  private final HBox menuBox;

  public TransmissorReceptor() {

    input = new TextField();
    input.setPromptText("Digite Algo...");
    input.setLayoutX(180);
    input.setLayoutY(160);

    exibicao = new TextField();
    exibicao.setPromptText("Mensagem será exibida");
    exibicao.setLayoutX(690);
    exibicao.setLayoutY(160);
    exibicao.setEditable(false);
    exibicao.setFocusTraversable(false);

    // --- BOTOES DE CODIFICACAO ---
    labelCodificacao = new Label("Tipo de Codificação");
    labelCodificacao.setStyle("-fx-font-weight: bold;");

    button1 = new RadioButton("Binário");
    button1.setToggleGroup(group);
    button1.setSelected(true);

    button2 = new RadioButton("Manchester");
    button2.setToggleGroup(group);

    button3 = new RadioButton("Manchester Diferencial");
    button3.setToggleGroup(group);

    // VBox para a seçao de Codificacao (espaçamento de 10px entre os elementos)
    VBox boxCodificacao = new VBox(10, labelCodificacao, button1, button2, button3);

    // --- BOTOES DE ENQUADRAMENTO ---
    labelEnquadramento = new Label("Tipo de Enquadramento");
    labelEnquadramento.setStyle("-fx-font-weight: bold;");

    buttonE1 = new RadioButton("Contagem de Caracteres");
    buttonE1.setToggleGroup(groupE);
    buttonE1.setSelected(true);

    buttonE2 = new RadioButton("Inserção de Bytes");
    buttonE2.setToggleGroup(groupE);

    buttonE3 = new RadioButton("Inserção de Bits");
    buttonE3.setToggleGroup(groupE);

    buttonE4 = new RadioButton("Violação da Camada Física");
    buttonE4.setToggleGroup(groupE);

    // VBox para a seçao de Enquadramento (espaçamento de 10px entre os elementos)
    VBox boxEnquadramento = new VBox(10, labelEnquadramento, buttonE1, buttonE2, buttonE3, buttonE4);
    
    // --- BOTOES DE CONTROLE DE ERRO ---

    labelControleErro = new Label("Controle de Erro");
    labelControleErro.setStyle("-fx-font-weight: bold;");

    buttonC1 = new RadioButton("Paridade Par");
    buttonC1.setToggleGroup(groupC);
    buttonC1.setSelected(true);

    buttonC2 = new RadioButton("Paridade Ímpar");
    buttonC2.setToggleGroup(groupC);

    buttonC3 = new RadioButton("CRC");
    buttonC3.setToggleGroup(groupC);

    buttonC4 = new RadioButton("Hamming");
    buttonC4.setToggleGroup(groupC);

    VBox boxControleErro = new VBox(
        10,
        labelControleErro,
        buttonC1,
        buttonC2,
        buttonC3,
        buttonC4);

    // --- SLIDER DE PROBABILIDADE DE ERRO ---
    labelProbabilidade = new Label("Probabilidade de Erro");
    labelProbabilidade.setStyle("-fx-font-weight: bold;");

    sliderErro = new Slider(0, 100, 0);
    sliderErro.setShowTickMarks(true);
    sliderErro.setShowTickLabels(true);
    sliderErro.setMajorTickUnit(10); // Intervalo dos ticks em 10
    sliderErro.setMinorTickCount(0);
    sliderErro.setSnapToTicks(true);
    sliderErro.setPrefWidth(200);

    // VBox para a seçao do Slider
    VBox boxProbabilidade = new VBox(10, labelProbabilidade, sliderErro);

    // --- HBOX PRINCIPAL (MENU) ---
    menuBox = new HBox(
        30,
        boxCodificacao,
        boxEnquadramento,
        boxControleErro,
        boxProbabilidade);
    menuBox.setPrefSize(1000, 200);
    menuBox.setPadding(new Insets(20));
    
    menuBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10px; -fx-border-color: #90caf9; -fx-border-radius: 10px;");
        
    // Posição do HBox no layout (descido ~20px em relaçao ao Y=500 original)
    menuBox.setLayoutX(0);
    menuBox.setLayoutY(500);

    enviar = new Button("Enviar");
    enviar.setLayoutX(280);
    enviar.setLayoutY(200);
  }

  // --- GETTERS ---
    
  public HBox getMenuBox() {
    return menuBox;
  }

  public Slider getSliderErro() {
    return sliderErro;
  }

  public String getTexto() {
    return input.getText();
  }

  public TextField getInput() {
    return input;
  }

  public TextField getExibicao() {
    return exibicao;
  }

  public Line getLH() {
    return linhaHorizontal;
  }

  public Line getLVE() {
    return linhaVerticalE;
  }

  public Line getLVD() {
    return linhaVerticalD;
  }

  public RadioButton getEscolha() {
    return (RadioButton) group.getSelectedToggle();
  }

  public RadioButton getOpcao1() {
    return button1;
  }

  public RadioButton getOpcao2() {
    return button2;
  }

  public RadioButton getOpcao3() {
    return button3;
  }

  public Button getButton() {
    return enviar;
  }

  public RadioButton getEnquadramento() {
    return (RadioButton) groupE.getSelectedToggle();
  }

  public RadioButton getOpcaoE1() {
    return buttonE1;
  }

  public RadioButton getOpcaoE2() {
    return buttonE2;
  }

  public RadioButton getOpcaoE3() {
    return buttonE3;
  }

  public RadioButton getOpcaoE4() {
    return buttonE4;
  }
  
  public RadioButton getControleErro() {
    return (RadioButton) groupC.getSelectedToggle();
  }

  public RadioButton getOpcaoC1() {
    return buttonC1;
  }

  public RadioButton getOpcaoC2() {
    return buttonC2;
  }

  public RadioButton getOpcaoC3() {
    return buttonC3;
  }

  public RadioButton getOpcaoC4() {
    return buttonC4;
  }
}
