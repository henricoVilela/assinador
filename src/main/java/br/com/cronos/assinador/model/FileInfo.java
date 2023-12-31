package br.com.cronos.assinador.model;

import java.io.File;

public class FileInfo {
	
	private String name;
	private String path;
	private String pathWrite;
	
	private String hash;
	private File file;
	
	/**
	 * As duas propriedades são usadas para assinaturas integradas com servicos web
	 */
	private byte[] bytes;
	private String uri;

	public FileInfo(File file) {
		super();
		this.name = file.getName();
		this.path = file.getPath();
		this.file = file;
	}
	
	public FileInfo(String name, String path) {
		super();
		this.name = name;
		this.path = path;
		
	}
	
	public FileInfo(String name, String path, String pathWrite) {
		super();
		this.name = name;
		this.path = path;
		this.pathWrite = pathWrite;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		return file;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPathWrite() {
		return pathWrite;
	}

	public boolean hasPathWrite() {
		return pathWrite != null && !pathWrite.isBlank();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
	
}
