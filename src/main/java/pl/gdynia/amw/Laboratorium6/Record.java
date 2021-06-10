package pl.gdynia.amw.Laboratorium6;

import java.util.Date;

// Klasa (pseudo struktura) do przetrzymiania danych pobranych z ECB za pomocą RSS
public class Record {
    private String name;
    private double valueCompareToEuro;
    private Date date;

    Record(){

    }

    public String getName(){
        return name;
    }
    public double getValueCompareToEuro()
    {
        return valueCompareToEuro;
    }
    public Date getDate(){
        return date;
    }

    Record(String name, Double valueCompareToEuro, Date date){
        this.name = name;
        this.valueCompareToEuro = valueCompareToEuro;
        this.date = date;
    }

    public String toString(){
        return "Record{name="+this.name+",valueCompareToEuro="+this.valueCompareToEuro+",date="+this.date+"}";
    }
}