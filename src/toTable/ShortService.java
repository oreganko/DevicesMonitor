package toTable;

import javafx.beans.property.SimpleStringProperty;

public class ShortService {


    private final SimpleStringProperty fabrical_no;
    private final SimpleStringProperty checkUpDate;
    private final String checkUpNote;

    public ShortService(String f_no, String checkUpDate, String checkUpNote){
        this.fabrical_no = new SimpleStringProperty(f_no);
        this.checkUpNote = checkUpNote;
        this.checkUpDate = new SimpleStringProperty(checkUpDate);
    }

    public String getFabrical_no() {
        return fabrical_no.get();
    }
    public void setFabrical_no(String fName) {
        fabrical_no.set(fName);
    }



    public String getCheckUpNote() {
        return checkUpNote;
    }

    public String getCheckUpDate() {
        return checkUpDate.get();
    }

    public void setCheckUpDate(String checkUpDdate) {
        this.checkUpDate.set(checkUpDdate);
    }
}


