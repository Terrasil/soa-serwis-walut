package pl.gdynia.amw.Laboratorium6;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
public class ExchangeController {

    private final ExchangeRepository repository;
    ExchangeController(ExchangeRepository repository){
        this.repository = repository;
    }

    @GetMapping("/exchange/")
    List<Exchange>
    find(@RequestParam(value = "baseCurrencyName", defaultValue = "") String baseCurrencyName,
         @RequestParam(value = "toCurrencyName", defaultValue = "") String toCurrencyName,
         @RequestParam(value = "date", defaultValue = "")String date
         ) {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "date"))
                .stream()
                //jesli podano cokolwiek to wykonaj
                .filter((e) -> {
                    if(!baseCurrencyName.equals("")){
                        //jeżeli warunek jest spelniony w tym przypadku baseCurrencyName to zostaje zapisany, inaczej jest wyrzucany
                        return (e.getBaseCurrencyName().equals(baseCurrencyName.toUpperCase(Locale.ROOT)));
                    }
                    else return true;
                })
                .collect((Collectors.toList()))
                .stream()
                //jesli podano cokolwiek to wykonaj
                .filter((e)->{
                    if(!toCurrencyName.equals("")){
                        //jeżeli warunek jest spelniony w tym przypadku toCurrencyName to zostaje zapisany inaczej jest wyrzucany
                        return (e.getToCurrencyName().equals(toCurrencyName.toUpperCase(Locale.ROOT)));
                    }else return true;
                })
                .collect(Collectors.toList())
                .stream()
                .filter((e)->{
                    if(!date.equals("")){
                        List <Exchange> lastDateExchange = repository.findTopByOrderByIdAsc();
                        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String lastDateInDataBase = formatter.format(lastDateExchange.get(0).getDate());
                        return(formatter.format(e.getDate()).equals(date) || formatter.format(e.getDate()).equals(lastDateInDataBase));
                    }else return true;
                })
                .collect(Collectors.toList())
                .stream()
                .limit(!date.equals("") ? 1 : repository.count())
                .collect(Collectors.toList());
    }

    @GetMapping("/exchanges/")
    List<Exchange> all() {
        return repository.findAll();
    }
}