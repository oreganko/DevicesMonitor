package monitorInterface;

import database.ClientFields;
import database.StoredProcedures;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import toTable.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AllClients {

    public static Map<String, Person> toChange = new LinkedHashMap<>();

    @FXML
    private TableView<Person> tableView;

    @FXML
    private TableColumn firstNameCol;



    @FXML
    private TableColumn lastNameCol;

    @FXML
    private TableColumn phoneColumn;
    @FXML
    private TableColumn numberCol;

    @FXML
    private Hyperlink turn;

    @FXML
    private Button seeDetails;


    private ObservableList<Person> makeClientList() throws SQLException {
        ResultSet resultSet = database.StoredProcedures.getClients();
        ObservableList<Person> clients = FXCollections.observableArrayList();
        while (resultSet.next()){
            Person current = new Person(resultSet.getString(ClientFields.FIRSTNAME.toString()),
                    resultSet.getString(ClientFields.LASTNAME.toString()), resultSet.getString(ClientFields.PHONE.toString()),
                    resultSet.getString(ClientFields.ID.toString()));
            clients.add(current);
        }
        Comparator<Person> comparator = Comparator.comparing(Person::getLastName);
        FXCollections.sort(clients, comparator);
        return clients;
    }


    @FXML
    private void setReturn(){
        if (!toChange.isEmpty()){
            alertBeforeNext();
        }
        Main.showMainMenu();
    }


    @FXML
    private void setSeeDetails(){
        if (!toChange.isEmpty()){
            alertBeforeNext();
        }
        Person client = tableView.getSelectionModel().getSelectedItem();
        if(client != null) Main.showClientView(client);
    }

    private void setFirstNameCol(){

        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> event) {
                        if(validName(event.getNewValue().trim())) {
                            Person oldPerson = (event.getTableView().getItems().get(
                                    event.getTablePosition().getRow())
                            );
                            (event.getTableView().getItems().get(
                                    event.getTablePosition().getRow())
                            ).setFirstName(event.getNewValue().trim());

                            Person newPerson = oldPerson;
                            newPerson.setFirstName(event.getNewValue().trim());
                            toChange.put(newPerson.getId(), newPerson);
                        }else event.consume();
                    }
                }
        );
    }

    private void setLastNameCol(){

        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> event) {
                        if(validName(event.getNewValue().trim())) {
                            Person oldPerson = (event.getTableView().getItems().get(
                                    event.getTablePosition().getRow())
                            );
                            (event.getTableView().getItems().get(
                                    event.getTablePosition().getRow())
                            ).setLastName(event.getNewValue().trim());

                            Person newPerson = oldPerson;
                            newPerson.setLastName(event.getNewValue().trim());
                            toChange.put(newPerson.getId(), newPerson);
                        }else event.consume();
                    }
                }
        );
    }

    private void setPhoneCol(){

        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> event) {
                        if (validPhone(event.getNewValue().trim())) {
                            Person oldPerson = (event.getTableView().getItems().get(
                                    event.getTablePosition().getRow())
                            );
                            (event.getTableView().getItems().get(
                                    event.getTablePosition().getRow())
                            ).setPhone(event.getNewValue().trim());

                            Person newPerson = oldPerson;
                            newPerson.setPhone(event.getNewValue().trim());
                            toChange.put(newPerson.getId(), newPerson);
                        }
                        else ErrorView.display("Niepoprawny format telefonu", Main.primaryStage);
                    }
                }
        );
    }

    public static void alertBeforeNext(){
        if(SureTo.display("Zapisać zmiany?", Main.primaryStage)){
            toChange.values().forEach( x -> StoredProcedures.updateClient(x));
        }
        toChange.clear();
    }

    private boolean validName(String name){
        if(name == null || name.trim().equals("")) return false;
        return true;
    }
    private boolean validPhone(String phoneNum){
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
        return correct;
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("\\d");
    }

    @FXML
    private void initialize(){

        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Person,String>("firstName")
        );
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Person,String>("lastName")
        );
        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<Person,String>("phone")
        );



        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Person, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Person, String> p) {
                return new ReadOnlyObjectWrapper(tableView.getItems().indexOf(p.getValue())+1 + "");
            }
        });
        numberCol.setSortable(false);

        try {
            tableView.setItems(makeClientList());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setFirstNameCol();
        setLastNameCol();
        setPhoneCol();






    }
}
