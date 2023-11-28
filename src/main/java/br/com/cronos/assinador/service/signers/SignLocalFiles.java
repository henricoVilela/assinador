package br.com.cronos.assinador.service.signers;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.cronos.assinador.exceptions.StoreException;
import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.model.strategy.SavingMode;
import br.com.cronos.assinador.model.strategy.SignerFiles;
import br.com.cronos.assinador.util.Utils;
import javafx.application.Platform;

public class SignLocalFiles extends SignerPdfFile implements SignerFiles {

	@Override
	public SavingMode getInstanceType() {
		return SavingMode.LOCAL;
	}

	@Override
	public void signDocuments(List<FileInfo> files, CertificateData cert) {
		int INITIAL_SIZE = 32;
		byte[] bytesOfFile = null;
		
		List<String> unsaved = new ArrayList<String>();
		List<String> saved = new ArrayList<String>();
		for (FileInfo fileInfo : files) {
			bytesOfFile = Utils.convertFileToBytes(fileInfo.getFile());
			
			try (ByteArrayOutputStream baos = getPdfAssinado(bytesOfFile, cert.getCertificate(), cert.getPrivateKey())){

				if (baos.size() > INITIAL_SIZE) {
					try (FileOutputStream fos = new FileOutputStream(fileInfo.getPath())) {
			            fos.write(baos.toByteArray());
			        }
				}
				
			} catch (StoreException se) {
				Platform.runLater(() -> Utils.showErrorDialog("Erro ao acessar chave do certificado", se.getMessage()));
				return;
			} catch (IOException e) {
				Platform.runLater(() -> Utils.showErrorDialog("Erro ao atualizar documento", "Não foi possível gravar o documeto " + fileInfo.getName(), false)); 
	            unsaved.add(fileInfo.getName());
	            continue;
	        }
			
			saved.add(fileInfo.getName());
			
		}
		bytesOfFile = null;
		 
		notificar(saved, unsaved);
		
	}

}
