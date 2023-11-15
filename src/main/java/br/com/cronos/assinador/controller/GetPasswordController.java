package br.com.cronos.assinador.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class GetPasswordController {
	
	@FXML
	private PasswordField passwordField;
	
	private String password;

	@FXML
    private void onClickConfirmPassword(ActionEvent event) {
		
		password = passwordField.getText();
		
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
	}
	
	
    public String getPasswordProvided() {
        return password;
    }
}
