package br.com.rodolfo.ferramenta.segmentacao.models;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.Point;

import br.com.rodolfo.ferramenta.segmentacao.models.enums.TecidoTipo;

/**
 * Superpixel
 */
public class Superpixel {

    public List<Point> pixels;
    public TecidoTipo tipo;
    
}