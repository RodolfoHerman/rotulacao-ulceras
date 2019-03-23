package br.com.rodolfo.ferramenta.segmentacao.process;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.Point;

import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import javafx.concurrent.Task;

/**
 * TrabalhadoraSegmentacao
 */
public class TrabalhadoraSegmentacao extends Task<Imagem>{

    private final List<List<Point>> pontosDesenhados;
    private final Imagem imagem;

    public TrabalhadoraSegmentacao(List<List<Point>> pontosDesenhados, Imagem imagem) {

        this.pontosDesenhados = pontosDesenhados;
        this.imagem = imagem;
    }


    @Override
    protected Imagem call() throws Exception {
        
        int[] progresso  = {0, 1, 2, 3, 4};
        int maxProgresso = progresso.length;
        int andamento    = 0;
        
        return null;
    }

    
}