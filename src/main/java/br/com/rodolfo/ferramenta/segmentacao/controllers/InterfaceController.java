package br.com.rodolfo.ferramenta.segmentacao.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import br.com.rodolfo.ferramenta.segmentacao.MainApp;
import br.com.rodolfo.ferramenta.segmentacao.config.Configuracao;
import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import br.com.rodolfo.ferramenta.segmentacao.process.TrabalhadoraSegmentacao;
import br.com.rodolfo.ferramenta.segmentacao.services.ImagemService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import org.bytedeco.javacpp.opencv_core.Point;

public class InterfaceController implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane paneCanvas;

    @FXML
    private Canvas canvasFG;

    @FXML
    private Canvas canvasBG;

    @FXML
    private ProgressBar barraProgresso;

    @FXML
    private Menu btnMenuArquivo;

    @FXML
    private Button btnProcessar;

    @FXML
    private TextField txtCampoDiretorio;

    @FXML
    private ToggleGroup rotulacaoGrupoRadio;

    @FXML
    private RadioButton radioGranulacao;

    @FXML
    private RadioButton radioEsfacelo;

    @FXML
    private RadioButton radioEscara;

    // Variáveis não FX
    private ScrollController scrollController;
    private FileChooser fileChooser;
    private String caminho;
    private ImagemService imagemService = new ImagemService();
    private Imagem imagem;
    private GraphicsContext graphicCanvasFG, graphicCanvasBG;
    private Point mousePressionado;
    private int mousePressionadoPrevX;
    private int mousePressionadoPrevY;
    private List<List<Point>> pontosDesenhados;
    private int qtdClicks;
    private final Color cor = Color.rgb(255, 80, 75);

    private static final String PROPERTIES = "config.properties"; 
    private Configuracao configuracao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        graphicCanvasFG = canvasFG.getGraphicsContext2D();
        graphicCanvasBG = canvasBG.getGraphicsContext2D();

        pontosDesenhados = new ArrayList<>();

        this.configuracao = new Configuracao();
        Properties prop = new Properties();
        
        try(InputStream input = InterfaceController.class.getClassLoader().getResourceAsStream(PROPERTIES)) {
            
            prop.load(input);

            this.configuracao.superpixelIteracoes = Integer.valueOf(prop.getProperty("superpixel.iteracoes"));
            this.configuracao.superpixelTamanho   = Integer.valueOf(prop.getProperty("superpixel.tamanho"));
            this.configuracao.superpixelRegra     = Integer.valueOf(prop.getProperty("superpixel.regra"));
            this.configuracao.superpixelTaxa      = Float.valueOf(prop.getProperty("superpixel.taxa"));
            
            this.configuracao.morfologiaDimensaoKernel = Integer.valueOf(prop.getProperty("morfologia.dimensao.kernel"));
            
            this.configuracao.grabcutAreaMinima      = Integer.valueOf(prop.getProperty("grabcut.area.minima"));
            this.configuracao.grabcutAreaEsqueleto   = Double.valueOf(prop.getProperty("grabcut.area.esqueleto"));
            this.configuracao.grabcutEsqueletoKernel = Integer.valueOf(prop.getProperty("grabcut.esqueleto.kernel"));
            
            this.configuracao.amostragemAltura  = Integer.valueOf(prop.getProperty("amostragem.altura"));
            this.configuracao.amostragemLargura = Integer.valueOf(prop.getProperty("amostragem.largura"));

        } catch(IOException e) {

            System.err.println(e);
        }

    }

    @FXML
    public void btnProcessarAction() {

        TrabalhadoraSegmentacao trabalhadoraSegmentacao = new TrabalhadoraSegmentacao(pontosDesenhados, imagem, configuracao);

        resetarProgresso();
        this.barraProgresso.progressProperty().bind(trabalhadoraSegmentacao.progressProperty());

        trabalhadoraSegmentacao.setOnSucceeded(Event -> {

            try {
                
                Optional<Imagem> img = trabalhadoraSegmentacao.get();
                img.ifPresentOrElse(
                    processada -> {
                        inserirImagemProcessadaCanvas(processada.getImagemContornoBytes());
                        resetarProgresso();
                    }, 
                    () -> System.out.println("Imagem não foi processada"));

            } catch (InterruptedException | ExecutionException e) {
                
                e.printStackTrace();
            }

        });


        Thread thread = new Thread(trabalhadoraSegmentacao);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {

        scrollController.realizarEventoScroll(event);
        
        int x = Double.valueOf(event.getX()).intValue();
        int y = Double.valueOf(event.getY()).intValue();

        graphicCanvasFG.strokeLine(mousePressionadoPrevX, mousePressionadoPrevY, x, y);

        // x = col
        // y = row
        mousePressionadoPrevX = x;
        mousePressionadoPrevY = y;

        pontosDesenhados.get(qtdClicks).add(new Point(x,y));
    }

    @FXML
    public void onMousePressed(MouseEvent event) {

        qtdClicks++;

        scrollController.stopScroll();
        
        int x = Double.valueOf(event.getX()).intValue();
        int y = Double.valueOf(event.getY()).intValue();

        mousePressionadoPrevX = x;
        mousePressionadoPrevY = y;

        mousePressionado = new Point(x,y);

        pontosDesenhados.add(new ArrayList<>(Arrays.asList(mousePressionado)));
    }

    @FXML
    public void onMouseReleased() {

        scrollController.stopScroll();

    }

    @FXML
    public void abrirImagemAction() {

        FileChooser chooser = getFileChooser();
        File file = chooser.showOpenDialog(MainApp.mainStage);

        if(file != null) {

            caminho = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
            txtCampoDiretorio.setText(file.getAbsolutePath());
            imagem = imagemService.abrirImagem(this.configuracao.amostragemLargura, this.configuracao.amostragemAltura, file.getAbsolutePath());

            resetar();
            inicializarCanvas(imagem.getImagemBytes());
            
            scrollController = new ScrollController(scrollPane, paneCanvas);
        }
    }

    @FXML
    public void salvarProgressoAction() {}

    @FXML
    public void fecharProgramaAction() {

        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void sobreProgramaAction() {}


    // Métodos complementares

    private FileChooser getFileChooser() {

        if(fileChooser == null) {

            fileChooser = new FileChooser ();
            fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")));
            fileChooser.setTitle("Abrir Imagem");
        } else {

            fileChooser.setInitialDirectory(new File(caminho));
        }

        return fileChooser;
    }

    private void inserirImagemProcessadaCanvas(ByteArrayInputStream arrayInputStream) {

        Image image = new Image(arrayInputStream);

        graphicCanvasBG.clearRect(0, 0, image.getWidth(), image.getHeight());
        canvasBG.setWidth(imagem.getCols());
        canvasBG.setHeight(imagem.getRows());

        graphicCanvasBG.drawImage(image, 0, 0);

        canvasFG.toFront();
    }

    private void inicializarCanvas(ByteArrayInputStream arrayInputStream) {
        
        Image image = new Image(arrayInputStream);

        graphicCanvasFG.clearRect(0, 0, image.getWidth(), image.getHeight());
        canvasFG.setWidth(imagem.getCols());
        canvasFG.setHeight(imagem.getRows());
        graphicCanvasFG.setLineWidth(2.0);
        graphicCanvasFG.setStroke(cor);

        canvasBG.setWidth(imagem.getCols());
        canvasBG.setHeight(imagem.getRows());

        graphicCanvasBG.drawImage(image, 0, 0);

        canvasFG.toFront();
    }

    private void resetar() {

        pontosDesenhados.clear();
        scrollPane.setVvalue(0);
        scrollPane.setHvalue(0);
        qtdClicks = -1;
    }

    private void resetarProgresso() {

        this.barraProgresso.progressProperty().unbind();
        this.barraProgresso.setProgress(0.0);
    }

}
