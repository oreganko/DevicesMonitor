package monitorInterface;

import database.DevicesFields;
import database.InstallationsFields;
import database.StoredProcedures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import toTable.Installation;
import toTable.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class ModifyDevice {

    private String oldDate = installation.getMontagedate();
    private static Person clientPerson;
    private static Installation installation;
    private static Stage statStage;
    private static List <String> models = new LinkedList<>();
    private boolean wasChange;

    @FXML
    private void setAddress() throws SQLException {
        ResultSet resultSet = database.StoredProcedures.getAddress(clientPerson.getFirstName(), clientPerson.getLastName());
        ObservableList<String> addressRes = FXCollections.observableArrayList();
        while (resultSet.next()) {
            String current = resultSet.getString(InstallationsFields.ADDRESS.toString());
            addressRes.add(current);
        }
        address.setItems(addressRes);
    }



    @FXML
    private ComboBox<String> address;

    @FXML
    private void addressFill(){
        installation.setAddress(address.getValue().trim());
        wasChange = true;
    }
    @FXML
    private ComboBox<String> model;

    private void makeModelObservableList() throws SQLException{
        ResultSet resultSet = database.StoredProcedures.getModels();
        ObservableList<String> mods = FXCollections.observableArrayList();
        while (resultSet.next()){
            String current = resultSet.getString(DevicesFields.MODEL.toString());
            mods.add(current);
        }
        models = mods;
        model.setItems(mods);
    }

    @FXML
    private void modelFill(){
        installation.setModel(model.getValue().trim());
        wasChange = true;
    }


    @FXML
    private DatePicker datePicker;

    @FXML
    private void dateFill(){
        installation.setMontagedate(datePicker.getValue().toString());
        wasChange = true;
    }

    @FXML
    private void noteFill(){
        installation.setInstalNote(note.getText().trim());
        wasChange = true;
    }

    @FXML
    private Button anulation;

    @FXML
    private void getAnulation(){
        if(wasChange) {
            boolean answear;
            answear = SureTo.display("Czy jesteś pewien?\nDane nie zostaną zapisane.", statStage);
            if (answear) {
                installation = null;
                clientPerson = null;
                statStage.close();
            }
        }else {
            installation = null;
            clientPerson = null;
            statStage.close();
        }

    }

    @FXML
    private TextArea note;
    @FXML
    private Button confirmation;

    @FXML
    private void getConfirmation(){
        if(wasChange) {
            boolean answear;
            answear = SureTo.display("Zapisać wprowadzone zmiany?", statStage);
            if (answear) {
                installation.setInstalNote(note.getText().trim());
                installation.setMontagedate(datePicker.getValue().toString());
                installation.setAddress(address.getValue().trim());
                installation.setModel(model.getValue().trim());
                saveToDataBase();
                Main.showDeviceView(clientPerson, installation);
            }
        }
            clientPerson = null;
            installation = null;
            statStage.close();
        }

        private boolean modelsContains(){
        for(String m : models){
            if(m.equals(model.getValue().trim())) return true;
        }
        return false;
        }

    private void saveToDataBase(){

        if (!modelsContains()){
            database.StoredProcedures.addModel(installation.getModel());
        }

        database.StoredProcedures.updateInstalled_Devices(installation);

        if(installation.getMontagedate().equals(installation.getCheckUpDate())){
                StoredProcedures.updateLastCheckups(installation.getFabrical_no(),installation.getMontagedate(), installation.getInstalNote());
        }

    }





    @FXML
    public void initialize() throws SQLException{
        wasChange = false;
        setAddress();
        makeModelObservableList();
        datePicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        address.setValue(installation.getAddress());
        model.setValue(installation.getModel());
        datePicker.setValue(datePicker.getConverter().fromString(installation.getMontagedate()));
        note.setText(installation.getInstalNote());
    }

    public static void display(Installation installationGiven, Person person){
        installation = installationGiven;
        clientPerson = person;
        statStage = new Stage();
        statStage.initOwner(Main.primaryStage);
        statStage.setTitle("Edycja urządzenia nr " + installationGiven.getFabrical_no());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DeviceView.class.getResource("ModifyDevice.fxml"));
        statStage.setOnCloseRequest(e -> {
            boolean answear = SureTo.display("Czy na pewno chcesz wyjść?\nDane nie zostaną zapisane.", statStage);
            if(!answear) e.consume();
        });
        Parent layout = null;
        try {
            layout = loader.load();
        }
        catch (Exception e){
        }
        Scene scene = new Scene(layout, 430, 330);
        statStage.setScene(scene);
        statStage.show();
    }
}
