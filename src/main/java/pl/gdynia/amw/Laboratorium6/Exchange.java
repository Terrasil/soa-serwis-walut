package pl.gdynia.amw.Laboratorium6;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Exchange {

    private @Id @GeneratedValue Long id;
    private String baseCurrencyName;
    private String toCurrencyName;
    private Double baseCurrencyValue;
    private Double toCurrencyValue;
    private Double exchangeValue;
    private Date date;

    public Exchange(){
        //throw new ExchangeNotFoundException(this.id);
    }

    public Exchange(String baseCurrencyName, String toCurrencyName, Double baseCurrencyValue, Double toCurrencyValue, Date date) {
        this.baseCurrencyName = baseCurrencyName;
        this.toCurrencyName = toCurrencyName;
        this.baseCurrencyValue = baseCurrencyValue;
        this.toCurrencyValue = toCurrencyValue;
        this.exchangeValue = toCurrencyValue/baseCurrencyValue;
        this.date = date;
    }

    public Long getId() {
        return this.id;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public String getToCurrencyName() {
        return toCurrencyName;
    }

    public Double getBaseCurrencyValue() {
        return baseCurrencyValue;
    }

    public Double getToCurrencyValue() {
        return toCurrencyValue;
    }

    public Double getExchangeValue() {
        return exchangeValue;
    }

    public Date getDate() {
        return date;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public void setToCurrencyName(String toCurrencyName) {
        this.toCurrencyName = toCurrencyName;
    }

    public void setBaseCurrencyValue(Double baseCurrencyValue) {
        this.baseCurrencyValue = baseCurrencyValue;
    }

    public void setToCurrencyValue(Double toCurrencyValue) {
        this.toCurrencyValue = toCurrencyValue;
    }

    public void setExchangeValue(Double exchangeValue) {
        this.exchangeValue = exchangeValue;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Exchange))
            return false;
        Exchange exchange = (Exchange) o;
        return Objects.equals(this.id, exchange.id)
                && Objects.equals(this.baseCurrencyName, exchange.baseCurrencyName)
                && Objects.equals(this.toCurrencyName, exchange.toCurrencyName)
                && Objects.equals(this.baseCurrencyValue, exchange.baseCurrencyValue)
                && Objects.equals(this.toCurrencyValue, exchange.toCurrencyValue)
                && Objects.equals(this.exchangeValue, exchange.exchangeValue)
                && Objects.equals(this.date, exchange.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.baseCurrencyName, this.toCurrencyName, this.baseCurrencyValue, this.toCurrencyValue, this.exchangeValue, this.date);
    }

    @Override
    public String toString() {
        return "Exchange {"
                + "id="+ this.id
                + ", baseCurrencyName='" + this.baseCurrencyName + '\''
                + ", toCurrencyName='" + this.toCurrencyName + '\''
                + ", baseCurrencyValue='" + this.baseCurrencyValue + '\''
                + ", toCurrencyValue='" + this.toCurrencyValue + '\''
                + ", exchangeValue='" + this.exchangeValue + '\''
                + ", date='" + this.date + '\'' + '}';
    }
}
