package br.com.cronos.assinador.service;

import static br.com.cronos.assinador.util.Utils.addHeadersIntoRestTemplate;

import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.util.Utils;

public class SendFilesToService {
	
	public static void sendFile(FileInfo file, Map<String, String> paramsHeaders) {
		
		paramsHeaders.put(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
		
		try {
			RestTemplate restTemplate = new RestTemplate();

	        addHeadersIntoRestTemplate(restTemplate, paramsHeaders);

	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("arquivo", new ByteArrayResource(file.getBytes()) {
	        	
	            @Override
	            public String getFilename() {
	                return file.getName(); 
	            }
	            
	        });

	        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(file.getUri());

	        RequestCallback requestCallback = restTemplate.httpEntityCallback(
	            new HttpEntity<>(body),
	            String.class
	        );

	        ResponseExtractor<ResponseEntity<String>> responseExtractor = restTemplate.responseEntityExtractor(String.class);

	        ResponseEntity<String> responseEntity = restTemplate.execute(
	            builder.build().toUri(),
	            HttpMethod.POST,
	            requestCallback,
	            responseExtractor
	        );
	        
	        if (!responseEntity.getStatusCode().is2xxSuccessful())
	        	throw new Exception("");

	        
		} catch (Exception e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao enviar o arquivo", "Não foi possivel enviar o arquivo para o endereço: " + file.getUri(), false);
		}
		

    }
}
