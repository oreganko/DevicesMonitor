package monitorInterface;

import database.ClientFields;
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
import toTable.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class AddDevice {

    private static Stage statStage;
    private static String phoneNo;
    private static String firstName;
    private static String lastName;
    private static int id;
    private static ObservableList<String> modelList;
    private static String gotNote;
    private static Person person;

    @FXML
    private ComboBox<String> client;
    private String gotClient = null;

    public static ObservableList<String> makeClientObservableList() throws SQLException {
        ResultSet resultSet = database.StoredProcedures.getClients();
        ObservableList<String> clients = FXCollections.observableArrayList();
        while (resultSet.next()){
            String current = resultSet.getString(ClientFields.LASTNAME.toString()) + " "
                    + resultSet.getString(ClientFields.FIRSTNAME.toString());
            clients.add(current);
        }
        Comparator<String> comparator = Comparator.naturalOrder();
        FXCollections.sort(clients, comparator);
        return clients;
    }


    @FXML
    private void clientFill() throws SQLException {
        gotClient = client.getValue().trim();
        if(gotClient.equals("")) return;
            lastName = database.StoredProcedures.divideName(gotClient)[0];
            firstName = database.StoredProcedures.divideName(gotClient)[1];
            ResultSet resultSet = database.StoredProcedures.getAddress(firstName, lastName);
            ObservableList<String> addressRes = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String current = resultSet.getString(InstallationsFields.ADDRESS.toString());
                addressRes.add(current);
            }
            address.setItems(addressRes);
    }



@FXML
    private ComboBox<String> address;
    private String gotAddress = null;

    @FXML
    private void addressFill(){
        gotAddress = address.getValue().trim();
    }
@FXML
    private ComboBox<String> model;
    private String gotModel = null;

    public ObservableList<String> makeModelObservableList() throws SQLException{
        ResultSet resultSet = database.StoredProcedures.getModels();
        ObservableList<String> mods = FXCollections.observableArrayList();
        while (resultSet.next()){
            String current = resultSet.getString(DevicesFields.MODEL.toString());
            mods.add(current);
        }
        Comparator<String> comparator = Comparator.naturalOrder();
        FXCollections.sort(mods, comparator);
        modelList = mods;
        return mods;
    }

    @FXML
    private void modelFill(){
        gotModel = model.getValue().trim();
    }

@FXML
    private TextField deviceNo;
    private String gotDeviceNo;

    @FXML
    private void deviceNoFill(){

        gotDeviceNo = deviceNo.getText().trim();

    }


@FXML
    private DatePicker datePicker;
    String gotDate;

    @FXML
    private void dateFill(){
        gotDate = datePicker.getValue().toString();
    }

    @FXML
    private Button anulation;

    @FXML
    private void getAnulation(){
        boolean answear;
        answear = SureTo.display("Czy jesteś pewien?\nDane nie zostaną zapisane.", statStage);
        if(answear){
            person = null;
            statStage.close();
        }
    }

    @FXML
    private TextArea note;
@FXML
    private Button confirmation;

    private boolean isValid(String o){
        if(o == null || o.trim().equals("")) return false;
        return true;
    }

    private boolean validate(){
        String message = "Następujące pola:\n";
        if(!isValid(client.getValue())) message += "Nazwisko i imię klienta\n";
        if(!isValid(address.getValue())) message += "Adres\n";
        if(!isValid(model.getValue())) message += "Model\n";
        if(!isValid(deviceNo.getText())) message += "Numer fabryczny\n";
        if(datePicker.getValue()==null) message += "Data uruchomienia\n";
        message += "są puste";
        if(message.equals("Następujące pola:\nsą puste")) return true;
        {ErrorView.display(message, statStage); return false;}
    }

@FXML
    private void getConfirmation(){
    boolean answear;
    answear = SureTo.display("Czy jesteś pewien, że chcesz zapisać?", statStage);
    if(answear){
        if(validate()) {
            gotNote = note.getText().trim();
            gotDeviceNo = deviceNo.getText().trim();
            saveToDataBase();
            if (person != null) {
                Main.showClientView(person);
            }
            person = null;
            statStage.close();
        }
    }
}

    private void saveToDataBase(){
    id = database.StoredProcedures.personInDatabase(firstName, lastName);
    if(id == -1){
        phoneNo = AddTelephone.display();
        database.StoredProcedures.addClient(firstName, lastName, phoneNo);
        id = database.StoredProcedures.personInDatabase(firstName, lastName);
    }

    if (!modelList.contains(gotModel)){
        database.StoredProcedures.addModel(gotModel);
    }

    database.StoredProcedures.addInstallation(gotDeviceNo, id, gotModel, gotAddress, gotDate, gotNote);

        StoredProcedures.addToLastCheckups(gotDeviceNo,gotDate, gotNote);

    }





@FXML
public void initialize() throws SQLException{
    if(person != null){
        client.setValue(person.getLastName() + " " + person.getFirstName());
        clientFill();
    }

    client.setItems(makeClientObservableList());
    model.setItems(makeModelObservableList());
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
}

    public static void display(){
        statStage = new Stage();
        statStage.setTitle("Nowa instalacja");
        statStage.initOwner(Main.primaryStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AddDevice.class.getResource("AddDevice.fxml"));
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
        Scene scene = new Scene(layout, 600, 400);
        statStage.setScene(scene);
        statStage.showAndWait();
    }

    public static void display(Person clientPerson){

    person = clientPerson;
    display();
    }


    }
