package it.polito.mad.mad_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Siltes on 28/04/17.
 */

public class Currencies {

    private Map<String, String> Currencies = new TreeMap<>();
    private Map<String, String> Symbols = new TreeMap<>();

    public Currencies() {

        this.Currencies.put("EUR", "EUR €");
        this.Currencies.put("USD", "USD $");
        this.Currencies.put("GBP", "GBP £");
        this.Currencies.put("JPY", "JPY ¥");

        this.Symbols.put("EUR", "€");
        this.Symbols.put("USD", "$");
        this.Symbols.put("GBP", "£");
        this.Symbols.put("JPY", "¥");

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

            return null;

        }
        return null;


    }

    public Set<String> getCurrenciesCodes() {
        return Currencies.keySet();
    }

    public List<String> getCurrenciesStrings() {

        List<String> l = new ArrayList<String>();

        for (Entry<String, String> e : Currencies.entrySet()) {
            l.add(e.getValue());
        }

        return l;
    }

    public String getCurrencySymbol(String s) {
        return Symbols.get(s);
    }

}
