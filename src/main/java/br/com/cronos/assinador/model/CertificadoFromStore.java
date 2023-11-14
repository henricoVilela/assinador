package br.com.cronos.assinador.model;

import java.time.LocalDate;

public class CertificadoFromStore extends Certificado {

	private String autoridadeCertificadora;
	private String cpf;
	private String tipoCertificado;
	private LocalDate dataUtilizacao;
	private String caminho;
	private LocalDate dataValidade;
	
	
	public CertificadoFromStore() {
		super();
	}
	
	public CertificadoFromStore(String identificador, String nome) {
		super();
		setIdentificador(identificador); 
		setNome(nome);
	}

	public String getAutoridadeCertificadora() {
		return autoridadeCertificadora;
	}
	public void setAutoridadeCertificadora(String autoridadeCertificadora) {
		this.autoridadeCertificadora = autoridadeCertificadora;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getTipoCertificado() {
		return tipoCertificado;
	}
	public void setTipoCertificado(String tipoCertificado) {
		this.tipoCertificado = tipoCertificado;
	}

	public String getCaminho() {
		return caminho;
	}
	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}

	public LocalDate getDataUtilizacao() {
		return dataUtilizacao;
	}

	public void setDataUtilizacao(LocalDate dataUtilizacao) {
		this.dataUtilizacao = dataUtilizacao;
	}

	public LocalDate getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(LocalDate dataValidade) {
		this.dataValidade = dataValidade;
	}
	
	@Override
	public String toString() {
		return getNome();
	}

	
	
}
