package br.com.rodolfo.ferramenta.segmentacao.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import br.com.rodolfo.ferramenta.segmentacao.utils.Metodos;
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
    public Imagem abrirImagem(String caminho) {
        
        log.info("Abrindo imagem no caminho : {}", caminho);

        Imagem imagem = new Imagem();
        imagem.setImagemOpenCV(ImagemOpenCV.abrir(caminho));
        imagem.setNome(Metodos.extrairNomeArquivo(caminho));

        return imagem;
    }

    /**
     * Abrir imagem no caminho especificado
     * 
     * @param largura
     * @param altura
     * @param caminho
     * @return img
     */
    public Imagem abrirImagem(int largura, int altura, String caminho) {
        
        log.info("Abrindo imagem no caminho : {}", caminho);

        Imagem imagem = new Imagem();
        imagem.setImagemOpenCV(ImagemOpenCV.amostrar(altura, largura, ImagemOpenCV.abrir(caminho)));
        imagem.setNome(Metodos.extrairNomeArquivo(caminho));

        return imagem;
    }
    
}