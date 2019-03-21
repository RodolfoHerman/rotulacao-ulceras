package br.com.rodolfo.ferramenta.segmentacao.services;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.rodolfo.ferramenta.segmentacao.utils.opencv.ImagemOpenCV;

/**
 * ImagemService
 */
public class ImagemService {

    private static final Logger log = LoggerFactory.getLogger(ImagemService.class);

    /**
     * Abrir imagem no caminho especificado
     * 
     * @param caminho
     * @return img
     */
    public Mat abrirImagem(String caminho) {
        
        log.info("Abrindo imagem no caminho : {}", caminho);
        return ImagemOpenCV.abrir(caminho);
    }

    /**
     * Abrir imagem no caminho especificado
     * 
     * @param largura
     * @param altura
     * @param caminho
     * @return img
     */
    public Mat abrirImagem(int largura, int altura, String caminho) {
        
        log.info("Abrindo imagem no caminho : {}", caminho);
        return ImagemOpenCV.amostrar(altura, largura, ImagemOpenCV.abrir(caminho));
    }
    
}