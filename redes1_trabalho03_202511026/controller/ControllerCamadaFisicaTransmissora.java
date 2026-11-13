/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: Camada Fisica
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package controller;

import model.*;
import view.*;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerCamadaFisicaTransmissora {
  private Image imagem = new Image(getClass().getResourceAsStream("../img/fundo.png"));
  private ImageView imageView = new ImageView(imagem);
  private TransmissorReceptor tR = new TransmissorReceptor();
  private AnimacaoOnda aO = new AnimacaoOnda();
  private AplicacaoReceptora aplicacaoReceptora = new AplicacaoReceptora(this);
  private CamadaAplicacaoReceptora camadaAplicacaoReceptora = new CamadaAplicacaoReceptora(aplicacaoReceptora);
  private CamadaEnlaceDadosReceptora camadaEnlaceDadosReceptora = new CamadaEnlaceDadosReceptora(camadaAplicacaoReceptora, this);
  private CamadaFisicaReceptora camadaFisicaReceptora = new CamadaFisicaReceptora(camadaEnlaceDadosReceptora);
  private MeioDeComunicacao meioDeComunicacao = new MeioDeComunicacao(aO, camadaFisicaReceptora);
  private CamadaFisicaTransmissora camadaFisicaTransmissora = new CamadaFisicaTransmissora(meioDeComunicacao);
  private CamadaEnlaceDadosTransmissora camadaEnlaceDadosTransmissora = new CamadaEnlaceDadosTransmissora(camadaFisicaTransmissora);
  private CamadaAplicacaoTransmissora camadaAplicacaoTransmissora = new CamadaAplicacaoTransmissora(camadaEnlaceDadosTransmissora);
  private AplicacaoTransmissora aplicacaoTransmissora = new AplicacaoTransmissora(camadaAplicacaoTransmissora); 
  private Pane root;
  
  public ControllerCamadaFisicaTransmissora(){
    root = new Pane();
    root.getChildren().addAll(imageView, aO.getOnda(), 
                              tR.getInput(), tR.getLVE(), 
                              tR.getLVD(), tR.getLH(), 
                              tR.getMenuBox(),
                              /*tR.getOpcao1(), tR.getOpcao2(), 
                              tR.getOpcao3(), tR.getOpcaoE1(), 
                              tR.getOpcaoE2(), tR.getOpcaoE3(),
                              tR.getOpcaoE4()*/ tR.getButton(), 
                              tR.getExibicao());
    
    tR.getButton().setOnAction(event -> enviarMensagem());
  }
  
  public Pane getRoot(){
    return root;
  }
  
  public void enviarMensagem(){  
    String textoDigitado = tR.getTexto();
    int escolha = 0;
    if(tR.getEscolha() == tR.getOpcao1()){
      escolha = 0;
    } else if(tR.getEscolha() == tR.getOpcao2()){
      escolha = 1;
    } else {
      escolha = 2;
    }
    
    int tipoEnquadramento = 0;
    if(tR.getEnquadramento() == tR.getOpcaoE1()){
      tipoEnquadramento = 0;
    } else if(tR.getEnquadramento() == tR.getOpcaoE2()){
      tipoEnquadramento = 1;
    } else if(tR.getEnquadramento() == tR.getOpcaoE3()){
      tipoEnquadramento = 2;
    } else {
      tipoEnquadramento = 3;
    }
    
    int tipoControleDeErro = 0;
    if (tR.getControleErro() == tR.getOpcaoC1()) {
      tipoControleDeErro = 0;
    } else if (tR.getControleErro() == tR.getOpcaoC2()) {
      tipoControleDeErro = 1;
    } else if (tR.getControleErro() == tR.getOpcaoC3()) {
      tipoControleDeErro = 2;
    } else {
      tipoControleDeErro = 3;
    }
    
    int probabilidadeErro = (int) tR.getSliderErro().getValue();
    
    aplicacaoTransmissora.passarMensagem(
        textoDigitado,
        escolha,
        tipoEnquadramento,
        tipoControleDeErro,
        probabilidadeErro);
    //Limpar o campo após o clique
    tR.getInput().clear(); 
  }
  
  public void exibirMensagem(String mensagem){
    tR.getExibicao().setText(mensagem);
  }
  
  public void mensagemDeErroDeTransmissao(){
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("ERRO");
    alert.setContentText("Erro no envio da mensagem");
        
    alert.showAndWait(); // Exibe a janela e aguarda o usuário fechar
  }
}
