package br.com.rodolfo.ferramenta.segmentacao.utils.opencv;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

/**
 * Imagem
 */
public class Imagem {

    /**
     * Não permitir instânciação da classe
     */
    private Imagem() {}
    
    /**
     * Abrir uma imagem.
     * 
     * @param caminho
     * @return img
     */
    public static Mat abrir(String caminho) {
        
        return opencv_imgcodecs.imread(caminho);
    }

    /**
     * Mostrar imagem.
     * 
     * @param img
     */
    public static void mostrar(Mat img) {
        
        JOptionPane.showMessageDialog(null, new JLabel(matParaImageIcon(img)));
    }

    /**
     * Transforma uma Mat para ImageIcon
     * 
     * @param img
     * @return ImageIcon
     */
    private static ImageIcon matParaImageIcon(Mat img) {

        byte[] buf = new byte[(int)img.elemSize() * img.cols() * img.rows()];
        opencv_imgcodecs.imencode(".jpg", img, buf);

        return new ImageIcon(buf);
    }

    /**
     * Realiza o dump da imagem
     * CV_8U   0 - Byte
     * CV_8S   1 - Byte
     * CV_16U  2 - Integer
     * CV_16S  3 - Integer
     * CV_32S  4 - Integer
     * CV_32F  5 - Float
     * CV_64F  6 - Float
     * 
     * @param img
     */
    public static void dump(Mat img) {
        
        StringBuilder dados = new StringBuilder(); 

        int profundidade = img.depth();

        dados.append("Dimensão (linha/coluna) : ").append("\t").append(img.rows()).append("/").append(img.cols());
        dados.append(System.lineSeparator());
        dados.append("Tipo da imagem : ").append("\t").append(img.type());
        dados.append(System.lineSeparator());
        dados.append("Qtd canais da imagem : ").append("\t").append(img.channels());
        dados.append(System.lineSeparator());

        if(profundidade <= 1) {

            dados.append(dump_byte(img));
            
        } else if(profundidade <= 4) {
            
            dados.append(dump_integer(img));
            
        } else {
            
            dados.append(dump_float(img));
        }

        System.out.println(dados.toString());
    }
    
    /**
     * Realizar o dump da imagem Mat do tipo Byte
     * 
     * @param img
     * @return dados
     */
    private static String dump_byte(Mat img) {
        
        UByteRawIndexer indice = img.createIndexer();
        StringBuilder dados = new StringBuilder();

        for(int row = 0; row < img.rows(); row++) {
            for(int col = 0; col < img.cols(); col++) {

                dados.append(indice.get(row, col) + " ");
            }

            dados.append(System.lineSeparator());
        }

        indice.release();

        return dados.toString();
    }

    /**
     * Realizar o dump da imagem Mat do tipo Integer
     * 
     * @param img
     * @return dados
     */
    private static String dump_integer(Mat img) {
        
        IntRawIndexer indice = img.createIndexer();
        StringBuilder dados = new StringBuilder();

        for(int row = 0; row < img.rows(); row++) {
            for(int col = 0; col < img.cols(); col++) {

                dados.append(indice.get(row, col) + " ");
            }

            dados.append(System.lineSeparator());
        }

        indice.release();

        return dados.toString();
    }
    
    /**
     * Realizar o dump da imagem Mat do tipo Float
     * 
     * @param img
     * @return dados
     */
    private static String dump_float(Mat img) {
        
        FloatRawIndexer indice = img.createIndexer();
        StringBuilder dados = new StringBuilder();

        for(int row = 0; row < img.rows(); row++) {
            for(int col = 0; col < img.cols(); col++) {

                dados.append(indice.get(row, col) + " ");
            }

            dados.append(System.lineSeparator());
        }

        indice.release();

        return dados.toString();
    }

    /**
     * Amostra a imagem utilizando a interpolação Bicubica
     * 
     * @param altura
     * @param largura
     * @param img
     * @return img
     */
    public static Mat amostrar(int altura, int largura, Mat img) {
        
        Mat dst = new Mat();
        opencv_imgproc.resize(img, dst, new Size(largura, altura), 0, 0, opencv_imgproc.INTER_CUBIC);

        return dst;
    }

}