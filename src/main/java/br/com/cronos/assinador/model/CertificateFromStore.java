package br.com.cronos.assinador.model;

public class CertificateFromStore extends CertificateData {

	private String certificationAuthority;
	private String cpf;
	private String path;

	public CertificateFromStore() {
		super();
	}
	
	public CertificateFromStore(String identify, String name) {
		super();
		setIdentify(identify); 
		setName(name);
	}

	public String getCertificationAuthority() {
		return certificationAuthority;
	}
	public void setCertificationAuthority(String autoridadeCertificadora) {
		this.certificationAuthority = autoridadeCertificadora;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String caminho) {
		this.path = caminho;
	}

	@Override
	public String toString() {
		return getName();
	}

	
	
}
