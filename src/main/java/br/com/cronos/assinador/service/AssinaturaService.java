package br.com.cronos.assinador.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

import br.com.cronos.assinador.exceptions.StoreException;
import br.com.cronos.assinador.model.Certificado;
import br.com.cronos.assinador.model.FileInfo;
import br.com.cronos.assinador.util.Constants;
import br.com.cronos.assinador.util.Utils;

public class AssinaturaService {

	public static void assinarDocumentos(List<FileInfo> files, Certificado cert) {
		try {
			gerarHashDosArquivos(files, cert);
		} catch (StoreException e) {
			Utils.showErrorDialog("Erro ao assinar os documentos", e.getMessage());
		}
	}

	private static void gerarHashDosArquivos(List<FileInfo> files, Certificado cert) throws StoreException {
		int INITIAL_SIZE = 32;
		byte[] bytesOfFile = null;
		for (FileInfo fileInfo : files) {
			bytesOfFile = Utils.convertFileToBytes(fileInfo.getFile());
			var baos = getPdfAssinado(bytesOfFile, cert.getCertificate(), cert.getPrivateKey());
			
			if (baos.size() > INITIAL_SIZE) {
				String caminhoArquivo = "E:\\var\\arquivos_de_log\\"+fileInfo.getName();
				try (FileOutputStream fos = new FileOutputStream(caminhoArquivo)) {
		            fos.write(baos.toByteArray());
		            System.out.println("Arquivo salvo com sucesso em: " + caminhoArquivo);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		}
	}

	private static ByteArrayOutputStream getPdfAssinado(byte[] bytes, Certificate certificate, PrivateKey privateKey) throws StoreException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			PdfReader reader = new PdfReader(bytes);
			PdfStamper stamper = null;

			boolean isPdfAssinado = Utils.isPDFAssinado(reader);

			if (isPdfAssinado) {
				stamper = PdfStamper.createSignature(reader, baos, '\000', null, true);
			} else {
				stamper = PdfStamper.createSignature(reader, baos, '\000');
			}
			 
			PdfSignatureAppearance signatureAppearance = stamper.getSignatureAppearance();
			signatureAppearance.setCertificate(certificate);
			signatureAppearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);

			PdfSignature signature = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
			signature.setReason(signatureAppearance.getReason());
			signature.setLocation(signatureAppearance.getLocation());
			signature.setContact(signatureAppearance.getContact());
			signature.setDate(new PdfDate(signatureAppearance.getSignDate()));
			
			signatureAppearance.setCryptoDictionary(signature);

			ExternalSignature externalSignature = null;
			if (privateKey instanceof RSAPrivateKey) 
				externalSignature = new PrivateKeySignature((RSAPrivateKey) privateKey, "SHA-256", "BC");
			else
				externalSignature = new PrivateKeySignature(privateKey, "SHA-256", Constants.getKeyStoreProvider());
			
			ExternalDigest digest = new BouncyCastleDigest();
	        
	        MakeSignature.signDetached(signatureAppearance, digest, externalSignature, new Certificate[]{certificate}, null, null, null, 0, CryptoStandard.CMS);

	        stamper.close();

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao ler o pdf", "Não foi possivel manipular o arquivo pdf");
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao assinar o pdf", "Não foi possivel realizar a assinatura do pdf");
		}
		
		return baos;

	}

}
