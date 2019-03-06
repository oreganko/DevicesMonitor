package monitorInterface;

import database.StoredProcedures;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import toTable.Installation;
import toTable.Person;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


public class Main extends Application {


    static Parent mainLayout;
    public static Stage primaryStage;
    static Scene scene;
    public static Connection connection;

    public static void setPrimaryStage(FXMLLoader loader){
        Parent newLayout = null;
        try {
            newLayout = loader.load();
        }
        catch (Exception e){
            System.out.println("Coś poszło nie tak");
            e.printStackTrace();
        }
        scene.setRoot(newLayout);
    }


    public static void showMainMenu(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("MainMenu.fxml"));
        primaryStage.setTitle("Monitor urządzeń");
        setPrimaryStage(loader);

    }

    public static void showAddDevice(){
        AddDevice.display();
    }

    public static void showAllClients(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("AllClients.fxml"));
        primaryStage.setTitle("Monitor urządzeń - wszyscy klienci");
        setPrimaryStage(loader);
    }

    public static void showClientView(Person client){
        primaryStage.setTitle("Monitor urządzeń - " + client.getFirstName() + " " + client.getLastName());
        ClientView.display(client);
    }

    public static void showDeviceView(Person client, Installation installation){
        primaryStage.setTitle("Monitor urządzeń - urządzenie nr " + installation.getFabrical_no());
        DeviceView.display(client, installation);
    }

    public static void showServicesPerMonth(){
        primaryStage.setTitle("Monitor urządzeń - serwisy na miesiąc");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("ServicesPerMonth.fxml"));
        setPrimaryStage(loader);
    }

    public static void showAllInstallations(){
        primaryStage.setTitle("Monitor urządzeń - wszystkie urządzenia");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("AllInstallations.fxml"));
        setPrimaryStage(loader);
    }
    public static void showAllServices(){
        primaryStage.setTitle("Monitor urządzeń - wszystkie przeglądy");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("AllServices.fxml"));
        setPrimaryStage(loader);
    }

    private void closeSafety(){
        if(!AllClients.toChange.isEmpty()) AllClients.alertBeforeNext();
        if(!ClientView.toChange.isEmpty()) ClientView.alertBeforeNext();
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Monitor Urządzeń");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("MainMenu.fxml"));
        try {
            mainLayout = loader.load();
            Scene scene1 = new Scene(mainLayout, 675, 430);
            scene = scene1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> closeSafety());
        primaryStage.show();
        try {
            connection = StoredProcedures.getConnected();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println(AddTelephone.display());
        //AddDevice.display();
        //ClientView.display(new Person("Piotr", "Malinowski", "533-707-078", "2"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
