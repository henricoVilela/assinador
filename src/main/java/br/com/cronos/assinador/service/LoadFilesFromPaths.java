package br.com.cronos.assinador.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.model.SignParamsFromPaths;
import br.com.cronos.assinador.util.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LoadFilesFromPaths {

	public static ObservableList<FileInfo> loadFiles(List<SignParamsFromPaths> params) {
		List<FileInfo> files = new ArrayList<>();
		
		if (params.stream().anyMatch(SignParamsFromPaths::naoExistePathEscrita))
			Platform.runLater(
				() -> Utils.showWarnDialog("Aviso", "Existe arquivo ao qual não foi informado o caminho para escrita, então o arquivo informado para leitura sera sobrescrito com o arquivo assinado.")
			);
		
		String filesNotFound = "";
		for (SignParamsFromPaths param : params) {
			
			Path path = Path.of(param.getPathLeitura());
			
			if (!Files.exists(path)) {
				filesNotFound +=  param.getPathLeitura() + "\n";
				continue;
			}	
			
			files.add(new FileInfo(path.getFileName().toString(), param.getPathLeitura(), param.getPathEscrita()));
			
		}
		
		if (!filesNotFound.isEmpty()) {
			final var filesName = filesNotFound;
			Platform.runLater(() -> Utils.showErrorDialog("Arquivo inexistente", "Não foi possível encontrar o(s) arquivo(s): " + filesName, false));
		}
		
		return FXCollections.observableArrayList(files);
		
	}
}
