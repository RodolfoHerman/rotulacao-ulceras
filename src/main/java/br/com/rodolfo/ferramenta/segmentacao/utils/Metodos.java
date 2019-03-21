package br.com.rodolfo.ferramenta.segmentacao.utils;

/**
 * Metodos
 */
public class Metodos {

    /**
     * Não permitir instânciação da classe
     */
    private Metodos() {}
    

    /**
     * Extrai o nome do arquivo a partir do caminho absoluto informado
     * 
     * @param caminho
     * @return nome
     */
    public static String extrairNomeArquivo(String caminho) {
        
        return caminho.substring(caminho.lastIndexOf("\\")+1);
    }

}