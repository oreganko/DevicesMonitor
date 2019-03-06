package monitorInterface;

import database.Months;
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
import toTable.MonthLists;
import toTable.Service;

import java.util.Calendar;

public class ServicesPerMonth {

    @FXML TableView<Service> serviceTableView;
    @FXML TableColumn <Service, String> clientFirstName;
    @FXML TableColumn <Service, String> clientLastName;
    @FXML TableColumn <Service, String> address;
    @FXML TableColumn <Service, String> model;
    @FXML TableColumn <Service, String> deviceNo;
    @FXML TableColumn <Service, String> lastCheckUp;
    @FXML TableColumn <Service, String> numberCol;
    @FXML TextArea note;
    @FXML Button addService;
    @FXML Button seeClient;
    @FXML Button seeDevice;
    @FXML ChoiceBox<String> monthChoice;
    @FXML Hyperlink backToMainMenu;
    @FXML CheckBox lateServices;
    @FXML Label monthShow;
    @FXML CheckBox actualService;

    public boolean showActualServices;
    public boolean showLate;
    public String month;

    MonthLists monthLists = new MonthLists();
    ObservableList<Service> toShow = FXCollections.observableArrayList();

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

        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Service, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Service, String> p) {
                return new ReadOnlyObjectWrapper(serviceTableView.getItems().indexOf(p.getValue())+1 + "");
            }
        });
        numberCol.setSortable(false);
        setMonthChoice();

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
                    if (!item.isActual()) {
                        //We apply now the changes in all the cells of the row
                        for(int i=0; i<getChildren().size();i++){
                            ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                            ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: tomato");
                        }
                    } else {
                        if (item.doneThisMonth()) {
                            for (int i = 0; i < getChildren().size(); i++) {
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                ((Labeled) getChildren().get(i)).setStyle("-fx-background-color: #7cff72");
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
    }
    /**
     * @ChoiceBox montChoice
     */
    private String getActualMonth(){
        Calendar now = Calendar.getInstance();
        Months month = Months.valueOf(now.get(Calendar.MONTH));
        return month.toString().toLowerCase();
    }

    private void initializeMonthChoice(){
        if(month == null){
        monthChoice.setValue(getActualMonth());}
        else monthChoice.setValue(month);
        ObservableList<String> months = FXCollections.observableArrayList();
        for(Months mon : Months.values()){
            months.add(mon.toString());
        }
        monthChoice.setItems(months);
    }

    @FXML
    private void setBackToMainMenu(){
        Main.showMainMenu();
    }

    @FXML
    private void setSeeClient(){
        Service service = serviceTableView.getSelectionModel().getSelectedItem();
        ClientView.display(service);
    }

    @FXML
    private void setMonthChoice(){
        String monthChosen = monthChoice.getValue();
        setLateServices();
        serviceTableView.setItems(toShow);
        serviceTableView.sort();
        System.out.println("sorted");
        monthShow.setText("Przeglądy na miesiąc: " + monthChosen);
    }


    /**
     * @CheckBox actualMonth
     */

    @FXML
    private void setActualService(){
        setMonthChoice();
        if(actualService.isSelected()) showActualServices = true;
        else showActualServices = false;
    }



    /**
     * @Button addService
     */

    @FXML
    public void setAddService(){
        Service service = serviceTableView.getSelectionModel().getSelectedItem();
        if(service!=null)AddService.display(service, Windows.ServicesPerMonth);
    }

    /**
     * @Button seeDevice
     */

    @FXML
    public void setSeeDevice(){
        Service service = serviceTableView.getSelectionModel().getSelectedItem();
        if(service != null) DeviceView.display(service);
    }


    /**
     * @CheckBox lateServices
     */

    private void setLateServices(){
        String actualMonth = Months.valueOf(Calendar.getInstance().get(Calendar.MONTH)).toString();
        System.out.println("actual month:" +actualMonth);
        String monthChosen = monthChoice.getValue();
        System.out.println("month chosen : " + monthChosen);
        if(monthChosen.equals(actualMonth)){
            actualService.setVisible(true);
            toShow = monthLists.showForThisMonth(lateServices.isSelected(), actualService.isSelected());
        }
        else {
            actualService.setVisible(false);
            if (lateServices.isSelected()) {
                toShow = monthLists.mergeLateToMonth(monthChosen);
            } else toShow = monthLists.getList(monthChosen);
        }

            if(lateServices.isSelected()) showLate = true;
            else showLate = false;

    }
    @FXML
    private void initialize(){
        setLastCheckUp();
        serviceTableView.refresh();
        initializeMonthChoice();
        initializeServiceTableView();

        monthChoice.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    setMonthChoice();
                    month = monthChoice.getValue();
                });
    }
}