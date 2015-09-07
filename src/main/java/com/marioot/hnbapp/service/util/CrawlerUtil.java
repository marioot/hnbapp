package com.marioot.hnbapp.service.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marioot.hnbapp.domain.Currency;

/**
 * Utility crawler that performs the currency data retrieval by calling an resource python tool
 * 
 * @author mario
 *
 */
public final class CrawlerUtil {

	private static final String CRAWLER_PATH = "src/main/resources/scripts/hnbscraper.py";
	private static final Logger log = LoggerFactory.getLogger(CrawlerUtil.class);
	
	/**
	 * Fetches the currency rates for the provided date.
	 * Calls the hnbscraper.py tool in a seperate process.
	 * 
	 * @param date
	 * @return
	 */
	public static List<Currency> fetchCurrencyRates(LocalDate date) {
		File f = new File(CRAWLER_PATH);
		if(!f.exists()) { 
			log.error("Missing the crawler script! {}", CRAWLER_PATH);
		} else {
			log.debug("Crawler script exists...");
		}
		
		log.debug("Fetching currency rate for date: {}", date);
		DateTimeFormatter fmt = DateTimeFormat.forPattern("ddMMyy");
		String dateStr = fmt.print(date);

		List<Currency> rates = new ArrayList<Currency>();
		ProcessBuilder builder = new ProcessBuilder("python", CRAWLER_PATH, dateStr);
		Process p;
		try {
			p = builder.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String line;
			while ((line = in.readLine()) != null) {
				line = line.replace("'", "");
				String[] currencies = line.split("], \\[");
				currencies[0] = currencies[0].substring(2);
				currencies[currencies.length - 1] = currencies[currencies.length - 1].substring(0, currencies[currencies.length - 1].length() - 2);
				for (int i = 0; i < currencies.length; i++) {
					Currency c = parseCurrency(currencies[i]);
					if (c != null) {
						rates.add(c);				
					}
				}
			}
			while ((line = err.readLine()) != null) {
				log.error(line);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		if (rates.isEmpty()) {
			log.debug("{}: No data available", date.toString());
		} else {
			log.debug("{}: {} currencies fetched", date.toString(), rates.size());			
		}
		return rates;
	}
	
	private static Currency parseCurrency(String currencyStr) {
		if (currencyStr.isEmpty()) {
			return null;
		}
		String[] elements = currencyStr.split(", ");
		Currency c = new Currency();
		c.setName(elements[0].trim());
		c.setUnit(Integer.parseInt(elements[1].trim()));
		c.setRate(Double.parseDouble(elements[2].trim().replace(",", ".")));
		c.setDate(LocalDate.parse(elements[3].trim(), DateTimeFormat.forPattern("ddMMyyyy")));
		return c;
	}
}
