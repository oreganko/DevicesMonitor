package monitorInterface;

import database.StoredProcedures;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import toTable.Installation;
import toTable.Person;
import toTable.Service;

import java.sql.SQLException;


public class AllInstallations {

        @FXML
        TableView<Service> serviceTableView;
        @FXML
        TableColumn<Service, String> clientFirstName;
        @FXML TableColumn <Service, String> clientLastName;
        @FXML TableColumn <Service, String> address;
        @FXML TableColumn <Service, String> model;
        @FXML TableColumn <Service, String> deviceNo;
        @FXML TableColumn <Service, String> instalDate;
        @FXML TableColumn <Service, String> lastCheckUp;
        @FXML TableColumn <Service, String> montageDate;
        @FXML TableColumn numberCol;
        @FXML
        TextArea note;
        @FXML
        TextArea instalNote;
        @FXML
        Button addService;
        @FXML Button seeClient;
        @FXML Button seeDevice;
        @FXML Hyperlink backToMainMenu;
        @FXML CheckBox lateServices;

        ObservableList<Service> toShow = FXCollections.observableArrayList();
        ObservableList<Service> all = FXCollections.observableArrayList();
        ObservableList<Service> active = FXCollections.observableArrayList();

        /**
         * @TableView serviceTableView
         */

        public void initializeServiceTableView(){
            serviceTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            setLastCheckUp();
            clientFirstName.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("clientFirstName")
            );
            clientLastName.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("clientLastName")
            );
            address.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("address")
            );
            model.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("model")
            );
            deviceNo.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("fabrical_no")
            );
            lastCheckUp.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("checkUpDate")
            );
            montageDate.setCellValueFactory(
                    new PropertyValueFactory<Service,String>("montagedate")
            );
            numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Installation, String>, ObservableValue<String>>() {
                @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Installation, String> p) {
                    return new ReadOnlyObjectWrapper(serviceTableView.getItems().indexOf(p.getValue())+1 + "");
                }
            });
            numberCol.setSortable(false);

            setLateServices();

        }

        private void setLastCheckUp(){
            serviceTableView.setRowFactory(row -> new TableRow<Service>(){
                @Override
                public void updateItem(Service item, boolean empty){
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        for(int i=0; i<getChildren().size();i++) {
                            ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                            ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #f6f4ff");
                        }
                    } else {
                        //Now 'item' has all the info of the Person in this row
                        if (item.getDisabled().equals("1")) {
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #f8ff73");
                            }

                        } else {
                            if (!item.isActualThisMonth()) {
                                //We apply now the changes in all the cells of the row
                                for(int i=0; i<getChildren().size();i++){
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
                        if(getTableView().getSelectionModel().getSelectedItems().contains(item)){
                            setNote(item);
                                for (int i = 0; i < getChildren().size(); i++) {
                                    ((Labeled) getChildren().get(i)).setTextFill(Color.WHITE);
                                    ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #2f72ff");
                                }
                        }

                    }
                }
            });
            serviceTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                            setSeeDevice();
                        }
                    }
                }
            });
        }

        /**
         * @TextArea
         */

        private void setNote(Service service){
            note.setText(service.getCheckUpNote());
            instalNote.setText(service.getInstalNote());
        }


        @FXML
        private void setBackToMainMenu(){
            Main.showMainMenu();
        }

        @FXML
        private void setSeeClient(){
            Service service = serviceTableView.getSelectionModel().getSelectedItem();
            if(service != null) {
                ClientView.display(service,true);
            }
        }

        @FXML
        private void setLateServices(){
            if(lateServices.isSelected()){
                toShow = all;
            }
            else toShow = active;
            serviceTableView.setItems(toShow);
        }


        private void initializeLists(){
            ObservableList<Service> installations = StoredProcedures.allInstallations();
            installations.forEach(x -> {
                if(x.getDisabled().equals("0")){
                    all.add(x);
                    active.add(x);
                }
                else all.add(x);
            });
            setLateServices();
        }


        /**
         * @Button addService
         */

        @FXML
        public void setAddService(){
            Service service = serviceTableView.getSelectionModel().getSelectedItem();
            if(service != null)
            AddService.display(service, Windows.AllInstallations);
        }

        /**
         * @Button seeDevice
         */

        @FXML
        public void setSeeDevice(){
            Service service = serviceTableView.getSelectionModel().getSelectedItem();
            if(service != null)
            {
                DeviceView.display(service, true);
            }
        }


        /**
         * @CheckBox lateServices
         */

        @FXML
        private void initialize(){
            setLastCheckUp();
            initializeServiceTableView();
            lateServices.setSelected(false);
            initializeLists();

        }

}
