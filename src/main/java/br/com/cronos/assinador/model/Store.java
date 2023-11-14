package br.com.cronos.assinador.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Store {
	
	private String nome;
	private String caminho;
	private Long slot;
	private LocalDate dataUtilizacao;
	private String tipoCertificado;
	
	public Store(String nome) {
		super();
		this.nome = nome;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCaminho() {
		return caminho;
	}
	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
	public Long getSlot() {
		return slot;
	}
	public void setSlot(Long slot) {
		this.slot = slot;
	}
	public LocalDate getDataUtilizacao() {
		return dataUtilizacao;
	}
	public void setDataUtilizacao(LocalDate dataUtilizacao) {
		this.dataUtilizacao = dataUtilizacao;
	}
	public String getTipoCertificado() {
		return tipoCertificado;
	}
	public void setTipoCertificado(String tipoCertificado) {
		this.tipoCertificado = tipoCertificado;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nome);
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
		return Objects.equals(nome, other.nome);
	}

	@Override
	public String toString() {
		return "Store [nome=" + nome + ", tipoCertificado=" + tipoCertificado + "]";
	}
	
	public CertificadoFromStore extrairCertificado() {
        CertificadoFromStore to = new CertificadoFromStore();
        
        to.setIdentificador(UUID.randomUUID().toString());
        to.setCaminho(this.getCaminho());
        to.setDataUtilizacao(this.getDataUtilizacao());
        to.setNome(this.getNome());
        to.setTipoCertificado(this.getTipoCertificado());
        
        return to;
	}
	
	
}
