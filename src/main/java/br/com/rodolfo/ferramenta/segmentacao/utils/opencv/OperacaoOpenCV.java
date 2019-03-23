package br.com.rodolfo.ferramenta.segmentacao.utils.opencv;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;

/**
 * OperacaoOpenCV
 */
public class OperacaoOpenCV {

    /**
     * Não permitir instânciação da classe
     */
    private OperacaoOpenCV() {}

    /**
     * Calcula a área da figura binária
     * 
     * @param imagem
     * @param comparador
     * @return área
     */
    public static double calcularArea(Mat imagem, int comparador) {
        
        double area = 0.0;

        if(imagem.depth() <= 1) {

            UByteRawIndexer indice = imagem.createIndexer();
            int pixels = imagem.rows() * imagem.cols();

            for(int x = 0; x < pixels; x++) {
                
                if(indice.get(x) == comparador) {

                    area++;
                }
            }

            indice.release();
        }

        return area;
    }
    
}