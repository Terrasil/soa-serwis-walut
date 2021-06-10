package pl.gdynia.amw.Laboratorium6;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findExchangeByBaseCurrencyNameAndAndToCurrencyNameAndBaseCurrencyValueAndToCurrencyValueAndDate(String baseCurrencyName, String toCurrencyName, double baseCurrencyValue, double toCurrencyValue, Date date);
    List<Exchange> findTopByOrderByIdDesc();
    List<Exchange>findTopByOrderByIdAsc();
}