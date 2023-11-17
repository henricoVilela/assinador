package br.com.cronos.assinador.model.strategy;

import java.util.HashMap;
import java.util.Map;

import br.com.cronos.assinador.service.signers.SignLocalFiles;
import br.com.cronos.assinador.service.signers.SignLocalPathFiles;
import br.com.cronos.assinador.service.signers.SignServiceFiles;


public class SignerFilesFactory {

	private Map<SavingMode, SignerFiles> instances = new HashMap<>();
	
	public SignerFilesFactory() {
		
		/*SignServiceFiles signFilesFromService = new SignServiceFiles();
		instances.put(signFilesFromService.getInstanceType(), signFilesFromService);
		
		SignLocalFiles signLocalFile = new SignLocalFiles();
		instances.put(signLocalFile.getInstanceType(), signLocalFile);
		
		SignLocalPathFiles signLocalPathFiles = new SignLocalPathFiles();
		instances.put(signLocalPathFiles.getInstanceType(), signLocalPathFiles);*/
		
		addInstance(new SignServiceFiles());
		addInstance(new SignLocalFiles());
		addInstance(new SignLocalPathFiles());
	}

	private void addInstance(SignerFiles signerFiles) {
		instances.put(signerFiles.getInstanceType(), signerFiles);
	}
	
	public SignerFiles getInstance(SavingMode tipoProcessamento) {
		return instances.get(tipoProcessamento);
	}
	
}
