package br.com.rodolfo.ferramenta.segmentacao.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import br.com.rodolfo.ferramenta.segmentacao.MainApp;
import br.com.rodolfo.ferramenta.segmentacao.models.Imagem;
import br.com.rodolfo.ferramenta.segmentacao.services.ImagemService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.Pane;
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
    private FileChooser fileChooser;
    private String caminho;
    private ImagemService imagemService = new ImagemService();
    private Imagem imagem;
    private GraphicsContext graphicCanvasFG, graphicCanvasBG;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 

        graphicCanvasFG = canvasFG.getGraphicsContext2D();
        graphicCanvasBG = canvasBG.getGraphicsContext2D();
    }

    @FXML
    public void btnProcessarAction() {}

    @FXML
    public void onMouseDragged() {}

    @FXML
    public void onMousePressed() {}

    @FXML
    public void onMouseReleased() {}

    @FXML
    public void abrirImagemAction() {

        FileChooser chooser = getFileChooser();
        File file = chooser.showOpenDialog(MainApp.mainStage);

        if(file != null) {

            caminho = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
            txtCampoDiretorio.setText(file.getAbsolutePath());
            imagem = imagemService.abrirImagem(1024, 1024, file.getAbsolutePath());

            inicializarCanvas(imagem.getImagemBytes());
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

    private void inicializarCanvas(ByteArrayInputStream arrayInputStream) {
        
        Image image = new Image(arrayInputStream);

        graphicCanvasFG.clearRect(0, 0, image.getWidth(), image.getHeight());
        canvasFG.setWidth(imagem.getCols());
        canvasFG.setHeight(imagem.getRows());
        graphicCanvasFG.setLineWidth(2.0);

        canvasBG.setWidth(imagem.getCols());
        canvasBG.setHeight(imagem.getRows());

        graphicCanvasBG.drawImage(image, 0, 0);

        canvasFG.toFront();
    }

}
