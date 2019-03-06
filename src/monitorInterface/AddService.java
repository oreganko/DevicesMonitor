package monitorInterface;

import database.StoredProcedures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import toTable.Installation;
import toTable.Person;
import toTable.Service;
import toTable.ShortService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddService {

    @FXML ChoiceBox<String> deviceNo;
    @FXML ChoiceBox<String> type;
    @FXML DatePicker datePicker;
    @FXML TextArea note;
    @FXML Button confirmation;
    @FXML Button anullation;
    private ObservableList<String> types = FXCollections.observableArrayList("przegląd", "naprawa");
    private static Windows whereFrom = Windows.MainMenu;
    public static Stage statStage;
    private static String deviceNoStr;
    private static Person client;
    private static Installation installation;
    private static boolean wasChange;
    private static ShortService shortService;
    private static String typeOf;

    /**
     * @ChoiceBox deviceNo
     */

    private void initializeDeviceNo() throws SQLException {
        if(deviceNoStr != null){
            ObservableList<String> deviceNos = FXCollections.observableArrayList(deviceNoStr);
            deviceNo.setItems(deviceNos);
            deviceNo.setValue(deviceNoStr);
        }
        else {
            ResultSet resultSet = StoredProcedures.getFabricalNos();
            ObservableList<String> deviceNos = FXCollections.observableArrayList();
            while (resultSet.next()){
                deviceNos.add(resultSet.getString("fabrical_no"));
            }
            deviceNo.setItems(deviceNos);
        }
    }

    @FXML
    private void setAction(){
        wasChange = true;
    }

    /**
     * @Button confirmation
     */

    private boolean isValid(String o){
        if(o == null || o.trim().equals("")) return false;
        return true;
    }
    private boolean validDate() {
        if (isValid(deviceNo.getValue())) {
            String instalDate = StoredProcedures.getInstallationDate(deviceNo.getValue());
            if (instalDate == null) return false;
            if (datePicker.getValue() == null) return false;
            String serviceDate = datePicker.getValue().toString();
            if (!StoredProcedures.isBefore(instalDate, serviceDate)) {
                ErrorView.display("Data serwisu musi być później niż data instalacji, czyli: " + instalDate, statStage);
                return false;
                    }
                }
        return true;
    }

    private boolean validate(){
        String message = "Następujące pola:\n";
        if(!isValid(deviceNo.getValue())) message += "Numer urządzenia\n";
        if(datePicker.getValue()==null) message += "Data przeglądu\n";
        message += "są puste";
        if(message.equals("Następujące pola:\nsą puste")) return true;
        {ErrorView.display(message, statStage); return false;}
    }

   private void simplyAddNewServiceToDB(ShortService service){
       if (type.getValue().equals("przegląd")) {     //adding new service
           String oldCheckUpDate = StoredProcedures.getLastCheckup(service.getFabrical_no());
           StoredProcedures.addService(service);
           if (datePicker.getConverter().fromString(oldCheckUpDate).isBefore(datePicker.getConverter().fromString(service.getCheckUpDate()))) {
               StoredProcedures.updateLastCheckups(service.getFabrical_no(), service.getCheckUpDate(), service.getCheckUpNote());
           }
       } else {     //adding new fix
           StoredProcedures.addFix(service);
       }
       return;
   }

   private void simplyUpdateService(ShortService service) throws SQLException{
        if(type.getValue().equals("przegląd")) {
            System.out.println("TU JEST");
            StoredProcedures.updateService(shortService, service);
            ResultSet resultSet = StoredProcedures.getLastCheckUpFromCheckTable(service.getFabrical_no());
            ShortService newLastCheckup;
            if (resultSet.next()) {
                newLastCheckup = new ShortService(resultSet.getString("fabrical_no"),
                        resultSet.getString("check_date"), resultSet.getString("note"));
            } else {
                newLastCheckup = new ShortService(installation.getFabrical_no(), installation.getMontagedate(), installation.getInstalNote());
            }
            StoredProcedures.updateLastCheckups(newLastCheckup.getFabrical_no(), newLastCheckup.getCheckUpDate(), newLastCheckup.getCheckUpNote());
            installation.setCheckUpDate(newLastCheckup.getCheckUpDate());
            installation.setCheckUpNote(newLastCheckup.getCheckUpNote());
        }else {
            StoredProcedures.updateFixes(shortService, service);
        }
   }

   private void setClose(){
       client = null;
       installation = null;
       deviceNoStr = null;
       shortService = null;
       whereFrom = Windows.MainMenu;
       typeOf = null;
       statStage.close();
   }

    @FXML
    private void setConfirmation() throws SQLException{
        boolean answear;
        answear = SureTo.display("Czy jesteś pewien, że chcesz zapisać?", statStage);
        if (answear && validate() && validDate()) {
            ShortService service = new ShortService(deviceNo.getValue().trim(), datePicker.getValue().toString(), note.getText().trim());
            System.out.println(whereFrom.toString());
            switch (whereFrom) {
                case DeviceView:
                    if(shortService == null){
                        System.out.println("był null :(");
                        simplyAddNewServiceToDB(service);
                    }
                    else simplyUpdateService(service);
                    Main.showDeviceView(client, installation);
                    setClose();
                    return;
                case AllInstallations:
                    simplyAddNewServiceToDB(service);
                    Main.showAllInstallations();
                    setClose();
                    return;
                case AllServices:
                    simplyAddNewServiceToDB(service);
                    Main.showAllServices();
                    setClose();
                    return;
                case ServicesPerMonth:
                    simplyAddNewServiceToDB(service);
                    Main.showServicesPerMonth();
                    setClose();
                    return;

                    default://from main menu
                    simplyAddNewServiceToDB(service);
                    Main.showMainMenu();
                    setClose();
                    return;
            }
        }
    }




    /**
     * @Button anulation
     */
    @FXML
    private void setAnullation(){
        if(wasChange) {
            boolean answear;
            answear = SureTo.display("Czy jesteś pewien?\nDane nie zostaną zapisane.", statStage);
            if (answear) {
                setClose();
            }
        }else {
            setClose();
        }
    }

    @FXML
    private void initialize(){
        try {
            initializeDeviceNo();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(typeOf == null) {
            type.setValue("przegląd");
            type.setItems(types);
        }
        else {
            type.setValue(typeOf);
            ObservableList<String> oneType = FXCollections.observableArrayList(typeOf);
            type.setItems(oneType);
        }

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
        if(shortService != null){
            ObservableList<String> item = FXCollections.observableArrayList(shortService.getFabrical_no());
            deviceNo.setItems(item);
            deviceNo.setValue(shortService.getFabrical_no());
            datePicker.setValue(datePicker.getConverter().fromString(shortService.getCheckUpDate()));
            note.setText(shortService.getCheckUpNote());
        }
    }

    public static void display(){
        wasChange = false;
        statStage = new Stage();
        statStage.setTitle("Dodaj przegląd");
        statStage.initOwner(Main.primaryStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("AddService.fxml"));
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
        Scene scene = new Scene(layout, 340, 395);
        statStage.setScene(scene);
        statStage.showAndWait();
    }

    public static void display(Person person, Installation installationGiven, Windows from){
        deviceNoStr = installationGiven.getFabrical_no();
        client = person;
        installation = installationGiven;
        whereFrom = from;
        display();
    }

    public static void display(Person person, Installation installationGiven, ShortService service, Windows from, String typeOfService){
        deviceNoStr = installationGiven.getFabrical_no();
        client = person;
        installation = installationGiven;
        shortService = service;
        whereFrom = from;
        typeOf = typeOfService;
        display();

    }


    public static void display(Service service, Windows from){
        installation = service.installationOf();
        deviceNoStr = installation.getFabrical_no();
        client = service.ownerOf();
        whereFrom = from;
        display();
    }
}
