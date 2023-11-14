package br.com.cronos.assinador.model;

import java.io.File;

public class FileInfo {
	
	private String name;
	private String path;
	private String hash;
	private File file;
	

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

}
