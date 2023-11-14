package br.com.cronos.assinador.model;

import java.util.UUID;

public class CertificadoA1 extends Certificado {

	private String caminho;
	private String senha;
	
	public CertificadoA1() { }
	
	public CertificadoA1(String caminho, String senha) {
		super();
		this.caminho = caminho;
		this.senha = senha;
		
		setIdentificador(UUID.randomUUID().toString());
	}

	public String getCaminho() {
		return caminho;
	}

	public String getSenha() {
		return senha;
	}

}
