package br.com.cronos.assinador.service.signers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.cronos.assinador.exceptions.StoreException;
import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.model.SignParamsFromService;
import br.com.cronos.assinador.model.strategy.SavingMode;
import br.com.cronos.assinador.model.strategy.SignerFiles;
import br.com.cronos.assinador.service.LoadFilesFromService;
import br.com.cronos.assinador.service.SendFilesToService;
import br.com.cronos.assinador.util.Utils;
import javafx.collections.ObservableList;

public class SignServiceFiles extends SignerPdfFile implements SignerFiles {

	private SignParamsFromService params;
	
	@Override
	public SavingMode getInstanceType() {
		return SavingMode.SEND_TO_SERVICE;
	}

	@Override
	public void signDocuments(List<FileInfo> files, CertificateData cert) {
		int INITIAL_SIZE = 32;
		ByteArrayOutputStream baos = null;
		
		List<String> filesNotSent = new ArrayList<String>();
		List<String> filesSent = new ArrayList<String>();
		
		for (FileInfo fileInfo : files) {
			
			try {
				baos = getPdfAssinado(fileInfo.getBytes(), cert.getCertificate(), cert.getPrivateKey());
				
				if (baos.size() > INITIAL_SIZE) {
					fileInfo.setBytes(baos.toByteArray());
					var sent = SendFilesToService.sendFile(fileInfo, params.getHeaders());
					
					if (sent)
						filesSent.add(fileInfo.getName());
					else
						filesNotSent.add(fileInfo.getName());
				}
				
			} catch (StoreException e) {
				e.printStackTrace();
				Utils.showErrorDialog("Erro ao acessar certificado", e.getMessage());
			}
			
			
		}
		
		if (baos != null) {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		notificar(filesSent, filesNotSent);
	}
	
	private void notificar(List<String> filesSent, List<String> filesNotSent) {
		if (filesNotSent.isEmpty()) 
			Utils.showInfoDialog("Assinaturas Realizadas", "Todos os arquivos foram assinados e enviado com sucesso");
		
		if (!filesNotSent.isEmpty()) {
			var filesNotSentUri = filesNotSent.stream().collect(Collectors.joining(" \n"));
			Utils.showErrorDialog("Erro ao enviar o arquivo", "Não foi possivel enviar os arquivos: \n" + filesNotSentUri, false);
			filesNotSent = null;
		}
		
		if (!filesSent.isEmpty())  {
			var filesSentUri = filesSent.stream().collect(Collectors.joining(", "));
			Utils.showInfoDialog("Assinaturas Realizadas", "Os arquivos: " + filesSentUri + " foram assinados e enviados com sucesso");
			filesSent = null;
		}
	}

	@Override
	public ObservableList<FileInfo> loadFiles(String argumentBase64) {
		var params = Utils.getSignParamsFromService(argumentBase64);
		
		this.params = params;
		
		return LoadFilesFromService.loadFiles(params);
	}

}
