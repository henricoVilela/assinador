package br.com.cronos.assinador.model.strategy;

import java.util.List;

import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.FileInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface SignerFiles extends StrategyBase<SavingMode> {
	void signDocuments(List<FileInfo> files, CertificateData cert);
	
	/**
	 * Metodo para carregar arquivos quando usado chamadas de servico ou com os caminhos passado em argumentos
	 * @param argumentBase64
	 * @return
	 */
	default ObservableList<FileInfo> loadFiles(String argumentBase64) { 
		return FXCollections.observableArrayList();
	};
}
