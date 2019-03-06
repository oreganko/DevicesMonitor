package toTable;

import javafx.beans.property.SimpleStringProperty;

import java.util.Calendar;
import database.StoredProcedures;

import static database.StoredProcedures.*;

public class Installation {

    private final SimpleStringProperty fabrical_no;
    private final SimpleStringProperty client_id;
    private final SimpleStringProperty model;
    private final SimpleStringProperty address;
    private final SimpleStringProperty montagedate;
    private final SimpleStringProperty checkUpDate;
    private final SimpleStringProperty disabled;
    private String instalNote;
    private String checkUpNote;

    public Installation(String f_no, String c_id, String model, String address, String montagedate, String disabled, String instalNote, String checkUpDate,
                        String checkUpNote){
        this.fabrical_no = new SimpleStringProperty(f_no);
        this.client_id = new SimpleStringProperty(c_id);
        this.model = new SimpleStringProperty(model);
        this.address = new SimpleStringProperty(address);
        this.montagedate = new SimpleStringProperty(montagedate);
        this.disabled = new SimpleStringProperty(disabled);
        this.instalNote = instalNote;
        this.checkUpNote = checkUpNote;
        this.checkUpDate = new SimpleStringProperty(checkUpDate);
    }

    public boolean isActual(){
        if(getDisabled().equals("1"))return true;
        String lastCheckUp = this.getCheckUpDate();
        Calendar now = Calendar.getInstance();
        int checkUpMonth = getMonth(lastCheckUp);
        int checkUpYear = getYear(lastCheckUp);
        if(checkUpYear + 1 < now.get(Calendar.YEAR)) return false;
        if(checkUpYear + 1 == now.get(Calendar.YEAR)){
            if(checkUpMonth <= now.get(Calendar.MONTH)+1) return false;
        }
        return true;
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
    public void setInstalNote(String note) {this.instalNote = note;}
    public void setCheckUpNote(String note) {this.checkUpNote = note;}

    public String getCheckUpNote() {
        return checkUpNote;
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


}
