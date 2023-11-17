package br.com.cronos.assinador.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.cronos.assinador.exceptions.LoadPdfFromServiceException;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.model.SignParams;
import br.com.cronos.assinador.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LoadFilesFromService {

	public static ObservableList<FileInfo> loadFiles(SignParams params) {
		List<FileInfo> files = new ArrayList<>();
		
		for (var param : params.getParametros()) {
			if (param.existeUrl()) {
				var URI = String.format(param.getUrl(), param.getCodigo());
				
				try {
					files.add(buscarPdf(URI));
				} catch (LoadPdfFromServiceException e) {
					Utils.showErrorDialog("Erro ao buscar PDF", e.getMessage());
				}
			}
			
		}
		
		return FXCollections.observableArrayList(files);
		
	}
	
	public static FileInfo buscarPdf(String URI) throws LoadPdfFromServiceException {
		RestTemplate restTemplate = new RestTemplate();

		try {
			
			ResponseEntity<ByteArrayResource> response = restTemplate.getForEntity(URI, ByteArrayResource.class);
			
			if (response.getStatusCode().is2xxSuccessful()) {
	            ByteArrayResource resource = response.getBody();
	      
	            if (Objects.nonNull(resource)) {
	            	FileInfo file = new FileInfo(getFileNameFromResponse(response), "...");
	            	file.setBytes(resource.getByteArray());
	            	file.setUri(URI);
	            	
	            	return file;
	            }

	        } else 
	        	throw new LoadPdfFromServiceException("");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoadPdfFromServiceException("Não foi possível recuperar o pdf localizado em: " + URI + " " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Recupera o nome arquivo esperado no reader 'Content-Disposition' com o valor 'inline; filename=nome-do-arquivo'
	 * @param response
	 * @return o nome informado no cabeçalho ou 'Nome não informado' caso não exista, para que não fique vazio em tela.
	 */
	private static String getFileNameFromResponse(ResponseEntity<?> response) {
		var contentDisposition = response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
    	var fileName = "Nome não informado"; 
    	
    	if (contentDisposition != null && !contentDisposition.isEmpty()) {
    		String content = " " + contentDisposition.get(0);
    		String[] values = content.split("filename=");
    		if (values.length > 1)
    			fileName = values[1] + ".pdf";
    	}
    	
    	return fileName;
	}
	
}