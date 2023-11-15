package br.com.cronos.assinador.model;

import java.util.Objects;
import java.util.UUID;

public class Store {
	
	private String alias;
	private String path;
	
	public Store(String alias) {
		super();
		this.alias = alias;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String nome) {
		this.alias = nome;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(alias);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Store other = (Store) obj;
		return Objects.equals(alias, other.alias);
	}

	@Override
	public String toString() {
		return "Store [alias=" + alias + "]";
	}
	
	public CertificateFromStore getInstanceCertificateFromStore() {
        CertificateFromStore to = new CertificateFromStore();
        
        to.setIdentify(UUID.randomUUID().toString());
        to.setPath(this.getPath());
        to.setName(this.getAlias());
        
        return to;
	}
	
	
}
