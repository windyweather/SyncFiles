package net.windyweather.syncfiles;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.windyweather.syncfiles.SyncFilesController;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

import static java.awt.event.WindowEvent.WINDOW_ACTIVATED;
import static javafx.stage.WindowEvent.*;

public class AboutDialog {
    public Label lblSSAVersion;
    public TextArea taAboutText;
    public Hyperlink hlLinkToGitHub;
    public Button btnCloseAboutDialog;

    private final String[] sAboutText = new String[] {
            "SyncFiles is a program that finds and copies changed and new files between two directories. ",
            "The program holds pairs of directories, source and destinations, and then allow searching ",
            "for new and changed files between the two directories. Removed files are ignored so that if, ",
            "for example, files are deleted from the source, they remain in the destination.\n",
            "The program is useful for a variety of functions including backing up files up over the network ",
            "to a network server, or moving newly created programs from the coding target directory ",
            "to a directory to run them.\n",
            "\n",
            "The idea for the program was created a very long time ago and this is at least the third",
            "implementation in various languages and graphical frameworks.\n",
            "More details later\n\n",
            "See more at the link below:"
            };
    public ImageView imgView;


    /*
        Wait until the window is "Shown" before we load up the TextArea with the content above.
        Before that it won't take. Like the TextArea doesn't fully exist or something.
     */
    public void SetStuffUp() {

        SyncFilesController.printSysOut("AboutDialog - SetStuffUp called");
        taAboutText.setEditable( true );
        taAboutText.clear();
        for (String s : sAboutText) {
            taAboutText.appendText(s);
        }
        taAboutText.setEditable( false );
        taAboutText.deselect();
        taAboutText.home();

        Image imgIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("sync-icon-flip-240.png")) );
        imgView.setImage( imgIcon );
    }

    /*
        During Initialize, fix up the text area.
     */
    @FXML
    void initialize(){
        SetStuffUp();
    }

    public void OnCloseAbout(ActionEvent actionEvent) {
        /*
          get the scene from any GUI item, and get window from that.
          Then that's the stage and call close on it.
         */
        Stage stage = (Stage) btnCloseAboutDialog.getScene().getWindow();
        stage.close();
    }


    /*
        We have to do the link ourselves
     */
    public void OnLinkToGitHub(ActionEvent actionEvent) throws IOException {
        String uri = hlLinkToGitHub.getText();
        System.out.println(String.format("Open GitHub link in the browser: %s", uri));
        Desktop desktop = Desktop.getDesktop();
        desktop.browse( java.net.URI.create(uri));
    }
}
