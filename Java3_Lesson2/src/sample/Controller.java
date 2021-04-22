package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller{
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    TextArea mainTextArea;
    @FXML
    TextField sendTextField;
    @FXML
    HBox bottomPanel;
    @FXML
    HBox upperPanel;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passField;

    private boolean isAuthorized;

    public void setAuthorized(boolean auth){
        this.isAuthorized=auth;

        if(!isAuthorized){
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }
        else{
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }
    }


    @FXML
    public void sendMsg() {
        try{
            if(!sendTextField.getText().isEmpty()) {
                out.writeUTF(sendTextField.getText());
                sendTextField.clear();
                sendTextField.requestFocus();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @FXML
    public void exitAction() {
        Platform.exit();
    }
    @FXML
    public void clearTextArea() {
        mainTextArea.clear();
    }
    @FXML
    public void cpToBuffer() {
        String copyText = mainTextArea.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(copyText);
        clipboard.setContent(content);
    }

    public void connect() {
        try {
            client = new Socket("localhost", 8089);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            new Thread(()->{
                try {
                    while(true) {
                        String msg = in.readUTF();
                        if("/authOk".equals(msg)){
                            mainTextArea.clear();
                            setAuthorized(true);
                            break;
                        }
                        else {
                            mainTextArea.appendText(msg + "\n");
                        }
                    }

                    while(true) {
                        String msg = in.readUTF();
                        if("/ServerClosed".equals(msg)){
                            break;
                        }
                        mainTextArea.appendText(msg+"\n");
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                finally{
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            mainTextArea.appendText("Connection refused");
        }

    }

    public void tryToAuth(ActionEvent actionEvent) {
        if(client == null || client.isClosed()){
            connect();
        }
        try{
            out.writeUTF("/auth "+loginField.getText()+" "+passField.getText());
            loginField.clear();
            passField.clear();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void disconnect(){
        try{
            out.writeUTF("/end");
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
