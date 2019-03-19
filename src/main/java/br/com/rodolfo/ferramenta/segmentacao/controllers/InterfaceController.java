package br.com.rodolfo.ferramenta.segmentacao.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class InterfaceController implements Initializable {
    
    @FXML
    private ScrollPane scrollpane;

    @FXML
    private Canvas paneCanvas;

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

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    





}
