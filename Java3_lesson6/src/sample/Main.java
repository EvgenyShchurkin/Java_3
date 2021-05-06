package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("C:\\Java\\Java2_Lesson_7\\Chat\\src\\main\\java\\sample\\sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("JavaFX Chat");
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Controller controller =loader.getController();
                controller.disconnect();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
