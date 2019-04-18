package br.com.rodolfo.ferramenta.segmentacao.process;

import java.util.Optional;

import org.bytedeco.javacpp.opencv_core.Mat;

import br.com.rodolfo.ferramenta.segmentacao.models.Superpixel;
import br.com.rodolfo.ferramenta.segmentacao.utils.opencv.EstruturaOpenCV;
import javafx.concurrent.Task;

/**
 * TrabalhadoraRotulacao
 */
public class TrabalhadoraRotulacao extends Task<Optional<Superpixel>>{

    private final int row;
    private final int col;
    private final int tipoTecido;
    private final Mat superpixelsRotulos;


    public TrabalhadoraRotulacao(int row, int col, int tipoTecido, Mat superpixelsRotulos) {

        this.row = row;
        this.col = col;
        this.tipoTecido = tipoTecido;
        this.superpixelsRotulos = superpixelsRotulos;
    }

    @Override
    protected Optional<Superpixel> call() throws Exception {

        int[] progresso  = {1, 2, 3};
        int maxProgresso = progresso.length;
        int andamento    = 0;

        Superpixel superpixel = new Superpixel();

        superpixel.tecidoTipo = tipoTecido;
        updateProgress(progresso[andamento++], maxProgresso);

        superpixel.rotulo = EstruturaOpenCV.encontrarRotulo(superpixelsRotulos, row, col);
        updateProgress(progresso[andamento++], maxProgresso);

        superpixel.coordenadas = EstruturaOpenCV.encontrarCoordenadasRotulo(superpixelsRotulos, superpixel.rotulo);
        updateProgress(maxProgresso, maxProgresso);

        Thread.sleep(500);
        
        return Optional.of(superpixel);
    }

    
}