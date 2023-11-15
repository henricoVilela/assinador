package br.com.cronos.assinador.model;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

public abstract class CertificateData {
	
	private String identify;
	private String name;
	private Map<X509Certificate, PrivateKey> mapCertificateAndKey;

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

	public PrivateKey getPrivateKey() {
		return mapCertificateAndKey.entrySet().iterator().next().getValue();
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public boolean hasIdentify() {
		return this.identify != null;
	}

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}
	
	
}
