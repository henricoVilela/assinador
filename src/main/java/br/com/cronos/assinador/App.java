package br.com.cronos.assinador;

import java.io.IOException;
import java.util.Map;

import br.com.cronos.assinador.controller.SignController;
import br.com.cronos.assinador.model.strategy.SavingMode;
import br.com.cronos.assinador.util.Constants;
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
    	
    	FXMLLoader fxmlLoader = new FXMLLoader(URL);
        scene = new Scene(fxmlLoader.load(), 700, 500);
        scene.getStylesheets().add(App.class.getResource("/styles/styles.css").toExternalForm());
        
        stage.getIcons().add(Utils.getApplicationIcon());
        stage.setTitle("Assinador Digital");
        stage.setScene(scene);
        stage.show();
        
        SignController controller = fxmlLoader.getController();
        
        var arguments = getParameters().getNamed();
        if (arguments.isEmpty()) {
        	controller.setSavingMode(SavingMode.LOCAL);
        	return;
        }
        	
        validateExec(arguments);
       
        if (arguments.containsKey(Constants.ARG_DATA_FROM_SERVICE)) {
        	
        	controller.setSavingMode(SavingMode.SEND_TO_SERVICE);
        	controller.setArgumentBase64(arguments.get(Constants.ARG_DATA_FROM_SERVICE));
        	controller.loadTableFiles();
        	
            return;
        }
        
        if (arguments.containsKey(Constants.ARG_DATA_FROM_PATHS)) {
        	
        	controller.setSavingMode(SavingMode.SAVE_TO_PATH);
        	controller.setArgumentBase64(arguments.get(Constants.ARG_DATA_FROM_PATHS));
        	controller.loadTableFiles();
        	
            return;
        }

        
    }
    
    private static void validateExec(Map<String, String>  arguments) {
    	
    	var isFromService = arguments.containsKey(Constants.ARG_DATA_FROM_SERVICE);
    	var isFromPaths = arguments.containsKey(Constants.ARG_DATA_FROM_PATHS);
    	
    	if (isFromService && isFromPaths)
    		Utils.showErrorDialog("Execução inválida", "A aplicação não aceita duas forma de execução ao mesmo tempo.");
    	
    	if (!arguments.isEmpty() && !isFromPaths && !isFromService)
    		Utils.showErrorDialog("Execução inválida", "A aplicação não aceita os argumentos informados.");
    	
    }

    
    public static void main(String[] args) {
    	
    	for (String arg : args) 
    		System.out.println("arg: "+arg);
		
        launch(args);
    }

}