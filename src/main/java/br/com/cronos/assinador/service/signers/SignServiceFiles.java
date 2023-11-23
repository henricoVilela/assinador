package br.com.cronos.assinador.service.signers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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
		for (FileInfo fileInfo : files) {
			
			try {
				baos = getPdfAssinado(fileInfo.getBytes(), cert.getCertificate(), cert.getPrivateKey());
				
				if (baos.size() > INITIAL_SIZE) {
					fileInfo.setBytes(baos.toByteArray());
					SendFilesToService.sendFile(fileInfo, params.getHeaders());
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
		
		Utils.showInfoDialog("Assinaturas Realizadas", "Os arquivos foram assinados com sucesso");
		
	}

	@Override
	public ObservableList<FileInfo> loadFiles(String argumentBase64) {
		var params = Utils.getSignParamsFromService(argumentBase64);
		
		this.params = params;
		
		return LoadFilesFromService.loadFiles(params);
	}

}
