package br.com.rodolfo.ferramenta.segmentacao.utils.opencv;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_CROSS;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;
import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;;

/**
 * EstruturaOpenCV
 */
public class EstruturaOpenCV {

    /**
     * Não permitir instânciação da classe
     */
    private EstruturaOpenCV() {}


    /**
     * Realiza o preenchimento do contorno de uma imagem com a cor preta e ao redor com cor branca.
     * 
     * @param imagem
     * @param dimensao
     * @return imagem preenchida
     */
    public static Mat preenchimentoContornos(Mat imagem, int dimensao) {

        Mat dst = new Mat();
        Mat resposta = Mat.ones(imagem.size(), imagem.type()).asMat();
        Mat elemEstruturante = opencv_imgproc.getStructuringElement(MORPH_CROSS, new Size(dimensao, dimensao));
        MatVector contornos = new MatVector();

        opencv_imgproc.dilate(imagem, dst, elemEstruturante);
        opencv_imgproc.findContours(dst, contornos, new Mat(), RETR_TREE, CHAIN_APPROX_SIMPLE);

        opencv_imgproc.fillPoly(resposta, contornos, Scalar.BLACK);

        return resposta;
    }
    
}