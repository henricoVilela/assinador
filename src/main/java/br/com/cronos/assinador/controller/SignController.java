package br.com.cronos.assinador.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.CertificateFromStore;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.service.CertificateService;
import br.com.cronos.assinador.service.SignService;
import br.com.cronos.assinador.service.SignService.FileSavingMode;
import br.com.cronos.assinador.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SignController implements Initializable {
	
	FileChooser fileChooser = new FileChooser();
	
	@FXML
    private AnchorPane anchorPane;
	
	@FXML
	private RadioButton rbCertsInstalled;
	
	@FXML
	private RadioButton rbExternalFile;
	
	@FXML
	private Button btnSelectFile;
	
	@FXML
	private Button btnRemoveFile;
	
	@FXML
	private Button btnSingDocuments;
	
	@FXML
	private Label labelNameCertificate;
	
	@FXML
	private ComboBox<CertificateFromStore> selectCertificates;
	
	@FXML
	private TableView<FileInfo> tableOfFiles;
	
	@FXML
	private TableColumn<FileInfo, String> fileName;
	
	@FXML
	private TableColumn<FileInfo, String> filePath;
	
	private CertificateData selectedCertificate;

	private Stage waitDialog = null;
	
    @FXML
    public void onChangeToCertsInstalled() {
    	
        if (selectCertificates.getItems().isEmpty()) 
        	selectCertificates.setItems(CertificateService.loadInstalledCertificates());
        
        selectCertificates.getSelectionModel().select(0);
        selectCertificates.setVisible(true);
        btnSelectFile.setVisible(false);

    }
    
    @FXML
    public void onChangeToExternalFile() {
        selectCertificates.setVisible(false);
        btnSelectFile.setVisible(true);
    }
    
    @FXML
    public void onSelectCertificate() {
    	CertificateFromStore selected = selectCertificates.getSelectionModel().getSelectedItem();
    	selectedCertificate = selected;
    	
    	if (selected.getIdentify() == null)
    		return;
    	
    	var x509CertificateMap = CertificateService.getX509CertificateFromStore(selected);
    	
    	selectedCertificate.setMapCertificateAndKey(x509CertificateMap);
    	controllBtnSignDocuments();
    }
    
    
	@FXML
	@SuppressWarnings("exports")
    public void onClickSelectCertificateA1(ActionEvent event) throws IOException {
    	setPfxFilter();
    	
    	var selected = fileChooser.showOpenDialog(new Stage());
    	if (selected == null) 
    		return;
    	
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GetPassword.fxml"));
    	Parent root = fxmlLoader.load();
    	
    	Stage stage = new Stage();
    	
    	stage.setScene(new Scene(root));
    	stage.setTitle("Informa a senha para o certificado");
    	stage.setResizable(false);
    	stage.getIcons().add(Utils.getIconDialogPassword());
    	stage.initModality(Modality.APPLICATION_MODAL);
    	stage.showAndWait();
    	
    	GetPasswordController controller = fxmlLoader.getController();
    	String password = controller.getPasswordProvided();
    	
    	if (password != null) 
    		selectedCertificate = CertificateService.loadCertificateA1(selected.getPath(), password);
        	
    	controllLabelCertificateA1();
    	controllBtnSignDocuments();
    	
    }
    
    @FXML
    public void onClickLoadFiles() {
    	setPdfFilter();
    	
    	var selecteds = fileChooser.showOpenMultipleDialog(new Stage());
    	if (selecteds == null) 
    		return;
    	
    	List<FileInfo> files = selecteds.stream()
    			.map(file -> new FileInfo(file))
    			.collect(Collectors.toList());
    	
    	tableOfFiles.setItems(FXCollections.observableArrayList(files));
    	
    	enable(btnRemoveFile);
    	controllBtnSignDocuments();
    }
    
    @FXML
    public void onClickRemoveFile() {
    	int selectedIndex = tableOfFiles.getSelectionModel().getSelectedIndex();
    	if (selectedIndex < 0) {
    		Utils.showWarnDialog("Ação Necessária", "Selecionde ao menos um arquivo para remoção");
    		return;
    	}
    		
    	
    	tableOfFiles.getItems().remove(selectedIndex);
    	
    	if (tableOfFiles.getItems().isEmpty()) 
    		disable(btnRemoveFile);
    }
    
    @FXML
    public void onClickSignDocuments() {
    	SignService.signDocuments(tableOfFiles.getItems(), selectedCertificate, FileSavingMode.LOCAL);
    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setPdfFilter();
		setStageShowWaitScream();
		
    	//Table of Files
    	fileName.setCellValueFactory(new PropertyValueFactory<>("name"));
    	filePath.setCellValueFactory(new PropertyValueFactory<>("path"));
	}
	
	private void controllLabelCertificateA1() {
		if (selectedCertificate != null && 
			selectedCertificate.getMapCertificateAndKey() != null && 
			!selectedCertificate.getMapCertificateAndKey().isEmpty() ) {
			
    		labelNameCertificate.setText(selectedCertificate.getName());
    		labelNameCertificate.setVisible(true);
    		
    		return;
    	} 
		
		labelNameCertificate.setText("");
		labelNameCertificate.setVisible(false);
	}
	
	private void controllBtnSignDocuments() {
		if (btnSingDocuments == null)
			return;
		
		var disable = selectedCertificate == null || tableOfFiles.getItems().isEmpty();
		
		btnSingDocuments.setDisable(disable);
		
	}
	
	private void enable(Node node) {
		if (node != null) 
			node.setDisable(false);
	}
	
	private void disable(Node node) {
		if (node != null) 
			node.setDisable(true);
	}
	
	private void setPfxFilter() {
		fileChooser.setTitle("Escolha um arquivo .pfx");
    	setFilter("Arquivos PFX (*.pfx)", "*.pfx");
	}
	
	private void setPdfFilter() {
		fileChooser.setTitle("Escolha um arquivo .pdf");
    	setFilter("Arquivos PDF (*.pdf)", "*.pdf");
	}
	
	private void setFilter(String title, String extension) {
		ExtensionFilter filter = new ExtensionFilter(title, extension);
		fileChooser.getExtensionFilters().clear();
    	fileChooser.getExtensionFilters().add(filter);
    	fileChooser.setSelectedExtensionFilter(filter);
	}
	
	/**
	 * Criar um stage com o dialog de espera de processamento
	 * para usar: waitDialog.show() exibe o modal e waitDialog.close() fecha o modal
	 */
	private void setStageShowWaitScream() {
		
		if (waitDialog != null)
			return;
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/WaitScream.fxml"));
		Stage stage = new Stage();
		
		try {
			Parent root = fxmlLoader.load();
	    	
	    	stage.setScene(new Scene(root));
	    	stage.setTitle("");
	    	stage.setResizable(false);
	    	stage.getIcons().add(Utils.getIconInfo());
	    	stage.initModality(Modality.APPLICATION_MODAL);
	    	stage.setOnCloseRequest(event -> event.consume());
	    	
		} catch (IOException e) {
			System.err.println("Erro ao tentar carregar stage da tela de espera");
		}
    	
		waitDialog = stage;
	}
	
}
