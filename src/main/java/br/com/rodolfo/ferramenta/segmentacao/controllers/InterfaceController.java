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

import org.bytedeco.javacpp.opencv_core.Point;

import br.com.rodolfo.ferramenta.segmentacao.MainApp;
import br.com.rodolfo.ferramenta.segmentacao.config.Configuracao;
import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import br.com.rodolfo.ferramenta.segmentacao.models.Superpixel;
import br.com.rodolfo.ferramenta.segmentacao.models.enums.TecidoTipo;
import br.com.rodolfo.ferramenta.segmentacao.process.TrabalhadoraRotulacao;
import br.com.rodolfo.ferramenta.segmentacao.process.TrabalhadoraSegmentacao;
import br.com.rodolfo.ferramenta.segmentacao.services.ImagemService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
    private MenuItem btnMenuAbrir;

    @FXML
    private MenuItem btnMenuSalvar;

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
    private boolean classificacao;
    private final Color corContorno = Color.rgb(255, 80, 75);
    private final Color corGranulacao = Color.rgb(255, 80, 75, 0.4);
    private final Color corEsfacelo = Color.rgb(255, 165, 0, 0.4);
    private final Color corEscara = Color.rgb(30, 144, 255, 0.4);
    private final Color corTransparente = Color.TRANSPARENT;

    private static final String PROPERTIES = "config.properties"; 
    private Configuracao configuracao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        graphicCanvasFG = canvasFG.getGraphicsContext2D();
        graphicCanvasBG = canvasBG.getGraphicsContext2D();

        paneCanvas.setDisable(true);
        btnProcessar.setDisable(true);
        btnMenuSalvar.setDisable(true);
        rotulacaoGrupoRadio.getToggles().forEach(radio -> {
            
           RadioButton no = (RadioButton) radio;
            no.setDisable(true);
        });

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

        paneCanvas.setDisable(true);
        btnProcessar.setDisable(true);
        btnMenuAbrir.setDisable(true);
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

                        paneCanvas.setDisable(false);
                        btnMenuAbrir.setDisable(false);
                        classificacao = true;
                        rotulacaoGrupoRadio.getToggles().forEach(radio -> {
            
                            RadioButton no = (RadioButton) radio;
                            no.setDisable(false);
                        });
                    }, 
                    () -> {
                        System.out.println("Imagem não foi processada");
                        paneCanvas.setDisable(false);
                    }
                );

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

        if(!classificacao) {

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
        
        
    }

    @FXML
    public void onMousePressed(MouseEvent event) {

        int x = Double.valueOf(event.getX()).intValue();
        int y = Double.valueOf(event.getY()).intValue();
        
        if(!classificacao) {

            qtdClicks++;
    
            scrollController.stopScroll();
    
            mousePressionadoPrevX = x;
            mousePressionadoPrevY = y;
    
            mousePressionado = new Point(x,y);
    
            pontosDesenhados.add(new ArrayList<>(Arrays.asList(mousePressionado)));

        } else {

            graphicCanvasFG.setLineWidth(1);
            
            rotulacaoGrupoRadio.getToggles().filtered(toggle -> toggle.isSelected()).stream().findFirst().ifPresent(radio -> {

                RadioButton no = (RadioButton) radio;

                paneCanvas.setDisable(true);
                btnProcessar.setDisable(true);
                btnMenuAbrir.setDisable(true);
                btnMenuSalvar.setDisable(true);
                TrabalhadoraRotulacao trabalhadoraRotulacao = new TrabalhadoraRotulacao(y, x, TecidoTipo.toEnum(no.getText()).getCodigo(), imagem.getSuperpixelLabes());

                resetarProgresso();
                this.barraProgresso.progressProperty().bind(trabalhadoraRotulacao.progressProperty());

                trabalhadoraRotulacao.setOnSucceeded(Event -> {

                    try {

                        Optional<Superpixel> superpixel = trabalhadoraRotulacao.get();
                        superpixel.ifPresentOrElse(processado -> {

                            colorirCanvasFG(processado.tecidoTipo, processado.coordenadas);
                            
                            resetarProgresso();

                            paneCanvas.setDisable(false);
                            btnMenuAbrir.setDisable(false);
                            btnMenuSalvar.setDisable(false);

                            imagem.putSuperpixel(processado.rotulo, processado);


                        }, () -> {

                            System.out.println("Falha ao processar Superpixel");

                        });

                    } catch (InterruptedException | ExecutionException e) {
                
                        e.printStackTrace();
                    }

                });

                Thread thread = new Thread(trabalhadoraRotulacao);
                thread.setDaemon(true);
                thread.start();
            });
        }
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
            chooser.setInitialDirectory(new File(caminho));
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
        graphicCanvasFG.setLineWidth(3.0);
        graphicCanvasFG.setStroke(corContorno);

        canvasBG.setWidth(imagem.getCols());
        canvasBG.setHeight(imagem.getRows());

        graphicCanvasBG.drawImage(image, 0, 0);

        canvasFG.toFront();
        paneCanvas.setDisable(false);
        classificacao = false;
        btnProcessar.setDisable(false);
        btnMenuSalvar.setDisable(true);
        rotulacaoGrupoRadio.getToggles().forEach(radio -> {
            
           RadioButton no = (RadioButton) radio;
            no.setDisable(true);
        });
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

    private void colorirCanvasFG(int tipoTecido, List<Point> pontos) {
        
        switch (tipoTecido) {
            
            case 0:
                graphicCanvasFG.setStroke(corTransparente);

            break;

            case 1:
                graphicCanvasFG.setStroke(corEscara);

            break;
        
            case 2:
                graphicCanvasFG.setStroke(corEsfacelo);

            break;

            case 3:
                graphicCanvasFG.setStroke(corGranulacao);

            break;
        }

        pontos.stream().forEach(ponto -> graphicCanvasFG.clearRect(ponto.x(), ponto.y(), 1, 1));
        pontos.stream().forEach(ponto -> graphicCanvasFG.strokeLine(ponto.x(), ponto.y(), ponto.x(), ponto.y()));
    }

}
