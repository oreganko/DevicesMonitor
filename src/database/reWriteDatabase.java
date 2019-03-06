package database;

import installation.CreateDB;
import toTable.ShortService;

import java.nio.channels.FileLockInterruptionException;
import java.sql.*;

public class reWriteDatabase {

    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String JDBC_URL = "jdbc:derby:C:\\Users\\Ja\\Desktop\\deviceMon;";

    private Connection getConnected() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(JDBC_URL);
        return connection;
    }

    private ResultSet executeSelectStatement(String sql)throws SQLException {
        ResultSet resultSet = null;
        Statement statement = getConnected().createStatement();
        resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    private void addClient(int id, String firstname, String lastname, String phone_no){
        String sql = "insert into clients (id, firstname, lastname, phone) values ("+ id + ", '" + firstname + "', '" + lastname +"', '" + phone_no + "')";
        StoredProcedures.executeInsertStatement(sql);
        System.out.println(sql);
    }

    private void rewriteClients(){
        String sql = "select * from clients";
        ResultSet clientsTaken = null;
        try {
            clientsTaken = executeSelectStatement(sql);
            System.out.println("Pobrano");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (clientsTaken.next()) {
                System.out.println("NO hej");
                addClient(clientsTaken.getInt("id"), clientsTaken.getString("firstname"),
                        clientsTaken.getString("lastname"), clientsTaken.getString("phone"));

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void rewriteDevices(){
        String sql = "select * from devices";
        ResultSet taken = null;
        try {
            taken = executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (taken.next()) StoredProcedures.addModel(taken.getString("model"));
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void rewriteInstalled_Devices(){
        String sql = "select * from installed_devices";
        //String sql = "insert into INSTALLED_DEVICES (fabrical_no, client_id, device_model, address, montagedate, note)" +
        //                    " values ('" + deviceNo + "', " + id + ", '" + model + "', '" + address + "', '" + date + "', '" + note + "')";
        ResultSet taken = null;
        try {
            taken = executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (taken.next()) StoredProcedures.addInstallation(taken.getString("fabrical_no"),taken.getInt("client_id"),
                    taken.getString("device_model"), taken.getString("address"), taken.getString("montagedate"),
                    taken.getString("note"));
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void rewriteCheckTable(){
        String sql = "select * from check_table";
        ResultSet taken = null;
        try {
            taken = executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (taken.next()) StoredProcedures.addService(new ShortService(taken.getString("fabrical_no"),
                    taken.getString("check_date"), taken.getString("note")));
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void rewriteLastCheckup(){
        String sql = "select * from lastcheckups";
        ResultSet taken = null;
        try {
            taken = executeSelectStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (taken.next()) StoredProcedures.addToLastCheckups(taken.getString("fabrical_no"),
                    taken.getString("lastcheckup"), taken.getString("note"));
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            CreateDB.main(args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileLockInterruptionException e) {
            e.printStackTrace();
        }
        reWriteDatabase rd = new reWriteDatabase();
        rd.rewriteClients();
        rd.rewriteDevices();
        rd.rewriteInstalled_Devices();
        rd.rewriteCheckTable();
        rd.rewriteLastCheckup();
    }
}
