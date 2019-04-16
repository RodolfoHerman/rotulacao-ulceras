package br.com.rodolfo.ferramenta.segmentacao.utils.opencv;

import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_CROSS;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;

import java.util.Arrays;
import java.util.List;

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
    public static Mat preencherContornos(Mat imagem, int dimensao) {

        Mat resposta = new  Mat(imagem.size(), imagem.type(), Scalar.BLACK);

        encontrarContornos(imagem, dimensao)
            .stream()
            .forEach(img -> opencv_imgproc.fillPoly(resposta, new MatVector(img), Scalar.WHITE));

        return resposta;
    }

    /**
     * Realiza o preenchimento do contorno em uma imagem previamente criada
     * 
     * @param imagem
     * @param contorno
     * @param corPreenchimento
     * @param corFundo
     * @return imagem contorno
     */
    public static Mat preencherContorno(Mat imagem, Mat contorno, Scalar corPreenchimento) {
        
        Mat resposta = imagem.clone();
        opencv_imgproc.fillPoly(resposta, new MatVector(contorno), corPreenchimento);

        return resposta;
    }

    /**
     * Realiza o preenchimento do contorno em uma imagem nova com as cores especificadas
     * 
     * @param size
     * @param contorno
     * @param corPreenchimento
     * @param corFundo
     * @return imagem contorno
     */
    public static Mat preencherContorno(Size size, Mat contorno, Scalar corPreenchimento, Scalar corFundo) {
        
        Mat resposta = new Mat(size, opencv_core.CV_8U, corFundo);
        opencv_imgproc.fillPoly(resposta, new MatVector(contorno), corPreenchimento);

        return resposta;
    }

    /**
     * Realiza o preenchimento com a cor desejada na imagem1 de acordo com a figura da imagem2 e o comparador da figura.
     * Imagem necessariamente tem que ser opencv_core.CV_8U
     * 
     * @param imagem1
     * @param imagem2
     * @param corPreenchimento
     * @param comparador
     */
    public static void preencherContorno(Mat imagem1, Mat imagem2, int corPreenchimento, int comparador) {
        
        UByteRawIndexer indice1 = imagem1.createIndexer();
        UByteRawIndexer indice2 = imagem2.createIndexer();

        for(int row = 0; row < imagem1.rows(); row++) {
            for(int col = 0; col < imagem1.cols(); col++) {

                if(indice2.get(row, col) == comparador) {

                    indice1.put(row, col, corPreenchimento);
                }
            }
        }

        indice1.release();
        indice2.release();
    }

    /**
     * Encontra todos os contornos de figuras existentes na imagem. O parâmetro 'dimensao'
     * é o tamanho do elemento estruturante, ex.: dimensao = 3 -> 3x3
     * 
     * @param imagem
     * @param dimensao
     * @return Lista de Mat
     */
    public static List<Mat> encontrarContornos(Mat imagem, int dimensao) {
        
        MatVector contornos = new MatVector();

        opencv_imgproc.findContours(dilatacao(imagem, dimensao), contornos, new Mat(), RETR_TREE, CHAIN_APPROX_SIMPLE);

        return Arrays.asList(contornos.get());
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


    /**
     * Cria a máscara de entrada para o método GrabCut realizar a segmentação. 
     * O parâmetro 'dimensao' é o tamanho do elemento estruturante, ex.: dimensao = 3 -> 3x3.
     * O parâmetro 'area' é a porcentagem da área que se deseja manter ao fazer a erosão da área original.
     * 
     * @param imagemBinaria
     * @param dimensao
     * @param area
     * @return máscara
     */
    public static Mat criarMascaraGrabCut(Mat imagemBinaria, int dimensao, double area) {

        Mat mascara = new Mat(imagemBinaria.size(), opencv_core.CV_8U, new Scalar(Integer.valueOf(opencv_imgproc.GC_BGD).doubleValue()));
        Size size = imagemBinaria.size();
        
        encontrarContornos(imagemBinaria, dimensao)
            .stream()
            .filter(contorno -> opencv_imgproc.contourArea(contorno) >= 30)
            .forEach(contorno -> {

                //Criar contorno preenchido para o algoritmo do esqueleto
                Mat temp = preencherContorno(size, contorno, Scalar.WHITE, Scalar.BLACK);
                Mat esqu = esqueleto(temp, area);

                //Criar máscara parcial a partir do contorno
                //Mat parc = preencherContorno(size, contorno, new Scalar(Double.valueOf(opencv_imgproc.GC_PR_FGD)), new Scalar(Double.valueOf(opencv_imgproc.GC_BGD)));
                Mat parc = new Mat(size, opencv_core.CV_8U, new Scalar(Double.valueOf(opencv_imgproc.GC_BGD)));
                preencherContorno(parc, temp, opencv_imgproc.GC_PR_FGD, 255);
                
                //Criar a máscara para o grabCut
                preencherContorno(parc, esqu, opencv_imgproc.GC_FGD, 255);
                opencv_core.add(mascara, parc, mascara);
            }
        );

        return mascara;
    }
    
}