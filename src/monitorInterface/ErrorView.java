package monitorInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ErrorView {

    private static Stage statStage;
    private static String errorMessage;
@FXML
Label eMessage;
@FXML
Button okButton;

@FXML
    private void initialize(){
    eMessage.setText(errorMessage);
}

@FXML
private void setOkButton(){
    statStage.close();
}

    public static void display(String message, Stage ownerStage){

        statStage = new Stage();
        statStage.setTitle("Nieuzupełnione pola");
        statStage.initOwner(ownerStage);
        errorMessage = message;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ErrorView.class.getResource("ErrorView.fxml"));
        Parent layout = null;
        try {
            layout = loader.load();
        }
        catch (Exception e){
            System.out.println("Coś poszło nie tak");
        }
        Scene scene = new Scene(layout, 463, 217);
        statStage.setScene(scene);
        statStage.showAndWait();
        return;
    }
}
