module br.com.cronos.assinador {
    requires javafx.controls;
    requires javafx.fxml;
    requires itextpdf;
	requires bcprov.jdk15on;
    
    //opens br.com.cronos.assinador to javafx.fxml, javafx.graphics;
    //opens br.com.cronos.assinador.controller to javafx.fxml, javafx.graphics;
    //opens br.com.cronos.assinador.model to javafx.base;
    
    opens br.com.cronos.assinador;
    opens br.com.cronos.assinador.controller;
    opens br.com.cronos.assinador.model;
    opens br.com.cronos.assinador.util;
    opens br.com.cronos.assinador.exceptions;
    
    /*exports br.com.cronos.assinador;
    exports br.com.cronos.assinador.controller;
    exports br.com.cronos.assinador.model;
    exports br.com.cronos.assinador.util;
    exports br.com.cronos.assinador.exceptions;*/

}

