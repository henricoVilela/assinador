package br.com.cronos.assinador;

import java.io.IOException;

import br.com.cronos.assinador.controller.SignController;
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
    	
    	var URL = Class.forName("br.com.cronos.assinador.App").getResource("/fxml/AssinadorLocal.fxml");
    	
    	System.out.println(URL);
    	
    	FXMLLoader fxmlLoader = new FXMLLoader(URL);
        scene = new Scene(fxmlLoader.load(), 660, 500);
        scene.getStylesheets().add(App.class.getResource("/styles/styles.css").toExternalForm());
        
        stage.getIcons().add(Utils.getApplicationIcon());
        stage.setTitle("Assinador Digital");
        stage.setScene(scene);
        stage.show();
        
        var arguments = getParameters().getRaw();
        
        if (arguments.isEmpty())
        	return;
        
        if (arguments.size() > 1)
        	Utils.showErrorDialog("Execução inválida", "A aplicação espera no máximo um argumento de linha de comando.");

        SignController controller = fxmlLoader.getController();
        controller.setSignParams(Utils.getInstanceSignParamsFromArgument(arguments.get(0)));
        controller.loadTableFiles();
    }

    
    public static void main(String[] args) {
    	for (String arg : args) {
    		System.out.println("arg: "+arg);
		}

        launch(args);
    }

}