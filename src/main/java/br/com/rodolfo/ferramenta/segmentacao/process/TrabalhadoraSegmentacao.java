package br.com.rodolfo.ferramenta.segmentacao.process;

import java.util.List;
import java.util.Optional;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSLIC;

import br.com.rodolfo.ferramenta.segmentacao.config.Configuracao;
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
    private final Configuracao configuracao;

    public TrabalhadoraSegmentacao(List<List<Point>> pontosDesenhados, Imagem imagem, Configuracao configuracao) {

        this.pontosDesenhados = pontosDesenhados;
        this.imagem = imagem;
        this.configuracao = configuracao;
    }


    @Override
    protected Optional<Imagem> call() throws Exception {
        
        int[] progresso  = {1, 2, 3, 4, 5, 6};
        int maxProgresso = progresso.length;
        int andamento    = 0;

        Mat contornosDesenho = ImagemOpenCV.desenharContornos(imagem.getImagem().size(), pontosDesenhados);
        updateProgress(progresso[andamento++], maxProgresso);
        
        Mat contornosPreenchidos = EstruturaOpenCV.preencherContornos(contornosDesenho, configuracao.morfologiaDimensaoKernel);
        updateProgress(progresso[andamento++], maxProgresso);
        
        Mat mascarGrab = EstruturaOpenCV.criarMascaraGrabCut(contornosPreenchidos, configuracao.grabcutEsqueletoKernel,configuracao.grabcutAreaEsqueleto);
        updateProgress(progresso[andamento++], maxProgresso);
        
        Mat segmentacao = ImagemOpenCV.executarGrabCut(imagem.getImagem(), mascarGrab);
        updateProgress(progresso[andamento++], maxProgresso);
        
        SuperpixelSLIC slic = ImagemOpenCV.segmentarSuperpixelSLIC(segmentacao, configuracao.superpixelIteracoes, configuracao.superpixelTamanho, configuracao.superpixelRegra);
        updateProgress(progresso[andamento++], maxProgresso);
        
        Mat contornos = new Mat();
        Mat labels    = new Mat();
        slic.getLabels(labels);
        slic.getLabelContourMask(contornos);
        
        this.imagem.setSuperpixelLabes(labels);
        this.imagem.setSuperpixelContornos(ImagemOpenCV.desenharContornosSuperpixels(imagem.getImagem(), contornos));
        updateProgress(maxProgresso, maxProgresso);

        Thread.sleep(1000);
        
        return Optional.of(this.imagem);
    }

    
}