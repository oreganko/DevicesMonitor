package database;

import toTable.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Months {
    styczeń(0),
    luty(1),
    marzec(2),
    kwiecień(3),
    maj(4),
    czerwiec(5),
    lipiec(6),
    sierpień(7),
    wrzesień(8),
    październik(9),
    listopad(10),
    grudzień(11);

        private int value;
        private static Map map = new HashMap<>(12);

        private Months(int value) {
            this.value = value;
        }

        static {
            for (Months mont : Months.values()) {
                map.put(mont.value, mont);
            }
        }

        public static Months valueOf(int month) {
            return (Months) map.get(month);
        }

        public static Months realValueOf(int month){
            return (Months) map.get(month-1);
        }

        public int getValue() {
            return value;
        }

        public static Months fromString(String month){
            switch (month){
                case "styczeń":
                    return styczeń;
                case "luty":
                    return luty;
                case "marzec":
                    return marzec;
                case "kwiecień":
                    return kwiecień;
                case "maj":
                    return maj;
                case "czerwiec":
                    return czerwiec;
                case "lipiec":
                    return lipiec;
                case "sierpień":
                    return sierpień;
                case "wrzesień":
                    return wrzesień;
                case "październik":
                    return październik;
                case "listopad":
                    return listopad;
                case "grudzień":
                    return grudzień;
                default:
                    System.out.println("TA WARTOŚĆ PRZESZKADZA: ");
                    return luty;
            }
        }


    }

