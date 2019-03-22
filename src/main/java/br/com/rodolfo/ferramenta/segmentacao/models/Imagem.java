package br.com.rodolfo.ferramenta.segmentacao.models;

import java.io.ByteArrayInputStream;

import org.bytedeco.javacpp.opencv_core.Mat;

import br.com.rodolfo.ferramenta.segmentacao.utils.opencv.ImagemOpenCV;

/**
 * Imagem
 */
public class Imagem {

    private String nome;
    final private Mat imagem;

    public Imagem(Mat imagem) {

        this.imagem = imagem;
    }


    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Mat getImagem() {
        return this.imagem;
    }

    public int getRows() {
        return this.imagem.rows();
    }

    public int getCols() {
        return this.imagem.cols();
    }

    public ByteArrayInputStream getImagemBytes() {

        return ImagemOpenCV.matParaByteImputStream(this.imagem);
    }
    
}