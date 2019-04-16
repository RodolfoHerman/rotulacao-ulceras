package br.com.rodolfo.ferramenta.segmentacao.process;

import java.util.List;
import java.util.Optional;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelLSC;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSLIC;

import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import br.com.rodolfo.ferramenta.segmentacao.utils.opencv.EstruturaOpenCV;
import br.com.rodolfo.ferramenta.segmentacao.utils.opencv.ImagemOpenCV;
import javafx.concurrent.Task;

/**
 * TrabalhadoraSegmentacao
 */
public class TrabalhadoraSegmentacao extends Task<Optional<Imagem>>{

    private final List<List<Point>> pontosDesenhados;
    private final Imagem imagem;

    public TrabalhadoraSegmentacao(List<List<Point>> pontosDesenhados, Imagem imagem) {

        this.pontosDesenhados = pontosDesenhados;
        this.imagem = imagem;
    }


    @Override
    protected Optional<Imagem> call() throws Exception {
        
        int[] progresso  = {0, 1, 2, 3, 4};
        int maxProgresso = progresso.length;
        int andamento    = 0;

        Mat contornos = ImagemOpenCV.desenharContornos(imagem.getImagem().size(), pontosDesenhados);
        Mat contornoP = EstruturaOpenCV.preencherContornos(contornos, 3);
        Mat mascarGrab = EstruturaOpenCV.criarMascaraGrabCut(contornoP, 3, 0.65);

        ImagemOpenCV.mostrar(ImagemOpenCV.criarMascaraGrabCutVisual(mascarGrab));

        Mat seg = ImagemOpenCV.executarGrabCut(imagem.getImagem(), mascarGrab);

        Mat cont = new Mat();
        Mat labels = new Mat();
        SuperpixelSLIC slic = ImagemOpenCV.segmentarSuperpixelSLIC(seg, 400, 50, 20);
        slic.getLabels(labels);
        slic.getLabelContourMask(imagem.getImagem());

        ImagemOpenCV.mostrar(labels);
        ImagemOpenCV.mostrar(imagem.getImagem());

        
        return Optional.empty();
    }

    
}