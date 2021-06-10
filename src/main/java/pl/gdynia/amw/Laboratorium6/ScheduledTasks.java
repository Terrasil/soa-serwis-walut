package pl.gdynia.amw.Laboratorium6;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.*;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ScheduledTasks {
    @Autowired
    private ExchangeRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    // Lista odebranych rekordów
    private List<Record> records = new ArrayList<Record>();


    // Lista obsługiwanych walut
    private static String[] currencies = {
            "USD", // US dollar
            "JPY", // Japanese yen
            "BGN", // Bulgarian lev
            "CZK", // Czech koruna
            "DKK", // Danish krone
            "EEK", // Estonian kroon
            "GBP", // Pound sterling
            "HUF", // Hungarian forint
            "PLN", // Polish zloty
            "RON", // Romanian leu
            "SEK", // Swedish krona
            "CHF", // Swiss franc
            "ISK", // Icelandic krona
            "NOK", // Norwegian krone
            "HRK", // Croatian kuna
            "RUB", // Russian rouble
            "TRY", // Turkish lira
            "AUD", // Australian dollar
            "BRL", // Brasilian real
            "CAD", // Canadian dollar
            "CNY", // Chinese yuan renminbi
            "HKD", // Hong Kong dollar
            "IDR", // Indonesian rupiah
            "INR", // Indian rupee
            "KRW", // South Korean won
            "MXN", // Mexican peso
            "MYR", // Malaysian ringgit
            "NZD", // New Zealand dollar
            "PHP", // Philippine peso
            "SGD", // Singapore dollar
            "THB", // Thai baht
            "ZAR"  // South African rand
    };

    // Lista dni wolnych (poza eekendami czyli święta) respektowane przez ECB
    String[] publicHolidays={
            "2021-01-01",
            "2021-04-05",
            "2021-05-09",
            "2021-05-13",
            "2021-05-24",
            "2021-06-03",
            "2021-10-03",
            "2021-11-1",
            "2021-12-24",
            "2021-12-25",
            "2021-12-26",
            "2021-12-31",
    };
    //RSS

    String updateToday = null;

    // Odczytanie danych
    public void readData(String currencyName) throws IOException, FeedException {
        URL feedUrl = new URL("https://www.ecb.europa.eu/rss/fxref-"+currencyName.toLowerCase(Locale.ROOT)+".html");

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

        for (Iterator i = feed.getEntries().iterator(); i.hasNext();)
        {
            SyndEntry entry = (SyndEntry) i.next();
            double currencyValue = Double.parseDouble(entry.getTitle().split(" ")[0]);
            records.add(new Record(currencyName,currencyValue,entry.getPublishedDate()));
        }
    }

    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE ,backoff = @Backoff(delay = 300000)) // co 5min ,czyli 300 000 ms
    @Scheduled(cron = "0 15 16 * * MON-FRA")
    //@Retryable
    public void updateDataBase() throws Exception {
        log.info("scheduledTask progress...");
        //Sprawdzenie czy mamy dzien wolny
        for(int i = 0;i<publicHolidays.length;i++){
            if(publicHolidays[i].equals(LocalDate.now())){
                updateToday=publicHolidays[i];
            }
        }

        if(updateToday!=null){
            log.info("scheduledTask HOLYDAY!!!");
        }
        else{
            log.info("scheduledTask download data...");
            for(int i=0;i<currencies.length;i++){
                readData(currencies[i]);
            }
            log.info("scheduledTask download data complete!");

            // Sprawdzanie czy brakuje jakiś rekordów (dodaje te które nie widnieją w bazie)
            for(Record i: records){
                for(Record j: records) {
                    if (i.getDate().compareTo(j.getDate()) == 0) {
                        //sprawdzenie czy dane rekordy są już w bazie
                        if(repository.findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate(i.getName(),j.getName(),i.getValueCompareToEuro(),j.getValueCompareToEuro(),i.getDate()).isEmpty())
                        {
                            repository.save(new Exchange(i.getName(), j.getName(), i.getValueCompareToEuro(), j.getValueCompareToEuro(), i.getDate()));
                        }
                    }
                    // Dodanie porownania z i do euro
                    if(repository.findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate("EUR", i.getName(), 1.0, i.getValueCompareToEuro(),  i.getDate()).isEmpty()) {
                        repository.save(new Exchange("EUR", i.getName(), 1.0, i.getValueCompareToEuro(),  i.getDate()));
                    }
                    if(repository.findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate(i.getName(), "EUR", i.getValueCompareToEuro(), 1.0, i.getDate()).isEmpty()) {
                        repository.save(new Exchange(i.getName(), "EUR", i.getValueCompareToEuro(), 1.0, i.getDate()));
                    }
                }
            }
            String todayTime;
            String lastDateInDataBase = "";
            LocalDate time = LocalDate.now();
            todayTime = time.toString();
            List <Exchange> lastDate;
            lastDate = repository.findTopByOrderByIdDesc();
            // Ostatni rekord przetrzymuje ostatnia datę dodania
            lastDateInDataBase=lastDate.get(0).toString().split(" ")[5].substring(5);

            if(lastDateInDataBase.equals(todayTime)){
                log.info("scheduledTask complete!");
            }
            else{
                log.info("scheduledTask (exceprion) niewykryto zmian... ponawianie...");
                throw new Exception();
            }
        }
    }
}
