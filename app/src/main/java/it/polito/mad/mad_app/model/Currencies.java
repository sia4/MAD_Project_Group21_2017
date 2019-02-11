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

        this.Currencies.put("AUD", "AUD $");
        this.Currencies.put("BGN", "BGN лв");
        this.Currencies.put("BRL", "BRL R$");
        this.Currencies.put("CAD", "CAD $");
        this.Currencies.put("CHF", "CHF CHF");
        this.Currencies.put("CNY", "CNY ¥");
        this.Currencies.put("CZK", "CZK Kč");
        this.Currencies.put("DKK", "DKK kr");
        this.Currencies.put("HKD", "HKD $");
        this.Currencies.put("HRK", "HRK kn");
        this.Currencies.put("HUF", "HUF Ft");
        this.Currencies.put("IDR", "IDR Rp");
        this.Currencies.put("ILS", "ILS ₪");
        this.Currencies.put("INR", "INR ₹");
        this.Currencies.put("KRW", "KRW ₩");
        this.Currencies.put("MXN", "MXN $");
        this.Currencies.put("MYR", "MYR RM");
        this.Currencies.put("NOK", "NOK kr");
        this.Currencies.put("NZD", "NZD $");
        this.Currencies.put("PHP", "PHP ₱");
        this.Currencies.put("PLN", "PLN zł");
        this.Currencies.put("RON", "RON lei");
        this.Currencies.put("RUB", "RUB \u20BD");
        this.Currencies.put("SEK", "SEK kr");
        this.Currencies.put("SGD", "SGD $");
        this.Currencies.put("THB", "THB ฿");
        this.Currencies.put("TRY", "TRY ₺");
        this.Currencies.put("ZAR", "ZAR R");

        this.Symbols.put("EUR", "€");
        this.Symbols.put("USD", "$");
        this.Symbols.put("GBP", "£");
        this.Symbols.put("JPY", "¥");
        this.Symbols.put("AUD", "A");

        this.Symbols.put("AUD", "$");
        this.Symbols.put("BGN", "лв");
        this.Symbols.put("BRL", "R$");
        this.Symbols.put("CAD", "$");
        this.Symbols.put("CHF", "CHF");
        this.Symbols.put("CNY", "¥");
        this.Symbols.put("CZK", "Kč");
        this.Symbols.put("DKK", "kr");
        this.Symbols.put("HKD", "$");
        this.Symbols.put("HRK", "kn");
        this.Symbols.put("HUF", "Ft");
        this.Symbols.put("IDR", "Rp");
        this.Symbols.put("ILS", "₪");
        this.Symbols.put("INR", "₹");
        this.Symbols.put("KRW", "₩");
        this.Symbols.put("MXN", "$");
        this.Symbols.put("MYR", "RM");
        this.Symbols.put("NOK", "kr");
        this.Symbols.put("NZD", "$");
        this.Symbols.put("PHP", "₱");
        this.Symbols.put("PLN", "zł");
        this.Symbols.put("RON", "lei");
        this.Symbols.put("RUB", "\u20BD");
        this.Symbols.put("SEK", "kr");
        this.Symbols.put("SGD", "$");
        this.Symbols.put("THB", "฿");
        this.Symbols.put("TRY", "₺");
        this.Symbols.put("ZAR", "R");

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
