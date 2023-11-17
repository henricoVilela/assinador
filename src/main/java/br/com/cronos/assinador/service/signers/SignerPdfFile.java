package br.com.cronos.assinador.service.signers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;

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
import br.com.cronos.assinador.util.Constants;
import br.com.cronos.assinador.util.Utils;

public abstract class SignerPdfFile {

	protected ByteArrayOutputStream getPdfAssinado(byte[] bytes, Certificate certificate, PrivateKey privateKey) throws StoreException {
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
		} catch (GeneralSecurityException | RuntimeException e) {
			e.printStackTrace();
			Utils.showErrorDialog("Erro ao assinar o pdf", "Não foi possivel realizar a assinatura do pdf, verifique o certificado selecionado");
		}
		
		return baos;

	}
}
