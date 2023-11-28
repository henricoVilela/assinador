package br.com.cronos.assinador.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.CertificateFromStore;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.model.strategy.SavingMode;
import br.com.cronos.assinador.model.strategy.SignerFiles;
import br.com.cronos.assinador.model.strategy.SignerFilesFactory;
import br.com.cronos.assinador.service.CertificateService;
import br.com.cronos.assinador.util.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SignController implements Initializable {
	
	FileChooser fileChooser = new FileChooser();
	
	private CertificateData selectedCertificate;
	private Stage progressDialog;
	private SavingMode savingMode;
	private String argumentBase64;
	private SignerFiles signerService;
	
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
	private Button btnLoadFiles;
	
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
    	progressDialog.show();
    	new Thread(() -> {
    		
    		signerService.signDocuments(tableOfFiles.getItems(), selectedCertificate);
            Platform.runLater(() -> progressDialog.close());
        }).start();
    	
    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setPdfFilter();
		setStageProgressDialog();
		
		selectCertificates.setItems(CertificateService.loadInstalledCertificates());
		
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
	
	private void setStageProgressDialog() {
		if (progressDialog != null)
			return;
		
		Stage stage = new Stage();
		stage.setResizable(false);
    	stage.getIcons().add(Utils.getApplicationIcon());
    	stage.initModality(Modality.APPLICATION_MODAL);
    	
		try {
			ProgressIndicator progressIndicator = new ProgressIndicator();
		
			Label label = new Label("Por favor, aguarde...");
			label.setCenterShape(true);
			
			BorderPane root = new BorderPane();

			root.setCenter(progressIndicator);
			root.setBottom(label);
			BorderPane.setMargin(label, new Insets(0, 0, 50, 75));
			
			Scene scene = new Scene(root, 250, 200);
			stage.setScene(scene);
			stage.setTitle("Aguarde");
			
		} catch (Exception e) {
			System.err.println("Erro ao tentar carregar stage do indicador de progresso");
		}
		
		progressDialog = stage;
	}
	
	public void loadTableFiles() {
		if (this.signerService != null && this.argumentBase64 != null) {
			disable(btnLoadFiles);
			
			progressDialog.show();
			new Thread(() -> {
				tableOfFiles.setItems(signerService.loadFiles(argumentBase64));
				
				Platform.runLater(() -> progressDialog.close());
	        }).start();
			
    		
    	}
	}
	
	private void setInstaceSignerService() {
		SignerFilesFactory signerFilesFactory = new SignerFilesFactory();
		this.signerService = signerFilesFactory.getInstance(savingMode);
		
	}

	public SavingMode getSavingMode() {
		return savingMode;
	}

	public void setSavingMode(SavingMode savingMode) {
		this.savingMode = savingMode;
		
		if (this.signerService == null)
			setInstaceSignerService();
	}

	public String getArgumentBase64() {
		return argumentBase64;
	}

	public void setArgumentBase64(String argumentBase64) {
		this.argumentBase64 = argumentBase64;
	}
	
}
