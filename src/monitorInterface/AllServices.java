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
import javafx.util.Callback;
import toTable.Installation;
import toTable.Service;

public class AllServices {


    @FXML
    TableView<Service> serviceTableView;
    @FXML
    TableColumn<Service, String> clientFirstName;
    @FXML TableColumn <Service, String> clientLastName;
    @FXML TableColumn <Service, String> address;
    @FXML TableColumn <Service, String> model;
    @FXML TableColumn <Service, String> deviceNo;
    @FXML TableColumn <Service, String> lastCheckUp;
    @FXML TableColumn <Service, String> montageDate;
    @FXML TableColumn <Service, String> numberCol;
    @FXML
    TextArea note;
    @FXML
    TextArea instalNote;
    @FXML
    Button addService;
    @FXML Button seeClient;
    @FXML Button seeDevice;
    @FXML
    Hyperlink backToMainMenu;
    @FXML CheckBox lateServices;

    ObservableList<Service> toShow = FXCollections.observableArrayList();
    ObservableList<Service> all = FXCollections.observableArrayList();
    ObservableList<Service> active = FXCollections.observableArrayList();

    /**
     * @TableView serviceTableView
     */

    public void initializeServiceTableView(){
        serviceTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
        setLateServices();

        serviceTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 1) {
                        if(serviceTableView.getSelectionModel().getSelectedItem() != null)
                        setNote(serviceTableView.getSelectionModel().getSelectedItem());
                    }
                    if(mouseEvent.getClickCount() == 2){
                        setSeeDevice();
                    }
                }
            }
        });

        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Service, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Service, String> p) {
                return new ReadOnlyObjectWrapper(serviceTableView.getItems().indexOf(p.getValue())+1 + "");
            }
        });
        numberCol.setSortable(false);

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
            ClientView.display(service,false);
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
        ObservableList<Service> installations = StoredProcedures.getAllServices();
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
            AddService.display(service, Windows.AllServices);
    }

    /**
     * @Button seeDevice
     */

    @FXML
    public void setSeeDevice(){
        Service service = serviceTableView.getSelectionModel().getSelectedItem();
        if(service != null)
        {
            DeviceView.display(service, false);
        }
    }


    /**
     * @CheckBox lateServices
     */

    @FXML
    private void initialize(){
        initializeServiceTableView();
        lateServices.setSelected(false);
        initializeLists();

    }
}
