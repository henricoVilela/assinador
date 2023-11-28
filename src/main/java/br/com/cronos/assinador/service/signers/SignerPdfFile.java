package br.com.cronos.assinador.service.signers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.util.stream.Collectors;

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
import javafx.application.Platform;

public abstract class SignerPdfFile {

	/**
	 * Retorar os bytes de saida, do pdf assinado
	 * @param bytes
	 * @param certificate
	 * @param privateKey
	 * @return pdf assinado
	 * @throws StoreException
	 */
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
			Platform.runLater(
					() -> Utils.showErrorDialog("Erro ao ler o pdf", "Não foi possivel manipular o arquivo pdf")
			);
		} catch (GeneralSecurityException | RuntimeException e) {
			e.printStackTrace();
			Platform.runLater(
					() -> Utils.showErrorDialog("Erro ao assinar o pdf", "Não foi possivel realizar a assinatura do pdf, verifique o certificado selecionado")
			);
		}
		
		return baos;

	}
	
	
	/**
	 * De acordo com a lista de arquivos que foram salvos com sucesso, ou arquivos que não foi possível concluir a operação.
	 * Notificamos o usuario.
	 * Se não arquivos não salvos então ocorreu tudo bem. Caso contrario pode ter sido salvo alguns e outros falharam, então
	 * exibe o erro dos que falharam e o sucesso dos que deu certo.
	 * @param filesSent
	 * @param filesNotSent
	 */
	protected void notificar(List<String> filesSent, List<String> filesNotSent) {
		
		if (filesNotSent.isEmpty()) {
			Platform.runLater(() -> Utils.showInfoDialog("Assinaturas Realizadas", "Todos os arquivos foram assinados e enviado com sucesso"));
			return;
		}
		
		Platform.runLater(() -> {
			if (!filesNotSent.isEmpty()) {
				var filesNotSentUri = filesNotSent.stream().collect(Collectors.joining(" \n"));
				Utils.showErrorDialog("Erro ao enviar o arquivo", "Não foi possivel enviar o(s) arquivo(s): \n" + filesNotSentUri, false);
				filesNotSent.clear();
			}
			
			if (!filesSent.isEmpty())  {
				var filesSentUri = filesSent.stream().collect(Collectors.joining(", "));
				Utils.showInfoDialog("Assinaturas Realizadas", "Arquivo(s): " + filesSentUri + " assinado(s) e enviado(s) com sucesso");
				filesSent.clear();
			}
		});
		
		
	}
}
