package com.marioot.hnbapp.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marioot.hnbapp.domain.Currency;
import com.marioot.hnbapp.repository.CurrencyRepository;
import com.marioot.hnbapp.service.util.CrawlerUtil;

/**
 * Service for managing currencies.
 * 
 * @author mario
 *
 */
@Service
@Transactional
public class CurrencyService {

	private final Logger log = LoggerFactory.getLogger(CurrencyService.class);

	@Inject
	CurrencyRepository currencyRepository;
	/**
	 * Retrieves the latest currency exchange rates by using a python script
	 * <p/>
	 * <p>
	 * This is scheduled to get fired everyday, at 01:00 (am).
	 * </p>
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void fetchLatestExchangeRates() {
		LocalDate today = new LocalDate();
		log.debug("Executing cron job for fetching latest currency rates: " + today.toString());
		
		List<Currency> newRates = new ArrayList<Currency>();
		newRates = CrawlerUtil.fetchCurrencyRates(today);
		currencyRepository.save(newRates);
		log.debug("Retrieved and inserted into database {} new entries.", newRates.size());
	}
}
