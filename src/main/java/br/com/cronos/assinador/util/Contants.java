package br.com.cronos.assinador.util;

public abstract class Contants {
	
	public static final String SYSTEM_NAME = System.getProperty("os.name").toUpperCase();
	public static final String USER_HOME_PATH = System.getProperty("user.home");
	public static final String WINDOWS = "WINDOWS";
	public static final String MAC = "MAC";
	
	public static final String WINDOWS_KEY_STORE_TYPE = "Windows-MY";
	public static final String WINDOWS_KEY_STORE_PROVIDER = "SunMSCAPI";
	
	public static final String MAC_KEY_STORE_TYPE = "KeychainStore";
	public static final String MAC_KEY_STORE_PROVIDER = "Apple";
	

}
