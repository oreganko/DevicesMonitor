package monitorInterface;

import database.StoredProcedures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import toTable.Installation;
import toTable.Person;
import toTable.Service;
import toTable.ShortService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceView {

    private static Person client;
    private static Installation installation;
    private static Service service;
    private static Boolean fromInstallations;

    public static void display(Person person, Installation installationGiven){
        client = person;
        installation = installationGiven;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("DeviceView.fxml"));
        Main.setPrimaryStage(loader);
    }

    public static void display(Service serviceGiven){
        service = serviceGiven;
        client = serviceGiven.ownerOf();
        installation = serviceGiven.installationOf();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("DeviceView.fxml"));
        Main.setPrimaryStage(loader);
    }
    public static void display(Service serviceGiven, boolean fromInstallation){
        service = serviceGiven;
        client = serviceGiven.ownerOf();
        installation = serviceGiven.installationOf();
        fromInstallations = fromInstallation;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("DeviceView.fxml"));
        Main.setPrimaryStage(loader);
    }

    @FXML TableView services;
    @FXML TableView services1;
    @FXML TableColumn fixes;
    @FXML TableColumn dates;
    @FXML Label deviceNo;
    @FXML Label name;
    @FXML Label model;
    @FXML Label phone;
    @FXML Label address;
    @FXML Label serviceActuality;
    @FXML Label instalDate;
    @FXML Button addService;
    @FXML Button editDevice;
    @FXML Button editService;
    @FXML Button deleteService;
    @FXML TextArea instalNote;
    @FXML TextArea serviceNote;
    @FXML Hyperlink turnToPrevious;
    @FXML CheckBox isEnabled;

    /**
     * @var tableView
     */

    private void setServices() throws SQLException {
        services.setEditable(false);
        services.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dates.setCellValueFactory(
                new PropertyValueFactory<ShortService,String>("checkUpDate")
        );
        //getting values
        ResultSet resultSet = database.StoredProcedures.getServices(installation.getFabrical_no());
        ObservableList<ShortService> serv = FXCollections.observableArrayList();
        while (resultSet.next()){
            ShortService current = new ShortService(resultSet.getString("fabrical_no"),
                    resultSet.getString("check_date"), resultSet.getString("note"));
            serv.add(current);
        }
        services.setItems(serv);
    }

    private void setFixes() throws SQLException {
        services1.setEditable(false);
        services1.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fixes.setCellValueFactory(
                new PropertyValueFactory<ShortService,String>("checkUpDate")
        );
        //getting values
        ResultSet resultSet = database.StoredProcedures.getFixes(installation.getFabrical_no());
        ObservableList<ShortService> fix = FXCollections.observableArrayList();
        while (resultSet.next()){
            ShortService current = new ShortService(resultSet.getString("fabrical_no"),
                    resultSet.getString("fix_date"), resultSet.getString("note"));
            fix.add(current);
        }
        services1.setItems(fix);
    }

    public void setServicesClicked(){
        ShortService service = (ShortService) services.getSelectionModel().getSelectedItem();
        if(service != null) serviceNote.setText(service.getCheckUpNote());
        services1.getSelectionModel().clearSelection();
    }
    @FXML
    public void setFixesClicked(){
        ShortService service = (ShortService) services1.getSelectionModel().getSelectedItem();
        if(service != null) serviceNote.setText(service.getCheckUpNote());
        services.getSelectionModel().clearSelection();
    }

    /**
     * @var name
     */

    private void nameClicked(){
        name.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        Main.showClientView(client);
                        service = null;
                        client = null;
                        installation = null;
                    }
                }
            }
        });
    }
    /**
     * @var serviceActuality
     */

    private void setServiceActuality(){
        if(!isEnabled.isSelected()) serviceActuality.setText("Urządzenie nieaktywne");
        else {
            if(installation.isActual()) serviceActuality.setText("Przegląd: AKTUALNY");
            else serviceActuality.setText("Przegląd: NIEAKTUALNY");
        }
    }


    /**
     * @var isEnabled
     */

    private void setIsEnabled(){
        if(installation.getDisabled().equals("0")){
            isEnabled.setSelected(true);
        }
        else isEnabled.setSelected(false);
    }

    @FXML
    private void setIsEnabledClick() {
        if (isEnabled.isSelected()) {
            if (SureTo.display("Czy na pewno chcesz zmienić urządzenie na aktywne?", Main.primaryStage)) {
                installation.setDisabled("0");
                StoredProcedures.updateInstalled_Devices(installation);
                setServiceActuality();
                return;
            } else {
                isEnabled.setSelected(false);
                return;
            }
        }
        if (!isEnabled.isSelected()) {
            if(SureTo.display("Czy na pewno chcesz zmienić urządzenie na nieaktywne?", Main.primaryStage)) {
                installation.setDisabled("1");
                StoredProcedures.updateInstalled_Devices(installation);
                setServiceActuality();
                return;
            } else {
                isEnabled.setSelected(true);
                return;
            }
        }
    }

    /**
     * @Button editDevice
     */
    @FXML
    public void setEditDevice(){
        ModifyDevice.display(installation, client);
    }

    /**
     * @Button addService
     */
    @FXML
    private void setAddService(){
        AddService.display(client, installation, Windows.DeviceView);
    }

    /**
     * @Button deleteService
     */

    @FXML
    private void setDeleteService() throws SQLException {
        ShortService service = (ShortService) services.getSelectionModel().getSelectedItem();
        if(service != null) {
            if (SureTo.display("Czy na pewno chcesz usunąć przegląd?", Main.primaryStage)) {
                StoredProcedures.deleteService(service);
                ResultSet lastCheckUpFromCheckUpTableRS = StoredProcedures.getLastCheckUpFromCheckTable(service.getFabrical_no());
                ShortService newLastCheckup;
                if (lastCheckUpFromCheckUpTableRS.next()) {
                    newLastCheckup = new ShortService(lastCheckUpFromCheckUpTableRS.getString("fabrical_no"),
                            lastCheckUpFromCheckUpTableRS.getString("check_date"), lastCheckUpFromCheckUpTableRS.getString("note"));
                } else {
                    newLastCheckup = new ShortService(installation.getFabrical_no(), installation.getMontagedate(), installation.getInstalNote());
                }
                StoredProcedures.updateLastCheckups(newLastCheckup.getFabrical_no(), newLastCheckup.getCheckUpDate(), newLastCheckup.getCheckUpNote());
                installation.setCheckUpDate(newLastCheckup.getCheckUpDate());
                installation.setCheckUpNote(newLastCheckup.getCheckUpNote());
                Main.showDeviceView(client, installation);
            }
        }else {
            service = (ShortService) services1.getSelectionModel().getSelectedItem();
            if(service != null){
                if(SureTo.display("Czy na pewno chcesz usunąć naprawę?", Main.primaryStage)){
                    StoredProcedures.deleteFix(service);
                }
            }
        }
    }

    /**
     * @Button editService
     */

    @FXML
    private void setEditService(){
        ShortService service = (ShortService)services.getSelectionModel().getSelectedItem();
        if(service != null) {
            AddService.display(client, installation, service, Windows.DeviceView, "przegląd");
        }
        else {
            service = (ShortService) services1.getSelectionModel().getSelectedItem();
            AddService.display(client, installation, service, Windows.DeviceView, "naprawa");
        }
    }

    /**
     * @Button turnToPrevious
     */

    @FXML
    private void setTurnToPrevious(){
        if(service == null) {
            Main.showClientView(client);
            service = null;
            client = null;
            installation = null;
            fromInstallations = null;
            return;
        }
        else {
            if(fromInstallations != null && fromInstallations){
                Main.showAllInstallations();
                service = null;
                client = null;
                installation = null;
                fromInstallations = null;
                return;
            }
            if(fromInstallations != null && !fromInstallations){
                Main.showAllServices();
                service = null;
                client = null;
                installation = null;
                fromInstallations = null;
                return;
            }
            Main.showServicesPerMonth();
            service = null;
            client = null;
            installation = null;
            fromInstallations = null;
            return;
        }
    }

    @FXML
    private void initialize(){
        try {
            setServices();
            setFixes();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        deviceNo.setText("Numer urządzenia: " + installation.getFabrical_no());
        model.setText(installation.getModel());
        name.setText(client.getFirstName() + " " + client.getLastName());
        phone.setText(client.getPhone());
        address.setText(installation.getAddress());
        setIsEnabled();
        setServiceActuality();
        instalDate.setText("Data uruchomienia: "+ installation.getMontagedate());
        instalNote.setText(installation.getInstalNote());
        nameClicked();
        setServiceActuality();
    }
}
