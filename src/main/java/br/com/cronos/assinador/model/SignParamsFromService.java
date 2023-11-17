package br.com.cronos.assinador.model;

import java.util.ArrayList;
import java.util.List;

public class SignParamsFromService {
	
	private String urlPrincipal;
	private List<Params> parametros = new ArrayList<>();

	public static class Params {
		private String url;
		private Integer codigo;
		
		public boolean existeUrl() {
			return this.url != null && !this.url.isBlank();
		}
		
		public boolean naoExisteUrl() {
			return !existeUrl();
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Integer getCodigo() {
			return codigo;
		}

		public void setCodigo(Integer codigo) {
			this.codigo = codigo;
		}

		@Override
		public String toString() {
			return "Params [url=" + url + ", codigo=" + codigo + "]";
		}
		
	}

	public boolean existeUrlPrincipal() {
		return urlPrincipal != null && !urlPrincipal.isBlank();
	}

	public String getUrlPrincipal() {
		return urlPrincipal;
	}

	public void setUrlPrincipal(String urlPrincipal) {
		this.urlPrincipal = urlPrincipal;
	}

	public List<Params> getParametros() {
		return parametros;
	}

	public void setParametros(List<Params> parametros) {
		this.parametros = parametros;
	}
	
}
