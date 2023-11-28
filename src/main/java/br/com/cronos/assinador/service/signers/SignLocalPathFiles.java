package br.com.cronos.assinador.service.signers;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import br.com.cronos.assinador.exceptions.StoreException;
import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.model.strategy.SavingMode;
import br.com.cronos.assinador.model.strategy.SignerFiles;
import br.com.cronos.assinador.service.LoadFilesFromPaths;
import br.com.cronos.assinador.util.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;

public class SignLocalPathFiles extends SignerPdfFile implements SignerFiles {

	@Override
	public SavingMode getInstanceType() {
		return SavingMode.SAVE_TO_PATH;
	}

	@Override
	public void signDocuments(List<FileInfo> files, CertificateData cert) {
		int INITIAL_SIZE = 32;
		byte[] bytesOfFile = null;
		
		
		List<String> unsaved = new ArrayList<String>();
		List<String> saved = new ArrayList<String>();
		
		for (FileInfo fileInfo : files) {
			
			Path path = Path.of(fileInfo.getPath());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			try {
				bytesOfFile = Files.readAllBytes(path);
				baos = getPdfAssinado(bytesOfFile, cert.getCertificate(), cert.getPrivateKey());
				
				if (baos.size() > INITIAL_SIZE) {
					
					var pathWrite = (fileInfo.hasPathWrite()) ? fileInfo.getPathWrite() : fileInfo.getPath();
					
					try (FileOutputStream fos = new FileOutputStream(pathWrite)) {
			            fos.write(baos.toByteArray());
			        }
				}
				
			} catch (StoreException se) {
				Platform.runLater(() ->Utils.showErrorDialog("Erro ao acessar chave do certificado", se.getMessage())); 
				return;
			} catch (IOException e) {
	            e.printStackTrace();
	            unsaved.add(fileInfo.getName());
	            
	            continue;
	        }
			
			saved.add(fileInfo.getName());
		}
		
		bytesOfFile = null;
		
		notificar(saved, unsaved);
	}
	
	
	@Override
	public ObservableList<FileInfo> loadFiles(String argumentBase64) {
		var params = Utils.getSignParamsFromPaths(argumentBase64);
		return LoadFilesFromPaths.loadFiles(params);
	}

	
}
