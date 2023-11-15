package br.com.cronos.assinador.util;

import br.com.cronos.assinador.exceptions.StoreException;

public abstract class Constants {
	
	public static final String SYSTEM_NAME = System.getProperty("os.name").toUpperCase();
	public static final String USER_HOME_PATH = System.getProperty("user.home");
	public static final String WINDOWS = "WINDOWS";
	public static final String MAC = "MAC";
	
	public static final String WINDOWS_KEY_STORE_TYPE = "Windows-MY";
	public static final String WINDOWS_KEY_STORE_PROVIDER = "SunMSCAPI";
	
	public static final String MAC_KEY_STORE_TYPE = "KeychainStore";
	public static final String MAC_KEY_STORE_PROVIDER = "Apple";
	
	
	public static String getKeyStoreProvider() throws StoreException {
		var isWindows = Constants.SYSTEM_NAME.indexOf(Constants.WINDOWS) >= 0;
		var isMacOs = Constants.SYSTEM_NAME.indexOf(Constants.MAC) >= 0;
		var provider = "";
		
		if (isWindows) 
			provider = Constants.WINDOWS_KEY_STORE_PROVIDER;
		else if (isMacOs)
			provider = Constants.MAC_KEY_STORE_PROVIDER;
		else
			throw new StoreException("Sistema operacional" + Constants.SYSTEM_NAME + " não suportado!");
		
		return provider;
	}

}
