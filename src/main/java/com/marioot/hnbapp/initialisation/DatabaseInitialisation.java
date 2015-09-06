package com.marioot.hnbapp.initialisation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.marioot.hnbapp.domain.Currency;
import com.marioot.hnbapp.repository.CurrencyRepository;
import com.marioot.hnbapp.service.util.CrawlerUtil;

/**
 * Class that performs checks and performs updates on the database to ensure the latest data is available.
 * 
 * @author mario
 *
 */
@Component
public class DatabaseInitialisation {
	
    private static final Logger log = LoggerFactory.getLogger(DatabaseInitialisation.class);
	
    private static CurrencyRepository currencyRepository;
    
    @Autowired
    public DatabaseInitialisation(CurrencyRepository currencyRepository) {
    	DatabaseInitialisation.currencyRepository = currencyRepository;
    }
    
    @PostConstruct
	public static void checkDatabase() {
		log.info("Checking database...");
		
		List<Currency> currencies = currencyRepository.findAll(new Sort(Direction.DESC, "date"));
		
		LocalDate lastDate = currencies.size() == 0 ? new LocalDate().minusYears(1) : currencies.get(0).getDate();
		LocalDate currentDate = new LocalDate();
		
		if (lastDate.isBefore(currentDate)) {
			log.info("Database outdated, latest date: {}", lastDate.toString());
			populateDatabase(lastDate.plusDays(1), currentDate);
		} else {
			log.info("Database is up to date, latest date: {}", lastDate.toString());
		}
	}
	
	private static void populateDatabase(LocalDate lastDate, LocalDate currentDate) {
		log.info("Fetching data for period between {} and {}...", lastDate.toString(), currentDate.toString());

		List<Currency> newRates = new ArrayList<Currency>();
		while (lastDate.compareTo(currentDate) < 1) {
			newRates.addAll(CrawlerUtil.fetchCurrencyRates(lastDate));
			lastDate = lastDate.plusDays(1);
		}
		log.info("Populating database...");
		currencyRepository.save(newRates);
		log.info("Inserted {} new entries.", newRates.size());
	}
}
