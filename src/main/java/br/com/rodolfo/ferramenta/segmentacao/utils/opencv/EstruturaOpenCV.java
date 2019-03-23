package br.com.rodolfo.ferramenta.segmentacao.utils.opencv;

import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_CROSS;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;;

/**
 * EstruturaOpenCV
 */
public class EstruturaOpenCV {

    /**
     * Não permitir instânciação da classe
     */
    private EstruturaOpenCV() {}

    /**
     * Verificador da lógica de esqueletização
     * 
     * @param condicao
     * @return 1 ou 0
     */
    private static int verificador(boolean condicao) {
        
        return condicao ? 1 : 0;
    }

    /**
     * Realiza a erosao da imagem pelas regras de esqueletização
     * 
     * @param img
     * @param iter
     */
    private static void afinar(Mat img, int iter) {
        
        Mat aux = Mat.ones(img.size(), img.type()).asMat();

        UByteRawIndexer imgIndice = img.createIndexer();
        UByteRawIndexer auxIndice = aux.createIndexer();

        for(int row = 1; row < img.rows()-1; row++) {
            for(int col = 1; col < img.cols()-1; col++) {

                int p2 = imgIndice.get(row-1, col);
                int p3 = imgIndice.get(row-1, col+1);
                int p4 = imgIndice.get(row, col+1);
                int p5 = imgIndice.get(row+1, col+1);
                int p6 = imgIndice.get(row+1, col);
                int p7 = imgIndice.get(row+1, col-1);
                int p8 = imgIndice.get(row, col-1);
                int p9 = imgIndice.get(row-1, col-1);

                int a = verificador((p2 == 0 && p3 == 1)) + verificador((p3 == 0 && p4 == 1)) +
                        verificador((p4 == 0 && p5 == 1)) + verificador((p5 == 0 && p6 == 1)) +
                        verificador((p6 == 0 && p7 == 1)) + verificador((p7 == 0 && p8 == 1)) +
                        verificador((p8 == 0 && p9 == 1)) + verificador((p9 == 0 && p2 == 1));

                int b = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;

                int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
                int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);

                if ((a == 1) && (b >= 2 && b <= 6) && m1 == 0 && m2 == 0) {
                    auxIndice.put(row, col, 0);
                }
                    
            }
        }

        imgIndice.release();
        auxIndice.release();

        opencv_core.bitwise_and(aux, img, img);
    }

    /**
     * Realiza erosão da imagem através do algoritmo do esqueleto
     * 
     * Algoritmo do Esqueleto (Skeleton Zhang-Suen)
     * A Fast Parallel Algorithm for Thinning Digital Patterns 
     * Código adaptado do repositório:
     * https://github.com/krishraghuram/Zhang-Suen-Skeletonization/blob/master/skeletonization.hpp
     * 
     * @param imagem
     * @param area
     * @return esqueletoda imagem
     */
    public static Mat esqueleto(Mat imagem, double area) {
        
        Mat clone = imagem.clone();
        Mat valor = new Mat(imagem.size(), imagem.type(), Scalar.WHITE);
        
        double areaFigura = OperacaoOpenCV.calcularArea(clone, 255) * area;
        
        opencv_core.divide(clone, valor, clone);
        
        do {
            
            afinar(clone, 0);
            afinar(clone, 1);
            
        } while (OperacaoOpenCV.calcularArea(clone, 1) > areaFigura);

        opencv_core.multiply(clone, valor, clone);
        
        return erosao(clone, 3);
    }

    /**
     * Realiza o preenchimento do contorno de uma imagem com a cor preta e ao redor com cor branca.
     * 
     * @param imagem
     * @param dimensao
     * @return imagem preenchida
     */
    public static Mat preenchimentoContornos(Mat imagem, int dimensao) {

        Mat resposta = new  Mat(imagem.size(), imagem.type(), Scalar.BLACK);
        MatVector contornos = new MatVector();

        opencv_imgproc.findContours(dilatacao(imagem, dimensao), contornos, new Mat(), RETR_TREE, CHAIN_APPROX_SIMPLE);

        for(Mat contorno : contornos.get()) {

            opencv_imgproc.fillPoly(resposta, new MatVector(contorno), Scalar.WHITE);
        }

        return resposta;
    }

    /**
     * Realiza a erosao da imagem
     * 
     * @param imagem
     * @param dimElemEstruturante
     * @return imagem erodida
     */
    public static Mat erosao(Mat imagem, int dimElemEstruturante) {

        Mat dst = new Mat();
        Mat elemEstruturante = opencv_imgproc.getStructuringElement(MORPH_CROSS, new Size(dimElemEstruturante, dimElemEstruturante));
        
        opencv_imgproc.erode(imagem, dst, elemEstruturante);

        return dst;
    }

    /**
     * Realiza a erosao da aimagem
     * 
     * @param imagem
     * @param dimElemEstruturante
     * @return imagem dilatada
     */
    public static Mat dilatacao(Mat imagem, int dimElemEstruturante) {

        Mat dst = new Mat();
        Mat elemEstruturante = opencv_imgproc.getStructuringElement(MORPH_CROSS, new Size(dimElemEstruturante, dimElemEstruturante));
        
        opencv_imgproc.dilate(imagem, dst, elemEstruturante);

        return dst;
    }
    
}