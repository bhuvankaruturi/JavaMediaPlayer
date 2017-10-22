/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamediaplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author bhuva
 */
public class FXMLDocumentController implements Initializable {
    
    private String filePath;
    
    private MediaPlayer mediaPlayer;
    
    private boolean isPlaying = false;
    
    private Duration duration;
    
    
    @FXML
    private Slider timeSlider;
    
    @FXML
    private MediaView mediaView;
    
    @FXML 
    private Text textArea;
    
    @FXML
    private Text timeElapsed;
    
    @FXML
    private void handleOpenButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        
        FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("mp4 file", "*.mp4");
        
        fileChooser.getExtensionFilters().add(extension);
        
        File file = fileChooser.showOpenDialog(null);
        
        filePath = file.toURI().toString();
        
        if(filePath != null){
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            timeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable ov) {
                    if (timeSlider.isValueChanging()) {
                        // multiply duration by percentage calculated by slider position
                        mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                    }
                }
            });
            mediaView.setMediaPlayer(mediaPlayer);
            textArea.setDisable(false);
            textArea.setText(getFileName(filePath));
            timeElapsed.setDisable(false);
            if(mediaPlayer != null){
               mediaPlayer.setOnReady(new Runnable(){
                   @Override
                   public void run(){
                       duration = mediaPlayer.getMedia().getDuration();
                   }
               });
               mediaPlayer.play();
                mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                   @Override
                   public void invalidated(Observable ov) {
                       updateValues();
                   }
               });
            }
            isPlaying = true;   
        }
    }
    
    @FXML
    private void handlePlayButton(ActionEvent event){
        if(mediaPlayer != null){
            mediaPlayer.play();
            isPlaying = true;
        }
    }
    
    @FXML
    private void handlePauseButton(ActionEvent event){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event){
        //DO NOTHING
    }
    
    @FXML
    private void handleMouseClicked(MouseEvent event){
        if(mediaPlayer != null){
            if(isPlaying){
                mediaPlayer.pause();
                isPlaying = false;
            }
            else{
                mediaPlayer.play();
                isPlaying = true;
            }
        }
            
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        textArea.setDisable(true);
        timeElapsed.setDisable(true);
        /* borderPane.heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldPaneHeight, Number newPaneWidth){
               // setDimensions(mediaView);
            }
        }); */
        
    }   
    
    
    private String getFileName(String filePath){
        String fileName;
        char slash = '/';
        int start = 0;
        for(int i = 0; i<filePath.length(); i++){
            if (filePath.charAt(i) == slash){
                start = i;
            }
        }
        fileName = filePath.substring(start+1, filePath.length());
        fileName = fileName.replace("%20", " ");
        if (fileName.length() > 60){
            fileName = fileName.substring(0,60);
        }
        return fileName;
    }
    
    @SuppressWarnings("Convert2Lambda")
    private void updateValues(){
        if(timeSlider != null){
           Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   Duration currentTime = mediaPlayer.getCurrentTime();
                   timeSlider.setDisable(duration.isUnknown());
                   if(!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()){
                        timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                     timeElapsed.setText("Time Elapsed: " + formatTime(currentTime,duration));
               }
           });
        }
    }
    
    private String formatTime(Duration elapsed, Duration duration){
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
        
    }    
}
