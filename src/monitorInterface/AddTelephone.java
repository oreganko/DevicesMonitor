package monitorInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AddTelephone {

    private static Stage statStage;
    public static String phoneNum = "hej";


    @FXML
    private Button confirmButton;
    @FXML
    private Button anulButton;
    @FXML
    private TextField phoneNo;
    @FXML
    private Label warning;

    @FXML
    public void initialize(){
        warning.setText("");
    }


    @FXML
    private void setAnulButton(){
        boolean answear = SureTo.display("Czy na pewno chcesz wyjść?\nInstalacja nie zostanie dodana, jeśli nie podasz telefonu " +
                "dla nowego klienta", statStage);
        if(answear){
            statStage.close();
        }
    }


    @FXML
    private void setConfirmButton(){
        phoneNum = phoneNo.getText();
        boolean correct = true;
        if (phoneNum.length() == 11) {
            String[] phone = phoneNum.split("");
            for (int i = 0; i < 10; i++) {
                if (i == 3 || i == 7) {
                    if (!phone[i].equals("-")) {
                        correct = false;
                    }
                }
                else {
                    if (!isNumeric(phone[i])) {
                        correct = false;
                    }
                }
            }
        }
        else
            correct = false;

        if(!correct){
            warning.setText("Niepoprawny format, powinno być xxx-xxx-xxx");
            phoneNum = null;
        }
        else {
            statStage.close();
        }

    }

    public static boolean isNumeric(String str)
    {
        return str.matches("\\d");
    }


    public static String display(){
        statStage = new Stage();
        statStage.setTitle("Dodaj telefon");
        statStage.setAlwaysOnTop(true);
        statStage.initOwner(AddService.statStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AddTelephone.class.getResource("AddTelephone.fxml"));
        statStage.setOnCloseRequest(e -> {
            boolean answear = SureTo.display("Czy na pewno chcesz wyjść?\nInstalacja nie zostanie dodana, jeśli nie podasz telefonu\n" +
                    "dla nowego klienta", statStage);
            if(!answear){
                e.consume();
            }
        });
        Parent layout = null;
        try {
            layout = loader.load();
        }
        catch (Exception e){
            System.out.println("Nie można załadować dodawania telefonu");
        }
        Scene scene = new Scene(layout, 600, 250);
        statStage.setScene(scene);
        statStage.showAndWait();
        return phoneNum;
    }
}
