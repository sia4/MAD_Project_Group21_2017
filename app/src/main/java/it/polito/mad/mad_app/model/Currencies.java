package it.polito.mad.mad_app.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Siltes on 28/04/17.
 */

public class Currencies {

    private Map<String, String> Currencies = new TreeMap<>();

    public Currencies() {

        this.Currencies.put("EUR", "Euro EUR €");
        this.Currencies.put("USD", "US Dollars USD $");
        this.Currencies.put("GBP", "Pounds GBP £");
        this.Currencies.put("JPY", "Yen JPY ¥");

    }

    public String getCurrencyString(String code) {

        if (Currencies.containsKey(code)) {
            return Currencies.get(code);
        } else {
            return "";
        }

    }

    public String getCurrencyCode(String s) {

        if (Currencies.containsValue(s)) {

            for (String k : Currencies.keySet()) {
                if (Currencies.get(k).equals(s)) {
                    return k;
                }
            }

        } else {

            return "null";

        }
        return "null";


    }

}
