package monitorInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class SureTo {

    @FXML
    Button yesButton;
    @FXML
    Button noButton;
    @FXML
    Label label;

    private static String toWhatSure;
    private static Boolean answear = null;
    private static Stage statStage;

    @FXML
    public void initialize(){
        label.setText(toWhatSure);
    }

    @FXML
    private void yesClicked(){
        answear = true;
        statStage.close();
    }

    @FXML
    private void noClicked() {
        answear = false;
        statStage.close();
    }

    public static boolean display(String toWhat, Stage ownerStage){

        statStage = new Stage();
        statStage.setTitle(toWhat);
        statStage.initOwner(ownerStage);
        toWhatSure = toWhat;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AddDevice.class.getResource("SureTo.fxml"));
        statStage.setOnCloseRequest(e -> e.consume());
        Parent layout = null;
        try {
            layout = loader.load();
        }
        catch (Exception e){
            System.out.println("Coś poszło nie tak");
        }
        Scene scene = new Scene(layout, 420, 200);
        statStage.setScene(scene);
        statStage.showAndWait();
        return answear;
    }
}
