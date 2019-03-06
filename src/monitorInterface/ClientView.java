package monitorInterface;

import database.StoredProcedures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import toTable.Installation;
import toTable.Person;
import toTable.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;


public class ClientView {

    private static Person person;
    public static Map<String, Installation> toChange = new LinkedHashMap<>();
    private static ObservableList<Installation> allInstallations;
    private static ObservableList<Installation> workingInstallations;
    private static Service service;
    private static Boolean fromInstallations;
    public static void display(Person gotPerson){
        person = gotPerson;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("ClientView.fxml"));
        Main.setPrimaryStage(loader);
    }

    public static void display(Service serviceG){
        service = serviceG;
        display(serviceG.ownerOf());
    }
    public static void display(Service serviceG, boolean fromInstallation){
        service = serviceG;
        fromInstallations = fromInstallation;
        display(serviceG.ownerOf());
    }

    private void setTableView() throws SQLException{
        ResultSet resultSet = database.StoredProcedures.getInstallations(person);
        ObservableList<Installation> installations = FXCollections.observableArrayList();
        ObservableList<Installation> workInstallations = FXCollections.observableArrayList();
        while (resultSet.next()){
            Installation current = new Installation(resultSet.getString("deviceno"),
                    resultSet.getString("client_id"), resultSet.getString("device_model"),
                    resultSet.getString("address"), resultSet.getString("montagedate"),
                    resultSet.getString("disabled"), resultSet.getString("instalnote"),
                    resultSet.getString("lastcheckup"), resultSet.getString("lastnote"));
            installations.add(current);
            if (current.getDisabled().equals("0")) workInstallations.add(current);
        }
        allInstallations = installations;
        workingInstallations = workInstallations;
        setShowDisabled();

    }


    @FXML
    TableView tableView;

    @FXML
    TableColumn model;

    @FXML
    TableColumn address;

    @FXML
    TableColumn deviceNo;

    @FXML
    TableColumn instalDate;



    @FXML
    TableColumn lastCheckUpDate;

    @FXML
    TextArea instalNote;

    @FXML
    TextArea checkUpNote;

    @FXML
    CheckBox showDisabled;

    @FXML
    Button showDetails;

    @FXML
    Button addNewInstallation;

    @FXML
    Button deleteInstallation;

    @FXML
    Label name;

    @FXML
    Label phone;

    @FXML
    Hyperlink turnBack;

    @FXML
    private void turnBack(){
        if(service == null) {
            if (!toChange.isEmpty()) {
                alertBeforeNext();
            }
            person = null;
            service = null;
            fromInstallations = null;
            Main.showAllClients();
        }
        else
        {
            if (!toChange.isEmpty()) {
                alertBeforeNext();
            }
            if(fromInstallations != null && fromInstallations == true){
                person = null;
                service = null;
                fromInstallations = null;
                Main.showAllInstallations();
                return;
            }
            if(fromInstallations != null && fromInstallations == false){
                person = null;
                service = null;
                fromInstallations = null;
                Main.showAllServices();
                return;
            }
            person = null;
            service = null;
            fromInstallations = null;
            Main.showServicesPerMonth();
        }
    }

    @FXML
    private void setAddNewInstallation(){
        if (!toChange.isEmpty()){
            alertBeforeNext();
        }
        AddDevice.display(person);

    }

    @FXML
    private void setShowDetails(){
        if (!toChange.isEmpty()){
            alertBeforeNext();
        }
        Installation installation = (Installation) tableView.getSelectionModel().getSelectedItem();
        if(installation != null) Main.showDeviceView(person, installation);
    }
    public static void alertBeforeNext(){
        if(SureTo.display("Zapisać zmiany dokonane w tabeli?", Main.primaryStage)){
            toChange.values().forEach( x -> StoredProcedures.updateInstalled_Devices(x));
        }
        toChange.clear();
    }

    @FXML
    public void setDeleteInstallation(){
        if (!toChange.isEmpty()){
            alertBeforeNext();
        }
        Installation installation = (Installation) tableView.getSelectionModel().getSelectedItem();
        if(installation != null) {
            if(SureTo.display("Czy na pewno chcesz usunąć urządzenie?\n" +
                    "Spowoduje to usunięcie wszystkich przeglądów urządzenia.", Main.primaryStage)){
                try {
                    StoredProcedures.deleteInstallation(installation);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            setTableView();
            setShowDisabled();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setShowDisabled(){
        if(showDisabled.isSelected()) tableView.setItems(allInstallations);
        else tableView.setItems(workingInstallations);
    }



    private void setEditableColumn(TableColumn column, String property){

        column.setCellValueFactory(
                new PropertyValueFactory<Installation,String>(property)
        );
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(
                new EventHandler<javafx.scene.control.TableColumn.CellEditEvent<Installation, String>>() {
                    @Override
                    public void handle(javafx.scene.control.TableColumn.CellEditEvent<Installation, String> event) {
                        Installation oldInstall = (event.getTableView().getItems().get(
                                event.getTablePosition().getRow())
                        );
                        String oldValue = event.getOldValue().trim();
                        if(isValid(event.getNewValue().trim())) {
                            Installation newInstall = oldInstall;
                            switch (property) {
                                case "address":
                                    (event.getTableView().getItems().get(
                                            event.getTablePosition().getRow())
                                    ).setAddress(event.getNewValue().trim());
                                    newInstall.setAddress(event.getNewValue().trim());
                                    toChange.put(newInstall.getFabrical_no(), newInstall);
                                    return;
                                case "montagedate":
                                    (event.getTableView().getItems().get(
                                            event.getTablePosition().getRow())
                                    ).setMontagedate(event.getNewValue().trim());
                                    newInstall.setMontagedate(event.getNewValue().trim());
                                    if (oldValue.equals(oldInstall.getCheckUpDate()))
                                        newInstall.setCheckUpDate(event.getNewValue().trim());
                                    toChange.put(newInstall.getFabrical_no(), newInstall);
                                    return;

                                default:
                                    return;
                            }
                        } else event.consume();

                    }
                }
        );
    }

    @FXML
    private void setTableViewClicked(){
        Installation selected = (Installation) tableView.getSelectionModel().getSelectedItem();
        if(selected != null) {
            instalNote.setText(selected.getInstalNote());
            checkUpNote.setText(selected.getCheckUpNote());
        }
    }

    private boolean isValid(String s){
        if(s == null || s.trim().equals("")) return false;
        return true;
    }

    @FXML
    private void initialize() throws SQLException{
        phone.setText(person.getPhone());
        name.setText(person.getFirstName() + " " + person.getLastName());
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setRowFactory(row -> new TableRow<Installation>(){
            @Override
            public void updateItem(Installation item, boolean empty){
                super.updateItem(item, empty);

                if (item == null || empty) {
                    for(int i=0; i<getChildren().size();i++) {
                        ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                        ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #f6f4ff");
                    }
                } else {
                    if (item.getDisabled().equals("1")) {
                        for (int i = 0; i < getChildren().size(); i++) {
                            ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                            ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #feff94");
                        }
                    } else {
                        if (!item.isActual()) {
                            //We apply now the changes in all the cells of the row
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: tomato");
                            }
                        } else {
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #f6f4ff");
                            }
                        }
                    }
                }
                if(tableView.getSelectionModel().getSelectedItem()!=null && tableView.getSelectionModel().getSelectedItem().equals(item)){
                    for (int i = 0; i < getChildren().size(); i++) {
                        ((Labeled) getChildren().get(i)).setTextFill(Color.WHITE);
                        ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #2f72ff");
                    }

                }
                }

        });
        model.setCellValueFactory(
                new PropertyValueFactory<Installation,String>("model")
        );
        setEditableColumn(address, "address");
        deviceNo.setCellValueFactory(
                new PropertyValueFactory<Installation,String>("fabrical_no")
        );
        setEditableColumn(instalDate, "montagedate");
        lastCheckUpDate.setCellValueFactory(
                new PropertyValueFactory<Installation,String>("checkUpDate")
        );
        setTableView();



    }






}
