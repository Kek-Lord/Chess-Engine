import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundManager {
    MediaPlayer mediaPlayer;

    @FXML
    void playCapture() {
        String filename = "/soundEffects/capture.mp3";
        playSound(filename);
    }

    @FXML
    void playMove() {
        String filename = "/soundEffects/move-self.mp3";
        playSound(filename);
    }

    private void playSound(String filename) {
        String path = getClass().getResource(filename).getPath();
        System.out.println(path);
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
    }
}