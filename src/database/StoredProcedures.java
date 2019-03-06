package database;

import installation.CreateDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import monitorInterface.ErrorView;
import monitorInterface.Main;
import toTable.*;
import java.sql.*;

public class StoredProcedures {


    public static Connection getConnected() throws SQLException {
        try {
            Class.forName(CreateDB.DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(CreateDB.JDBC_URL_toRun);
         return connection;
    }

    public static ResultSet executeSelectStatement(String sql)throws SQLException {
        ResultSet resultSet = null;
        Statement statement = monitorInterface.Main.connection.createStatement();
//        Statement statement = getConnected().createStatement();
        resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public static void executeInsertStatement(String sql) {
        Statement statement = null;
        try {
            statement = monitorInterface.Main.connection.createStatement();
            statement = getConnected().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            ErrorView.display(e.getMessage(), Main.primaryStage);
            e.printStackTrace();
        }
    }


    //CLIENTS statements

    public static ResultSet getClients() throws SQLException{
        String sql = "SELECT * FROM CLIENTS";
        return executeSelectStatement(sql);
    }

    public static String[] divideName(String name){
        String[] splitName = name.split(" ");
        return splitName;
    }

    public static int personInDatabase(String firstname, String lastname){
        String sql ="select id from CLIENTS where FIRSTNAME = '"+firstname+ "' and LASTNAME = '"+lastname+"'";
        int isIt = -1;
        try {
            ResultSet resultSet = executeSelectStatement(sql);
            if(resultSet.next())
            isIt = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isIt;
    }

    public static void addClient(String firstname, String lastname, String phone_no){
        String sql = "insert into clients (firstname, lastname, phone) values ('" + firstname + "', '" + lastname +"', '" + phone_no + "')";
            executeInsertStatement(sql);
    }

    public static void updateClient(Person person){
        String sql = "update CLIENTS\n" +
                "set FIRSTNAME = '"+ person.getFirstName()+ "', LASTNAME = '" + person.getLastName() + "', PHONE = '" + person.getPhone() +"' "+
                "where ID = " + person.getId();
            executeInsertStatement(sql);
    }


    //INSTALLATIONS statements

    public static ResultSet getAddress(String firstname, String lastname) throws SQLException{
        String sql = "select distinct ADDRESS from INSTALLED_DEVICES join CLIENTS " +
                "on INSTALLED_DEVICES.CLIENT_ID = CLIENTS.ID\n" +
                "where FIRSTNAME = '" + firstname + "' and LASTNAME = '" + lastname + "'";
        return executeSelectStatement(sql);

    }

    public static boolean isBeforeOrEqual(String firstDate, String secondDate){ //is second date after or equal first date
        int firstYear = getYear(firstDate);
        int secondYear = getYear(secondDate);
        if(firstYear > secondYear) return false;
        if(firstYear < secondYear) return true;
        if(firstYear == secondYear){
            int firstMonth = getMonth(firstDate);
            int secondMonth = getMonth(secondDate);
            if(firstMonth > secondMonth) return false;
            if(firstMonth < secondMonth) return true;
            if(firstMonth == secondMonth){
                int firstDay = getDay(firstDate);
                int secondDay = getDay(secondDate);
                if(firstDay > secondDay) return false;
                if(firstDay <= secondDay) return true;
            }
        }
        return true;
    }
    public static boolean isBefore(String firstDate, String secondDate){ //is second date after first date
        int firstYear = getYear(firstDate);
        int secondYear = getYear(secondDate);
        if(firstYear > secondYear) return false;
        if(firstYear < secondYear) return true;
        if(firstYear == secondYear){
            int firstMonth = getMonth(firstDate);
            int secondMonth = getMonth(secondDate);
            if(firstMonth > secondMonth) return false;
            if(firstMonth < secondMonth) return true;
            if(firstMonth == secondMonth){
                int firstDay = getDay(firstDate);
                int secondDay = getDay(secondDate);
                if(firstDay >= secondDay) return false;
                if(firstDay < secondDay) return true;
            }
        }
        return true;
    }
    public static void addInstallation(String deviceNo, int id, String  model, String  address, String date, String note){

            String sql = "insert into INSTALLED_DEVICES (fabrical_no, client_id, device_model, address, montagedate, note)" +
                    " values ('" + deviceNo + "', " + id + ", '" + model + "', '" + address + "', '" + date + "', '" + note + "')";
            executeInsertStatement(sql);

    }

    public static ResultSet getInstallations (Person person) throws SQLException{
        String sql = "select DEVICE_MODEL, CLIENT_ID, id.FABRICAL_NO deviceNo, ADDRESS," +
                "MONTAGEDATE, DISABLED, id.note instalNote,lc.lastCheckup lastCheckUp, lc.note lastNote\n" +
                "       from INSTALLED_DEVICES id join LASTCHECKUPS lc on id.FABRICAL_NO = lc.fabrical_no where CLIENT_ID = " + person.getId() ;
        return executeSelectStatement(sql);
    }


    public static String getInstallationDate(String fabrical_no){
        String sql = "select MONTAGEDATE from INSTALLED_DEVICES where FABRICAL_NO = '" + fabrical_no + "'";
        try {
            ResultSet resultSet = executeSelectStatement(sql);
            if(resultSet.next()) return resultSet.getString("montagedate");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void updateInstalled_Devices(Installation installation){
        if(installation.getMontagedate().equals(installation.getCheckUpDate())){
           deleteLastCheckUp(installation);
            String sql = "UPDATE INSTALLED_DEVICES SET " +
                    "ADDRESS = '" + installation.getAddress() + "', " +
                    "MONTAGEDATE = '" + installation.getMontagedate() + "', " +
                    "DISABLED = " + installation.getDisabled() + ", " +
                    "DEVICE_MODEL = '" + installation.getModel() + "'," +
                    "NOTE = '" + installation.getInstalNote() + "'" +
                    " WHERE FABRICAL_NO = '" + installation.getFabrical_no() + "'";
            System.out.println(sql);
            executeInsertStatement(sql);
            addToLastCheckups(installation.getFabrical_no(), installation.getMontagedate(), installation.getCheckUpNote());
        }
        else {
            String sql = "UPDATE INSTALLED_DEVICES SET " +
                    "ADDRESS = '" + installation.getAddress() + "', " +
                    "MONTAGEDATE = '" + installation.getMontagedate() + "', " +
                    "DISABLED = " + installation.getDisabled() + ", " +
                    "DEVICE_MODEL = '" + installation.getModel() + "'," +
                    "NOTE = '" + installation.getInstalNote() + "'" +
                    " WHERE FABRICAL_NO = '" + installation.getFabrical_no() + "'";
            System.out.println(sql);
            executeInsertStatement(sql);
        }
    }

    public static void deleteInstallation(Installation installation) throws SQLException{
        String sql = "delete from INSTALLED_DEVICES where FABRICAL_NO = '" + installation.getFabrical_no() + "'";
        executeInsertStatement(sql);
    }

    public static ResultSet getFabricalNos(){
        String sql = "select FABRICAL_NO from INSTALLED_DEVICES";
        try {
            return executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void fillMonthsLists (MonthLists monthLists){
        String sql = "select L.FABRICAL_NO, id.CLIENT_ID, id.DEVICE_MODEL, id.ADDRESS," +
                "id.MONTAGEDATE, id.DISABLED, id.NOTE instalnote, c.FIRSTNAME,\n" +
                "       c.LASTNAME, c.PHONE, LASTCHECKUP, L.note checkupnote\n" +
                "from INSTALLED_DEVICES id join CLIENTS c on id.CLIENT_ID = c.ID\n" +
                "join LASTCHECKUPS L on id.FABRICAL_NO = L.FABRICAL_NO\n" +
                "where DISABLED = 0";
        try {
            ResultSet r = executeSelectStatement(sql);
            while(r.next()){
                Service current = new Service(r.getString("fabrical_no"), r.getString("client_id"),
                        r.getString("device_model"),r.getString("address"), r.getString("montagedate"),
                        r.getString("disabled"), r.getString("instalnote"), r.getString("lastcheckup"),
                        r.getString("checkupnote"),r.getString("firstname"), r.getString("lastname"), r.getString("PHONE"));
                if(!current.isActual()){//if service should be done before this month but weren't
                    monthLists.addLate(current);
                }
                if(current.doneThisMonth()) monthLists.addDoneThisMonth(current);//if was already done this month
                monthLists.add(current); //all to divide
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ObservableList<Service> allInstallations() {
        String sql = "select L.FABRICAL_NO, id.CLIENT_ID, id.DEVICE_MODEL, id.ADDRESS," +
                "id.MONTAGEDATE, id.DISABLED, id.NOTE instalnote, c.FIRSTNAME,\n" +
                "       c.LASTNAME, c.PHONE, LASTCHECKUP, L.note checkupnote\n" +
                "from INSTALLED_DEVICES id join CLIENTS c on id.CLIENT_ID = c.ID\n" +
                "join LASTCHECKUPS L on id.FABRICAL_NO = L.FABRICAL_NO";
        ObservableList<Service> services = FXCollections.observableArrayList();
        try {
            ResultSet r = executeSelectStatement(sql);
            while (r.next()) {
                Service current = new Service(r.getString("fabrical_no"), r.getString("client_id"),
                        r.getString("device_model"), r.getString("address"), r.getString("montagedate"),
                        r.getString("disabled"), r.getString("instalnote"), r.getString("lastcheckup"),
                        r.getString("checkupnote"), r.getString("firstname"), r.getString("lastname"), r.getString("PHONE"));
            services.add(current);
            }
            } catch(SQLException e){
                e.printStackTrace();
            }
        return services;
    }


    //DEVICES statements

    public static ResultSet getModels() throws SQLException{
        String sql = "select MODEL from DEVICES";
        return executeSelectStatement(sql);
    }

    public static void addModel(String model){
        String sql = "insert into DEVICES (MODEL) values ('" + model + "')";
            executeInsertStatement(sql);
    }

    //LASTCHECKUPS statements
    public static void addToLastCheckups(String deviceNo, String firstDate, String note){

        String sql = "insert into lastCheckups (fabrical_no, lastCheckup, note) " +
                "VALUES ('" + deviceNo + "', '" + firstDate + "', '" + note + "' )";
            executeInsertStatement(sql);
    }

    public static void updateLastCheckups(String deviceNo, String date, String note) {
        String sql = "update lastCheckups set lastCheckup = '" + date + "'," +
                "note = '" + note + "' where fabrical_no = '" + deviceNo + "'";
            executeInsertStatement(sql);
    }

    public static String getLastCheckup(String deviceNo){
        String sql = "select LASTCHECKUP from LASTCHECKUPS where FABRICAL_NO = '" + deviceNo + "'";
        try {
            ResultSet resultSet = executeSelectStatement(sql);
            while (resultSet.next()) return resultSet.getString("lastcheckup");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteLastCheckUp(Installation installation){
        String sql = "delete from LASTCHECKUPS where FABRICAL_NO = '" + installation.getFabrical_no() + "'";
        executeInsertStatement(sql);
    }


    //FIX_TABLE statments

    public static ResultSet getFixes(String fabrical_no){
        String sql = "select * from FIX_TABLE where FABRICAL_NO = '" + fabrical_no + "'";
        try {
            return executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addFix(ShortService service){
        String sql = "insert into FIX_TABLE (FABRICAL_NO, FIX_DATE, NOTE) VALUES ('" + service.getFabrical_no() +
                "', '" + service.getCheckUpDate() + "', '" + service.getCheckUpNote() + "')";
        System.out.println(sql);
        executeInsertStatement(sql);
    }

    public static void updateFixes(ShortService oldFix, ShortService newFix){
        String sql = "update FIX_TABLE set FIX_DATE = '" + newFix.getCheckUpDate() + "', " +
                "NOTE = '" + newFix.getCheckUpNote() + "' " +
                "where FABRICAL_NO = '" + oldFix.getFabrical_no() +
                "' and FIX_DATE = '" + oldFix.getCheckUpDate() + "' " +
                "and NOTE = '" + oldFix.getCheckUpNote() + "'";
        executeInsertStatement(sql);
    }

    public static void deleteFix(ShortService fix){
        String sql = "delete from FIX_TABLE where FABRICAL_NO = '" + fix.getFabrical_no() + "' " +
                "and FIX_DATE = '" + fix.getCheckUpDate() + "' and NOTE = '" + fix.getCheckUpNote() + "'";
        executeInsertStatement(sql);
    }
    //CHECKUP_TABLE statments

    public static ResultSet getServices(String fabrical_no){
        String sql = "select * from CHECK_TABLE where FABRICAL_NO = '" + fabrical_no + "'";
        try {
            return executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addService(ShortService service){
    String sql = "insert into CHECK_TABLE (FABRICAL_NO, CHECK_DATE, NOTE) VALUES ('" + service.getFabrical_no() +
            "', '" + service.getCheckUpDate() + "', '" + service.getCheckUpNote() + "')";
        System.out.println(sql);
            executeInsertStatement(sql);
    }

    public static void deleteService(ShortService service){
        String sql = "delete from CHECK_TABLE where FABRICAL_NO = '" + service.getFabrical_no() + "' " +
                "and CHECK_DATE = '" + service.getCheckUpDate() + "' and NOTE = '" + service.getCheckUpNote() + "'";
            executeInsertStatement(sql);
    }

    public static void updateService(ShortService oldService, ShortService newService){
        String sql = "update CHECK_TABLE set CHECK_DATE = '" + newService.getCheckUpDate() + "', " +
                "NOTE = '" + newService.getCheckUpNote() + "' " +
                "where FABRICAL_NO = '" + oldService.getFabrical_no() +
                "' and CHECK_DATE = '" + oldService.getCheckUpDate() + "' " +
                "and NOTE = '" + oldService.getCheckUpNote() + "'";
            executeInsertStatement(sql);
    }

    public static ResultSet getLastCheckUpFromCheckTable(String deviceNo){
        String sql = "select FABRICAL_NO, CHECK_DATE, note from CHECK_TABLE\n" +
                "where FABRICAL_NO = '" + deviceNo + "'\n" +
                "order by CHECK_DATE desc";
        try {
            return executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Service> getAllServices(){
        String sql = "select L.FABRICAL_NO, id.CLIENT_ID, id.DEVICE_MODEL, id.ADDRESS," +
                " id.MONTAGEDATE, id.DISABLED, id.NOTE instalnote, c.FIRSTNAME,\n" +
                "       c.LASTNAME, c.PHONE, L.CHECK_DATE, L.note checkupnote\n" +
                "from INSTALLED_DEVICES id join CLIENTS c on id.CLIENT_ID = c.ID\n" +
                "join CHECK_TABLE L on id.FABRICAL_NO = L.FABRICAL_NO";
        ObservableList<Service> services = FXCollections.observableArrayList();
        try {
            ResultSet r = executeSelectStatement(sql);
            while (r.next()) {
                Service current = new Service(r.getString("fabrical_no"), r.getString("client_id"),
                        r.getString("device_model"), r.getString("address"), r.getString("montagedate"),
                        r.getString("disabled"), r.getString("instalnote"), r.getString("check_date"),
                        r.getString("checkupnote"), r.getString("firstname"), r.getString("lastname"), r.getString("PHONE"));
                services.add(current);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return services;
    }

    public static Integer getYear(String date){
        String threeFields[] = date.split("-");
        return Integer.parseInt(threeFields[0]);
    }
    public static Integer getMonth(String date){
        String threeFields[] = date.split("-");
        return Integer.parseInt(threeFields[1]);
    }
    public static Integer getDay(String date){
        String threeFields[] = date.split("-");
        return Integer.parseInt(threeFields[2]);
    }






    public static void main(String[] args) throws SQLException {

    }
}


