module br.com.cronos.assinador {
    requires javafx.controls;
    requires javafx.fxml;
    requires itextpdf;
	requires bcprov.jdk15on;
    
    opens br.com.cronos.assinador to javafx.fxml, javafx.graphics;
    opens br.com.cronos.assinador.controller to javafx.fxml, javafx.graphics;
    opens br.com.cronos.assinador.model to javafx.base;
    
    
    exports br.com.cronos.assinador;
    exports br.com.cronos.assinador.controller;
    exports br.com.cronos.assinador.model;

}

