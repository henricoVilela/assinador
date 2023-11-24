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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.cronos.assinador.model.FileInfo;

public class SendFilesToService {
	
	/**
	 * Envia o arquivo para o servidor informado
	 * @param file arquivo
	 * @param paramsHeaders headers custons
	 * @return true se foi enviado com succeso e false se houve algum problema
	 */
	public static boolean sendFile(FileInfo file, Map<String, String> paramsHeaders) {
		
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

	        ResponseEntity<Void> responseEntity = restTemplate.exchange(
	            builder.build().toUri(),
	            HttpMethod.POST,
	            new HttpEntity<>(body),
	            Void.class
	        );
	         
	        if (responseEntity.getStatusCode().is2xxSuccessful())
	        	return true;
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;

    }
}
