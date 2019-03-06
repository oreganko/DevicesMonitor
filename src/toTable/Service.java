package toTable;

import javafx.beans.property.SimpleStringProperty;

import java.util.Calendar;
import java.util.Objects;

import static database.StoredProcedures.getMonth;
import static database.StoredProcedures.getYear;

public class Service {

    private final SimpleStringProperty fabrical_no;
    private final SimpleStringProperty client_id;
    private final SimpleStringProperty clientFirstName;
    private final SimpleStringProperty clientLastName;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty model;
    private final SimpleStringProperty address;
    private final SimpleStringProperty montagedate;
    private final SimpleStringProperty checkUpDate;
    private final SimpleStringProperty disabled;
    private final String instalNote;
    private final String checkUpNote;

    public Service(String f_no, String c_id, String model, String address, String montagedate, String disabled, String instalNote, String checkUpDate,
                        String checkUpNote, String clientFName, String clientLName, String clientPhone){
        this.fabrical_no = new SimpleStringProperty(f_no);
        this.client_id = new SimpleStringProperty(c_id);
        this.clientFirstName = new SimpleStringProperty(clientFName);
        this.clientLastName = new SimpleStringProperty(clientLName);
        this.model = new SimpleStringProperty(model);
        this.address = new SimpleStringProperty(address);
        this.montagedate = new SimpleStringProperty(montagedate);
        this.disabled = new SimpleStringProperty(disabled);
        this.instalNote = instalNote;
        this.checkUpNote = checkUpNote;
        this.checkUpDate = new SimpleStringProperty(checkUpDate);
        this.phone = new SimpleStringProperty(clientPhone);
    }

    public boolean isActual(){
        if(getDisabled().equals("1")) return true;
        String lastCheckUp = this.getCheckUpDate();
        if (lastCheckUp == null || lastCheckUp.trim().equals("")) return true;
        Calendar now = Calendar.getInstance();
        int checkUpMonth = getMonth(lastCheckUp);
        int checkUpYear = getYear(lastCheckUp);
        if(checkUpYear + 1 < now.get(Calendar.YEAR)) return false;
        if(checkUpYear + 1 == now.get(Calendar.YEAR)){
            if(checkUpMonth < now.get(Calendar.MONTH)+1) return false;
        }
        return true;
    }
    public boolean isActualThisMonth(){
        if(getDisabled().equals("1")) return true;
        String lastCheckUp = this.getCheckUpDate();
        if (lastCheckUp == null || lastCheckUp.trim().equals("")) return true;
        Calendar now = Calendar.getInstance();
        int checkUpMonth = getMonth(lastCheckUp);
        int checkUpYear = getYear(lastCheckUp);
        if(checkUpYear + 1 < now.get(Calendar.YEAR)) return false;
        if(checkUpYear + 1 == now.get(Calendar.YEAR)){
            if(checkUpMonth <= now.get(Calendar.MONTH)+1) return false;
        }
        return true;
    }
    public boolean doneThisMonth(){
        String lastCheckUp = this.getCheckUpDate();
        Calendar now = Calendar.getInstance();
        int checkUpMonth = getMonth(lastCheckUp);
        int checkUpYear = getYear(lastCheckUp);
        if(checkUpYear == now.get(Calendar.YEAR) && checkUpMonth == now.get(Calendar.MONTH) + 1) return true;
        return false;
    }


    public Person ownerOf(){
        return new Person(getClientFirstName(), getClientLastName(), getPhone() ,getClient_id());
    }

    public Installation installationOf(){
        return new Installation(getFabrical_no(), getClient_id(), getModel(), getAddress(), getMontagedate(), getDisabled(), getInstalNote(),getCheckUpDate(),getCheckUpNote());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return fabrical_no.equals(service.fabrical_no);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fabrical_no);
    }

    public String getFabrical_no() {
        return fabrical_no.get();
    }
    public void setFabrical_no(String fName) {
        fabrical_no.set(fName);
    }

    public String getClient_id() {
        return client_id.get();
    }
    public void setClient_id(String client_id) {
        this.client_id.set(client_id);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getMontagedate() {
        return montagedate.get();
    }

    public void setMontagedate(String montagedate) {
        this.montagedate.set(montagedate);
    }

    public String getDisabled() {
        return disabled.get();
    }

    public void setDisabled(String disabled) {
        this.disabled.set(disabled);
    }

    public String getInstalNote() {
        return instalNote;
    }

    public String getCheckUpNote() {
        return checkUpNote;
    }

    public String getClientFirstName() {
        return clientFirstName.get();
    }

    public SimpleStringProperty clientFirstNameProperty() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName.set(clientFirstName);
    }

    public String getClientLastName() {
        return clientLastName.get();
    }

    public SimpleStringProperty clientLastNameProperty() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName.set(clientLastName);
    }

    public String getModel() {
        return model.get();
    }

    public void setModel(String model) {
        this.model.set(model);
    }

    public String getCheckUpDate() {
        return checkUpDate.get();
    }

    public void setCheckUpDate(String checkUpDdate) {
        this.checkUpDate.set(checkUpDdate);
    }

    public String getPhone() {
        return phone.get();
    }

    public SimpleStringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
}
