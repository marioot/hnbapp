package com.marioot.hnbapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marioot.hnbapp.domain.Currency;
import com.marioot.hnbapp.repository.CurrencyRepository;

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
		DateTime today = new DateTime();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("ddMMyy");
		String todayStr = fmt.print(today);
		Process p;
		try {
			List<Currency> newRates = new ArrayList<Currency>();
			log.debug("Executing cron: " + todayStr);
			p = Runtime.getRuntime().exec("python src/main/resources/scripts/hnbscraper.py " + todayStr);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				line = line.replace("'", "");
				String[] currencies = line.split("], \\[");
				currencies[0] = currencies[0].substring(2);
				currencies[currencies.length - 1] = currencies[currencies.length - 1].substring(0, currencies[currencies.length - 1].length() - 2);
				for (int i = 0; i < currencies.length; i++) {
					log.debug(currencies[i]);
					newRates.add(parseCurrency(currencies[i]));
				}
			}
			currencyRepository.save(newRates);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Currency parseCurrency(String currencyStr) {
		String[] elements = currencyStr.split(", ");
		Currency c = new Currency();
		c.setName(elements[0].trim());
		c.setUnit(Integer.parseInt(elements[1].trim()));
		c.setRate(Double.parseDouble(elements[2].trim().replace(",", ".")));
		c.setDate(LocalDate.parse(elements[3].trim(), DateTimeFormat.forPattern("ddMMyyyy")));
		log.debug(c.toString());
		return c;
	}
}
