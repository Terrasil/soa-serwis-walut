package pl.gdynia.amw.Laboratorium6;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.*;
import java.nio.file.*;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    @Autowired
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    // codzienne aktualizowanie
    private Thread aktualizacja = new Thread();
    private void dziennaAktualizacja(){};

    // Date format "EEE MMM d hh:mm:ss z YYYY"
    private Date timeNow = new Date();

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

    @Bean
    CommandLineRunner initDatabase(ExchangeRepository repository) throws FeedException, IOException {
        return args -> {
            log.info("initDatabase...");// Pobranie informacji o kursach danych z ostatnich 5 dni roboczych (gdy dane były aktualizowane)
            for(int i = 0; i < currencies.length; i++){
                log.info(currencies[i] + " loading currency data...");
                readData(currencies[i]);
                log.info(currencies[i] + " loading currency data complete!");
            }
            //sql.open();
            log.info("Update sqldatabase...");
            for(int x = 0; x < records.size(); x++){
                //log.info("Calculate permutation for " + records.get(x).toString());
                for(int y = 0; y < records.size(); y++) {
                    if (records.get(x).getDate().compareTo(records.get(y).getDate()) == 0) { // Przeliczane wartości muszą byż z tego samego dnia
                        if(repository.findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate(records.get(x).getName(), records.get(y).getName(), records.get(x).getValueCompareToEuro(), records.get(y).getValueCompareToEuro(), records.get(x).getDate()).isEmpty()) {
                            repository.save(new Exchange(records.get(x).getName(), records.get(y).getName(), records.get(x).getValueCompareToEuro(), records.get(y).getValueCompareToEuro(), records.get(x).getDate()));
                        }
                    }
                }
                // Dodanie porownania z i do euro
                if(repository.findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate("EUR", records.get(x).getName(), 1.0, records.get(x).getValueCompareToEuro(),  records.get(x).getDate()).isEmpty()) {
                    repository.save(new Exchange("EUR", records.get(x).getName(), 1.0, records.get(x).getValueCompareToEuro(),  records.get(x).getDate()));
                }
                if(repository.findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate(records.get(x).getName(), "EUR", records.get(x).getValueCompareToEuro(), 1.0, records.get(x).getDate()).isEmpty()) {
                    repository.save(new Exchange(records.get(x).getName(), "EUR", records.get(x).getValueCompareToEuro(), 1.0, records.get(x).getDate()));
                }
            }
            log.info("Update sqldatabase complete!");
            //sql.save(new Exchange("EUR", "EUR", 1.0, 1.0, new Date()));
            //sql.close();
            log.info("initDatabase complete!");
        };
    }
}
