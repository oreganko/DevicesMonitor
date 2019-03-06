package toTable;

import javafx.beans.property.SimpleStringProperty;

public class Person {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty id;

    public Person(String fName, String lName, String phone, String id) {
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.phone = new SimpleStringProperty(phone);
        this.id = new SimpleStringProperty(id);
    }

    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getId() {
        return id.get();
    }
    public void setId(String fName) {
        id.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public String getPhone() {
        return phone.get();
    }
    public void setPhone(String fName) {
        phone.set(fName);
    }

}
