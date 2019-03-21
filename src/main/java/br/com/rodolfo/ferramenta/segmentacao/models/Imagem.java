package br.com.rodolfo.ferramenta.segmentacao.models;

import org.bytedeco.javacpp.opencv_core.Mat;

/**
 * Imagem
 */
public class Imagem {

    private String nome;
    private Mat imagemOpenCV;

    public Imagem() {}


    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Mat getImagemOpenCV() {
        return this.imagemOpenCV;
    }

    public void setImagemOpenCV(Mat imagemOpenCV) {
        this.imagemOpenCV = imagemOpenCV;
    }

    public int getRows() {
        return this.imagemOpenCV.rows();
    }

    public int getCols() {
        return this.imagemOpenCV.cols();
    }
    
}