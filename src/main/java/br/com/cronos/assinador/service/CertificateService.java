package br.com.cronos.assinador.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.cronos.assinador.exceptions.StoreException;
import br.com.cronos.assinador.model.CertificateA1;
import br.com.cronos.assinador.model.CertificateData;
import br.com.cronos.assinador.model.CertificateFromStore;
import br.com.cronos.assinador.model.Store;
import br.com.cronos.assinador.util.Constants;
import br.com.cronos.assinador.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CertificateService {
	
	public static ObservableList<CertificateFromStore> loadInstalledCertificates() {

		var certs = searchStoresFromSystem().stream()
				.map(Store::getInstanceCertificateFromStore)
				.collect(Collectors.toList());
		
		certs.add(0, new CertificateFromStore(null, "Selecione..."));
		
		return FXCollections.observableArrayList(certs);
	}
	
	
	public static CertificateData loadCertificateA1(String path, String password) {
		var certificateA1 = new CertificateA1(path, password);
		
		Path arq = Path.of(path);
		
		if (!Files.exists(arq)) {
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Não foi possível encontrar o arquivo informado.");
			return null;
		} 
		
		if (!Files.isReadable(arq)) {
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "O arquivo não possui permisão para leitura");
			return null;
		}
		
		certificateA1.setName(arq.getFileName().toString());
		
		try {
			var keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(Files.newInputStream(arq), password.toCharArray());

			certificateA1.setMapCertificateAndKey(getX509CertificateFromFile(keyStore, password));
		} catch (KeyStoreException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Não foi possível carregar o certificado informado.");
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Favor verifique sua senha.", false);
		}
		
		return certificateA1;
		
	}
	
	public static Set<Store> searchStoresFromSystem() {
		
		Set<Store> stores = new HashSet<Store>();
		
		try {
			KeyStore keyStore = getKeyStoreFromSystem();
			keyStore.load(null, null);
			
			for (final Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();) 
				stores.add(new Store(e.nextElement()));
			
		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar os certificados", "Não foi possível acessar a repositório de certificados do Windows.");
		} catch (StoreException e) {
			Utils.showErrorDialog("Erro ao carregar os certificados", e.getMessage());
		}
		
		return stores;
	}
	
	public static Map<X509Certificate, PrivateKey> getX509CertificateFromStore(CertificateFromStore selectedCertificate) {
		var certificateKeyMap = new HashMap<X509Certificate, PrivateKey>();
		 
		try {
			KeyStore keyStore = getKeyStoreFromSystem();
			keyStore.load(null, null);
			
			final PrivateKey privateKey = (PrivateKey) keyStore.getKey(selectedCertificate.getName(), null);
			final X509Certificate certificate = (X509Certificate) keyStore.getCertificate(selectedCertificate.getName());

			certificateKeyMap.put(certificate, privateKey);
			
		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado x509", "Não foi possível acessar a repositório de certificados do Windows.");
		} catch (StoreException e) {
			Utils.showErrorDialog("Erro ao carregar o certificado x509", e.getMessage());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado x509", "Não foi possível carregar a chave do certificado");
		}
		
		return certificateKeyMap;
	}

	private static Map<X509Certificate, PrivateKey> getX509CertificateFromFile(KeyStore keyStore, String password) {
		Map<X509Certificate, PrivateKey> certificateKeyMap = new HashMap<X509Certificate, PrivateKey>();

		try {
			
			X509Certificate certificate = null;
			PrivateKey privateKey = null;
			String alias;
			
			for (Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();) {
				alias = e.nextElement();
				certificate = (X509Certificate) keyStore.getCertificate(alias);
				privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
				certificateKeyMap.put(certificate, privateKey);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Não foi possível carregar o certificado, verifique a senha ou permissão de leitura.");
		}
		
		return certificateKeyMap;
	}
	

	private static KeyStore getKeyStoreFromSystem() throws KeyStoreException, NoSuchProviderException, StoreException {
		
		var isWindows = Constants.SYSTEM_NAME.indexOf(Constants.WINDOWS) >= 0;
		var isMacOs = Constants.SYSTEM_NAME.indexOf(Constants.MAC) >= 0;
		
		KeyStore keyStore = null;
		
		if (isWindows)
			keyStore = KeyStore.getInstance(Constants.WINDOWS_KEY_STORE_TYPE, Constants.WINDOWS_KEY_STORE_PROVIDER);
		else if (isMacOs)
			keyStore = KeyStore.getInstance(Constants.MAC_KEY_STORE_TYPE, Constants.MAC_KEY_STORE_PROVIDER);
		
		if (keyStore == null) 
			throw new StoreException("Sistema operacional" + Constants.SYSTEM_NAME + " não suportado!");

		return keyStore;
	}
	
	
}
