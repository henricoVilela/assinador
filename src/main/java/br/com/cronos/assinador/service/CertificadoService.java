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
import br.com.cronos.assinador.model.Certificado;
import br.com.cronos.assinador.model.CertificadoA1;
import br.com.cronos.assinador.model.CertificadoFromStore;
import br.com.cronos.assinador.model.Store;
import br.com.cronos.assinador.util.Contants;
import br.com.cronos.assinador.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CertificadoService {
	
	public static ObservableList<CertificadoFromStore> carregarCertificadosInstalados() {

		var certificados = buscarStoriesFromSystem().stream()
				.map(Store::extrairCertificado)
				.collect(Collectors.toList());
		
		certificados.add(0, new CertificadoFromStore(null, "Selecione..."));
		
		return FXCollections.observableArrayList(certificados);
	}
	
	
	public static Certificado carregarCertificadoA1(String path, String password) {
		var certificadoA1 = new CertificadoA1(path, password);
		
		Path arq = Path.of(path);
		
		if (!Files.exists(arq)) {
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Não foi possível encontrar o arquivo informado.");
			return null;
		} 
		
		if (!Files.isReadable(arq)) {
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "O arquivo não possui permisão para leitura");
			return null;
		}
		
		certificadoA1.setNome(arq.getFileName().toString());
		
		try {
			var keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(Files.newInputStream(arq), password.toCharArray());

			certificadoA1.setMapCertificateAndKey(getX509CertificateFromFile(keyStore, password));
		} catch (KeyStoreException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Não foi possível carregar o certificado informado.");
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar o certificado A1", "Favor verifique sua senha.", false);
		}
		
		return certificadoA1;
		
	}
	
	public static Set<Store> buscarStoriesFromSystem() {
		
		Set<Store> certificados = new HashSet<Store>();
		
		try {
			KeyStore keyStore = getKeyStoreFromSystem();
			keyStore.load(null, null);
			
			for (final Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();) 
				certificados.add(new Store(e.nextElement()));
			
		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao carregar os certificados", "Não foi possível acessar a repositório de certificados do Windows.");
		} catch (StoreException e) {
			Utils.showErrorDialog("Erro ao carregar os certificados", e.getMessage());
		}
		
		return certificados;
	}
	
	public static Map<X509Certificate, PrivateKey> getX509CertificateFromStore(CertificadoFromStore selectedCertificate) {
		var certificateKeyMap = new HashMap<X509Certificate, PrivateKey>();
		 
		try {
			KeyStore keyStore = getKeyStoreFromSystem();
			keyStore.load(null, null);
			
			final PrivateKey privateKey = (PrivateKey) keyStore.getKey(selectedCertificate.getNome(), null);
			final X509Certificate certificate = (X509Certificate) keyStore.getCertificate(selectedCertificate.getNome());

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
		
		var isWindows = Contants.SYSTEM_NAME.indexOf(Contants.WINDOWS) >= 0;
		var isMacOs = Contants.SYSTEM_NAME.indexOf(Contants.MAC) >= 0;
		
		KeyStore keyStore = null;
		
		if (isWindows)
			keyStore = KeyStore.getInstance(Contants.WINDOWS_KEY_STORE_TYPE, Contants.WINDOWS_KEY_STORE_PROVIDER);
		else if (isMacOs)
			keyStore = KeyStore.getInstance(Contants.MAC_KEY_STORE_TYPE, Contants.MAC_KEY_STORE_PROVIDER);
		
		if (keyStore == null) 
			throw new StoreException("Sistema operacional" + Contants.SYSTEM_NAME + " não suportado!");

		return keyStore;
	}
	
	
}
