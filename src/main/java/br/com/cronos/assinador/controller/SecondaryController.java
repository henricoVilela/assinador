package br.com.cronos.assinador.controller;

import java.io.IOException;

import br.com.cronos.assinador.App;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    public void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}