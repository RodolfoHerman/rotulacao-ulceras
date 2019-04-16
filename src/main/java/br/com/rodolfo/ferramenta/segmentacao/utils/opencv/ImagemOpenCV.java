package br.com.rodolfo.ferramenta.segmentacao.utils.opencv;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelLSC;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSLIC;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

/**
 * ImagemOpenCV
 */
public class ImagemOpenCV {

    /**
     * Não permitir instânciação da classe
     */
    private ImagemOpenCV() {}
    
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
        
        JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(matParaByteImputStream(img).readAllBytes())));
    }

    /**
     * Transforma imagem Mat para array de bytes
     * 
     * @param img
     * @return bytes
     */
    public static ByteArrayInputStream matParaByteImputStream(Mat img) {

        byte[] buf = new byte[(int)img.elemSize() * img.cols() * img.rows()];
        opencv_imgcodecs.imencode(".jpg", img, buf);

        return new ByteArrayInputStream(buf);
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

    /**
     * Cria uma imagem de contornos a partir de uma conjunto (Set<Point>) de pontos.
     * 
     * @param size
     * @param pontosDesenhados
     * @return imagem de contornos
     */
    public static Mat desenharContornos(Size size, List<List<Point>> pontosDesenhados) {

        Mat imagem = new Mat(size, opencv_core.CV_8UC1, Scalar.BLACK);

        pontosDesenhados.stream().forEach(lista -> {

            for (int atual = 0, next = 1; next != 0; atual++) {
                next = (atual + 1) % lista.size();
                Point ini = lista.get(atual);
                Point fim = lista.get(next);
                opencv_imgproc.line(imagem, ini, fim, Scalar.WHITE);
            }

        });

        return imagem;
    }

    /**
     * Executa a segmentação pelo método grabCut
     * 
     * @param imagem
     * @param mascara
     * @return imagem segmentada
     */
    public static Mat executarGrabCut(Mat imagem, Mat mascara) {
        
        Mat bgdModel = new Mat();
        Mat fgdModel = new Mat();
        Rect rect    = new Rect();

        Mat clone = mascara.clone();

        Mat aux1 = new Mat(mascara.size(), opencv_core.CV_8UC1);
        Mat aux2 = new Mat(mascara.size(), opencv_core.CV_8UC1);

        Mat comparador1 = new Mat(1, 1, opencv_core.CV_8U, new Scalar(3.0));
        Mat comparador2 = new Mat(1, 1, opencv_core.CV_8U, new Scalar(1.0));

        int iterCount = 5;
        
        opencv_imgproc.grabCut(imagem, clone, rect, bgdModel, fgdModel, iterCount, opencv_imgproc.GC_INIT_WITH_MASK); 

        opencv_core.compare(clone, comparador1, aux1, opencv_core.CMP_EQ); 
        opencv_core.compare(clone, comparador2, aux2, opencv_core.CMP_EQ); 
        
        Mat foreground = new Mat(imagem.size(), imagem.type(), Scalar.WHITE);
        
        imagem.copyTo(foreground, aux1);
        imagem.copyTo(foreground, aux2);

        return foreground;
    }

    /**
     * Cria a máscara colorida do GrabCut para humanos enxergar
     * 
     * @param mascara
     * @return mascara
     */
    public static Mat criarMascaraGrabCutVisual(Mat mascara) {
        
        Mat resp = new Mat(mascara.size(), opencv_core.CV_8UC3, Scalar.WHITE);

        UByteRawIndexer indResp = resp.createIndexer();
        UByteRawIndexer indGrab = mascara.createIndexer();

        int[] ulcera    = {0, 165, 255};
        int[] provavel  = {52, 80, 255};
        int[] naoUlcera = {255, 144, 30};    

        for(int row = 0; row < mascara.rows(); row++) {
            for(int col = 0; col < mascara.cols(); col++) {

                if(indGrab.get(row, col) == opencv_imgproc.GC_PR_FGD) {

                    indResp.put(row, col, provavel);
                }

                if(indGrab.get(row, col) == opencv_imgproc.GC_FGD) {

                    indResp.put(row, col, ulcera);
                }

                if(indGrab.get(row, col) == opencv_imgproc.GC_BGD) {

                    indResp.put(row, col, naoUlcera);
                }

            }
        }

        indResp.release();
        indGrab.release();

        return resp;
    }

    /**
     * Realiza a segmentação por superpixels através do método LSC. 
     * O parâmetro iteracoes indica a quantidade de interações necessárias para formar o superpixel
     * O parâmetro 'tamanho' indica o tamanho aproximado desejado dos superpixels.
     * O parâmetro 'taxa' indica o fator de compactação dos superpixels, quanto menor for
     * mais as bordas da região se adaptará às curvas com tonalidades diferentes.
     * 
     * @param imagem
     * @param iteracoes
     * @param tamanho
     * @param taxa
     * @return
     */
    public static SuperpixelLSC segmentarSuperpixelLSC(Mat imagem, int iteracoes, int tamanho, float taxa) {
    
        Mat imagemLab = new Mat();

        opencv_imgproc.medianBlur(imagem, imagemLab, 3);
        opencv_imgproc.cvtColor(imagemLab, imagemLab, opencv_imgproc.COLOR_BGR2Lab);

        SuperpixelLSC lsc = opencv_ximgproc.createSuperpixelLSC(
            imagemLab, tamanho, taxa
        );

        lsc.iterate(iteracoes);
        lsc.enforceLabelConnectivity(tamanho - 1);

        return lsc;
    }


    /**
     * Realiza a segmentação por superpixels através do método LSC. 
     * O parâmetro iteracoes indica a quantidade de interações necessárias para formar o superpixel
     * O parâmetro 'tamanho' indica o tamanho aproximado desejado dos superpixels.
     * O parâmetro 'regra' indica o fator de compactação dos superpixels, quanto menor for
     * mais as bordas da região se adaptará às curvas com tonalidades diferentes.
     * 
     * @param imagem
     * @param iteracoes
     * @param tamanho
     * @param regra
     * @return
     */
    public static SuperpixelSLIC segmentarSuperpixelSLIC(Mat imagem, int iteracoes, int tamanho, int regra) {
        
        Mat imagemLab = new Mat();

        opencv_imgproc.medianBlur(imagem, imagemLab, 3);
        opencv_imgproc.cvtColor(imagemLab, imagemLab, opencv_imgproc.COLOR_BGR2Lab);

        SuperpixelSLIC slic = opencv_ximgproc.createSuperpixelSLIC(
            imagem, opencv_ximgproc.SLIC, tamanho, regra
        );

        slic.iterate(iteracoes);
        slic.enforceLabelConnectivity(tamanho - 1);

        return slic;
    }

}