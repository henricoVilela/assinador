package br.com.cronos.assinador.exceptions;

public class LoadPdfFromServiceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LoadPdfFromServiceException(Exception ex) {
        super(ex);
    }

    public LoadPdfFromServiceException(String message) {
        super(message);
    }
}
