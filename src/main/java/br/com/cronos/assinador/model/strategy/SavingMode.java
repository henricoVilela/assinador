package br.com.cronos.assinador.model.strategy;

/**
 * Modo de salvamento da assinatura
 * LOCAL - Usuario seleciona os arquivos e o assinador os sobreescrevem com os documentos assinados
 * SEND_TO_SERVICE - Busca em um endereço o documento assina e os envia para url informada 
 * PAH_TO_PATH - Recebe o caminho do arquivo como parametro, e o assina
 */
public enum SavingMode {
	LOCAL,
	SEND_TO_SERVICE,
	SAVE_TO_PATH
}
