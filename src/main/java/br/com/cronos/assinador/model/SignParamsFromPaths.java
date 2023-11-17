package br.com.cronos.assinador.model;

public class SignParamsFromPaths {
	private String pathLeitura;
	private String pathEscrita;
	
	public boolean existePathLeitura() {
		return pathLeitura != null && !pathLeitura.isBlank();
	}
	
	public boolean naoExistePathEscrita() {
		return pathEscrita == null || pathEscrita.isBlank();
	}

	public String getPathLeitura() {
		return pathLeitura;
	}

	public void setPathLeitura(String pathLeitura) {
		this.pathLeitura = pathLeitura;
	}

	public String getPathEscrita() {
		return pathEscrita;
	}

	public void setPathEscrita(String pathEscrita) {
		this.pathEscrita = pathEscrita;
	}

	@Override
	public String toString() {
		return "SignParamsFromPaths [pathLeitura=" + pathLeitura + ", pathEscrita=" + pathEscrita + "]";
	}
	
	
	
}
