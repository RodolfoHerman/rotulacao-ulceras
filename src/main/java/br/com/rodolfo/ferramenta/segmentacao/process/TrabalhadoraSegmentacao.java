package br.com.rodolfo.ferramenta.segmentacao.process;

import java.awt.Point;
import java.util.Set;

import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import javafx.concurrent.Task;

/**
 * TrabalhadoraSegmentacao
 */
public class TrabalhadoraSegmentacao extends Task<Imagem>{

    private final Set<Point> pontosDesenhados;
    private final Imagem imagem;

    public TrabalhadoraSegmentacao(Set<Point> pontosDesenhados, Imagem imagem) {

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