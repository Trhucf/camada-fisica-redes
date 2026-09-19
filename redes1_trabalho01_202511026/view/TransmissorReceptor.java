/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package view;

import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;


public class TransmissorReceptor {
  private final TextField input;
  private final TextField exibicao;
  private final Line linhaHorizontal = new Line(200, 500, 800, 500);
  private final Line linhaVerticalE = new Line(200, 325, 200, 500);
  private final Line linhaVerticalD = new Line(800, 325, 800, 500);
  private final ToggleGroup group = new ToggleGroup();
  private final RadioButton button1;
  private final RadioButton button2;
  private final RadioButton button3;
  private final Button enviar;
  
  public TransmissorReceptor(){
    
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
    
    button1 = new RadioButton("Binário");
    button1.setToggleGroup(group);
    button1.setSelected(true);
    button1.setLayoutX(430);
    button1.setLayoutY(110);
    
    button2 = new RadioButton("Manchester");
    button2.setToggleGroup(group);
    button2.setLayoutX(430);
    button2.setLayoutY(140);
    
    button3 = new RadioButton("Manchester Diferencial");
    button3.setToggleGroup(group);
    button3.setLayoutX(430);
    button3.setLayoutY(170);
    
    enviar = new Button("Enviar");
    enviar.setLayoutX(280);
    enviar.setLayoutY(200);
    
  }
  
  public String getTexto(){
    return input.getText();
  }
  
  public TextField getInput(){
    return input;
  }
  
  public TextField getExibicao(){
    return exibicao;
  }
  
  public Line getLH(){
    return linhaHorizontal;
  }
  
  public Line getLVE(){
    return linhaVerticalE;
  }
  
  public Line getLVD(){
    return linhaVerticalD;
  }
  
  public RadioButton getEscolha(){
    return (RadioButton) group.getSelectedToggle();
  }

  public RadioButton getOpcao1(){
    return button1;
  }
  
  public RadioButton getOpcao2(){
    return button2;
  }
  
  public RadioButton getOpcao3(){
    return button3;
  }
  
  public Button getButton(){
    return enviar;
  }
}
