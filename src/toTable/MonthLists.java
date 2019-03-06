package toTable;

import database.Months;
import database.StoredProcedures;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MonthLists {

    private ObservableList<Service> januaryList = FXCollections.observableArrayList();
    private ObservableList<Service> februaryList = FXCollections.observableArrayList();
    private ObservableList<Service> marchList = FXCollections.observableArrayList();
    private ObservableList<Service> aprilList = FXCollections.observableArrayList();
    private ObservableList<Service> mayList = FXCollections.observableArrayList();
    private ObservableList<Service> juneList = FXCollections.observableArrayList();
    private ObservableList<Service> julyList = FXCollections.observableArrayList();
    private ObservableList<Service> augustList = FXCollections.observableArrayList();
    private ObservableList<Service> septemberList = FXCollections.observableArrayList();
    private ObservableList<Service> octoberList = FXCollections.observableArrayList();
    private ObservableList<Service> novemberList = FXCollections.observableArrayList();
    private ObservableList<Service> decemberList = FXCollections.observableArrayList();
    private ObservableList<Service> lateList = FXCollections.observableArrayList();
    private ObservableList<Service> doneThisMonth = FXCollections.observableArrayList();

    private Map<Integer, ObservableList<Service>> map;
    public MonthLists(){
        map = new LinkedHashMap<>();
        map.put(0, lateList);
        map.put(1,januaryList);
        map.put(2, februaryList);
        map.put(3, marchList);
        map.put(4, aprilList);
        map.put(5, mayList);
        map.put(6, juneList);
        map.put(7, julyList);
        map.put(8, augustList);
        map.put(9, septemberList);
        map.put(10, octoberList);
        map.put(11, novemberList);
        map.put(12, decemberList);
        map.put(13, doneThisMonth);
        StoredProcedures.fillMonthsLists(this);
    }

    public void add(Service service){
        map.get(StoredProcedures.getMonth(service.getCheckUpDate())).add(service);
    }

    public void addLate(Service service){
        map.get(0).add(service);
    }
    public void addDoneThisMonth(Service service){map.get(13).add(service);}

    public ObservableList<Service> getList(String month) {
        System.out.println("to ta wartość: "+ month);
        Months monthM = Months.fromString(month);
        return map.get(monthM.getValue()+1);
    }

    public ObservableList<Service> mergeLateToMonth(String month){
        ObservableList<Service> merged = FXCollections.observableArrayList();
        merged.addAll(getList(month));
        for(Service x : lateList)
            if(!merged.contains(x)) merged.add(x);
        return merged;
    }
    public ObservableList<Service> showForThisMonth(boolean showLate, boolean showDone){
        ObservableList<Service> toShow = FXCollections.observableArrayList();
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        toShow.addAll(map.get(month));
        if(showLate){
            for(Service x : lateList)
                if(!toShow.contains(x)) toShow.add(x);
        }
        if(!showDone){
            toShow.removeAll(map.get(13));
        }
        return toShow;
    }
}
