package br.com.cronos.assinador;

import java.io.IOException;

import br.com.cronos.assinador.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
    	
    	//String caminhoPagina = "/resources/FilmeOverview.fxml";
    	System.out.println();
    	//var URL = App.class.getResource("/fxml/AssinadorLocal.fxml");
    	
    	var URL = Class.forName("br.com.cronos.assinador.App").getResource("/fxml/AssinadorLocal.fxml");
    	
    	System.out.println(URL);
    	
    	FXMLLoader fxmlLoader = new FXMLLoader(URL);
        scene = new Scene(fxmlLoader.load(), 660, 500);
        scene.getStylesheets().add(App.class.getResource("/styles/styles.css").toExternalForm());
        
        stage.getIcons().add(Utils.getApplicationIcon());
        stage.setTitle("Assinador Digital");
        stage.setScene(scene);
        stage.show();
    }

    
    public static void main(String[] args) {
    	
    	for (String arg : args) {
    		System.out.println("arg: "+arg);
		}
    	
        launch();
    }

}