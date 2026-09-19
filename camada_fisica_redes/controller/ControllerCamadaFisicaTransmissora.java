/* ***************************************************************
* Autor............: Ruth
* Matricula........: 202511026
* Inicio...........: 26/08/2026
* Ultima alteracao.: 12/08/2026
* Nome.............: EnlaceFisico
* Funcao...........: Transformacao de bits em sinais
*************************************************************** */

package controller;

import model.*;
import view.*;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControllerCamadaFisicaTransmissora {
  private Image imagem = new Image(getClass().getResourceAsStream("../img/fundo.png"));
  private ImageView imageView = new ImageView(imagem);
  private TransmissorReceptor tR = new TransmissorReceptor();
  private AnimacaoOnda aO = new AnimacaoOnda();
  private AplicacaoReceptora aplicacaoReceptora = new AplicacaoReceptora(this);
  private CamadaAplicacaoReceptora camadaAplicacaoReceptora = new CamadaAplicacaoReceptora(aplicacaoReceptora);
  private CamadaFisicaReceptora camadaFisicaReceptora = new CamadaFisicaReceptora(camadaAplicacaoReceptora);
  private MeioDeComunicacao meioDeComunicacao = new MeioDeComunicacao(aO, camadaFisicaReceptora);
  private CamadaFisicaTransmissora camadaFisicaTransmissora = new CamadaFisicaTransmissora(meioDeComunicacao);
  private CamadaAplicacaoTransmissora camadaAplicacaoTransmissora = new CamadaAplicacaoTransmissora(camadaFisicaTransmissora);
  private AplicacaoTransmissora aplicacaoTransmissora = new AplicacaoTransmissora(camadaAplicacaoTransmissora); 
  private Pane root;
  
  public ControllerCamadaFisicaTransmissora(){
    root = new Pane();
    root.getChildren().addAll(imageView, aO.getOnda(), tR.getInput(), tR.getLVE(), tR.getLVD(), tR.getLH(), tR.getOpcao1(), tR.getOpcao2(), tR.getOpcao3(), tR.getButton(), tR.getExibicao());
    
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
    aplicacaoTransmissora.passarMensagem(textoDigitado, escolha);
    // Opcional: Limpar o campo após o clique
    tR.getInput().clear(); 
  }
  
  public void exibirMensagem(String mensagem){
    tR.getExibicao().setText(mensagem);
  }
}
