import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view.*;
import model.*;
import controller.*;

public class Principal extends Application {
  private ControllerCamadaFisicaTransmissora controller = new ControllerCamadaFisicaTransmissora(); 
    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(controller.getRoot(), 1000, 700, javafx.scene.paint.Color.WHITE);
        primaryStage.setTitle("Simulador de Fluxo de Bits (Onda Quadrada)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

