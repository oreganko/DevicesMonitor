package monitorInterface;
import database.Months;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.*;


public class MainMenu {

    //Buttons

    @FXML Button servicesPerMonth;
    @FXML Button addService;
    @FXML Button addInstallation;
    @FXML
    AnchorPane pane;

    // Month ChoiceBox

    ObservableList<String> months = FXCollections.observableArrayList("styczeń", "luty", "marzec", "kwiecień",
            "maj", "czerwiec", "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień");


    // View ChoiceBox

    ObservableList<String> views = FXCollections.observableArrayList("wyświetl", "instalacje", "klienci", "przeglądy");
    @FXML
    private ChoiceBox<String> viewChoice;


    @FXML
    private ImageView logo;


    @FXML
    public void initialize()
    {
        pane.setCenterShape(true);
        viewChoice.setValue("wyświetl");
        viewChoice.setItems(views);
        viewChoice.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> setViewChoice());
    }



    @FXML
    public void setServicesPerMonth(){
        Main.showServicesPerMonth();
    }

    @FXML
    public void setAddService(){
        AddService.display();

    }


    public void setAddInstallation(){
        Main.showAddDevice();
    }



    private String gotViewChoice;

    @FXML //TODO
    public void setViewChoice(){
        gotViewChoice = viewChoice.getValue();
        switch (gotViewChoice){
            case "klienci":
                Main.showAllClients();
                return;
            case "instalacje":
                Main.showAllInstallations();
                return;
            case "przeglądy":
                Main.showAllServices();
                return;
                default: return;
        }
    }

}
