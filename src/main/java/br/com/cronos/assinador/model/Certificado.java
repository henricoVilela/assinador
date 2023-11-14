package br.com.cronos.assinador.model;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

public abstract class Certificado {
	
	private String identificador;
	private String nome;
	private Map<X509Certificate, PrivateKey> mapCertificateAndKey;
	private PublicKey publicKey;
	
	public Certificate getCertificate() {
		final Set<Map.Entry<X509Certificate, PrivateKey>> set = mapCertificateAndKey.entrySet();
		final X509Certificate cert = (X509Certificate) set.iterator().next().getKey();

		return cert;
	}
	
	public Map<X509Certificate, PrivateKey> getMapCertificateAndKey() {
		return mapCertificateAndKey;
	}
	
	public void setMapCertificateAndKey(Map<X509Certificate, PrivateKey> mapCertificateAndKey) {
		this.mapCertificateAndKey = mapCertificateAndKey;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	public PrivateKey getPrivateKey() {
		return mapCertificateAndKey.entrySet().iterator().next().getValue();
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	
	public boolean hasIdentificador() {
		return this.identificador != null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
