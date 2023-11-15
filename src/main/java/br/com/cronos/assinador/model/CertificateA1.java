package br.com.cronos.assinador.model;

import java.util.UUID;

public class CertificateA1 extends CertificateData {

	private String path;
	private String password;
	
	public CertificateA1() { }
	
	public CertificateA1(String path, String password) {
		super();
		this.path = path;
		this.password = password;
		
		setIdentify(UUID.randomUUID().toString());
	}

	public String getPath() {
		return path;
	}

	public String getPassword() {
		return password;
	}

}
