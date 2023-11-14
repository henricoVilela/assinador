package br.com.cronos.assinador.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class Utils {
	
	/**
	 * Exibi alerta de erro e encerra o programa ao fechar
	 * @param header
	 * @param content
	 */
	public static void showErrorDialog(String header, String content) {
        showErrorDialog(header, content, true);
    }
	
	public static void showErrorDialog(String header, String content, boolean exitProgram) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(header);
        alert.setContentText(content);

        if (exitProgram)
	        alert.setOnCloseRequest(event -> {
	            System.exit(0);
	        });

        alert.showAndWait();
    }
	
	public static Image getApplicationIcon() {
    	return new Image(Utils.class.getResourceAsStream("/icons/app_icon.png"));
    }
	
	public static Image getIconDialogPassword() {
    	return new Image(Utils.class.getResourceAsStream("/icons/privacidade.png"));
    }
	
	
	public static byte[] convertFileToBytes(File file) {

        try (FileInputStream fileInputStream = new FileInputStream(file);) {
         
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);

            return fileBytes;
        } catch (Exception e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao ler o arquivo", "Não foi possível ler o arquivo informado para assinatura");
		}
        
        return null;
	}
	
	public static boolean isPDFAssinado(PdfReader pdfReader) throws IOException {
		Security.addProvider(new BouncyCastleProvider());
		
        AcroFields af = pdfReader.getAcroFields();
        ArrayList<String> names = af.getSignatureNames();
        
        return !names.isEmpty();
        
    }

	
}
