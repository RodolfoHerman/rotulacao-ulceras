package br.com.rodolfo.ferramenta.segmentacao.models;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core.Mat;

import br.com.rodolfo.ferramenta.segmentacao.utils.opencv.ImagemOpenCV;

/**
 * Imagem
 */
public class Imagem {

    private String nome;
    private Mat superpixelLabels;
    private Mat superpixelContornos;
    private Map<Integer,Superpixel> superpixels = new HashMap<>();
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

    public Mat getSuperpixelLabes() {

        return this.superpixelLabels;
    }

    public void setSuperpixelLabes(Mat superpixelLabels) {

        this.superpixelLabels = superpixelLabels;
    }

    public Mat getSuperpixelContornos() {

        return this.superpixelContornos;
    }

    public void setSuperpixelContornos(Mat superpixelContornos) {

        this.superpixelContornos = superpixelContornos;
    }

    public Map<Integer,Superpixel> getSuperpixels() {

        return this.superpixels;
    }

    public void putSuperpixel(Integer rotulo, Superpixel superpixel) {

        this.superpixels.put(rotulo, superpixel);
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

    public ByteArrayInputStream getImagemContornoBytes() {

        return ImagemOpenCV.matParaByteImputStream(this.superpixelContornos);
    }
    
}