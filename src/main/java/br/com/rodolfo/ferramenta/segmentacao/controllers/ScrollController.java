package br.com.rodolfo.ferramenta.segmentacao.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * ScrollController
 */
public class ScrollController {

    private Timeline scrollTimeVertical = new Timeline();
    private Timeline scrollTimeHorizontal = new Timeline();
    private final ScrollPane scrollPane;
    private final Pane paneCanvas;
    private final double speed = 4.0;
    private final int bound = 20;
    private double scrollDirectionVertical;
    private double scrollDirectionHorizontal;

    public ScrollController(ScrollPane scrollPane, Pane paneCanvas) {

        this.scrollPane = scrollPane;
        this.paneCanvas = paneCanvas;
        setUpScrolling();
    }

    public void realizarEventoScroll(final MouseEvent event) {

        if (event.getSceneY() > (scrollPane.getBoundsInLocal().getHeight() - (scrollPane.getBoundsInLocal().getHeight()/bound))) {
            scrollDirectionVertical = 1.0 / (paneCanvas.getHeight()/speed);
        } else if (event.getSceneY() < (scrollPane.getBoundsInLocal().getHeight()/bound)){
            scrollDirectionVertical = -1.0 / (paneCanvas.getHeight()/speed);
        }

        if (event.getSceneX() > (scrollPane.getBoundsInLocal().getWidth() - (scrollPane.getBoundsInLocal().getWidth()/bound))) {
            scrollDirectionHorizontal = 1.0 / (paneCanvas.getWidth()/speed);
        } else if (event.getSceneX() < (scrollPane.getBoundsInLocal().getWidth()/bound)){
            scrollDirectionHorizontal = -1.0 / (paneCanvas.getWidth()/speed);
        } 

        if ((event.getSceneY() <= (scrollPane.getBoundsInLocal().getHeight() - (scrollPane.getBoundsInLocal().getHeight()/bound))) && 
             event.getSceneY() >= (scrollPane.getBoundsInLocal().getHeight()/bound)) {
            scrollTimeVertical.stop();
        } else {
            scrollTimeVertical.play();
        }

        if ((event.getSceneX() <= (scrollPane.getBoundsInLocal().getWidth() - (scrollPane.getBoundsInLocal().getWidth()/bound))) && 
             event.getSceneX() >= (scrollPane.getBoundsInLocal().getWidth()/bound)) {
            scrollTimeHorizontal.stop();
        } else {
            scrollTimeHorizontal.play();
        }
    }

    private void setUpScrolling () {

        scrollTimeVertical.setCycleCount(Timeline.INDEFINITE);
        scrollTimeHorizontal.setCycleCount(Timeline.INDEFINITE);

        scrollTimeVertical.getKeyFrames().add(new KeyFrame(Duration.millis(8), 
                                                               "Scroll", 
                                                               ActionEvent ->  
                dragScrollVertical()));
        
        scrollTimeHorizontal.getKeyFrames().add(new KeyFrame(Duration.millis(4), 
                                                               "Scroll", 
                                                               ActionEvent -> 
                dragScrollHorizontal()));        
        
    }

    public void stopScroll() {
        
        scrollTimeVertical.stop();
        scrollTimeHorizontal.stop();
    }

    private void dragScrollVertical () {
    
        double newValue = scrollPane.getVvalue() + scrollDirectionVertical;
        newValue = Math.min(newValue, 1.0);
        newValue = Math.max(newValue, 0.0);
        scrollPane.setVvalue(newValue);

    }
    
    private void dragScrollHorizontal () {
    
        double newValue = scrollPane.getHvalue() + scrollDirectionHorizontal;
        newValue = Math.min(newValue, 1.0);
        newValue = Math.max(newValue, 0.0);
        scrollPane.setHvalue(newValue);

    } 

}