package br.com.cronos.assinador;

import java.io.IOException;

import br.com.cronos.assinador.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        scene = new Scene(loadFXML("AssinadorLocal"), 660, 500);
       
        scene.getStylesheets().add(App.class.getResource("/styles/styles.css").toExternalForm());
        
        stage.getIcons().add(Utils.getApplicationIcon());
        stage.setTitle("Assinador Digital");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/"+fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void main(String[] args) {
    	
    	for (String arg : args) {
    		System.out.println("arg: "+arg);
		}
    	
        launch();
    }

}