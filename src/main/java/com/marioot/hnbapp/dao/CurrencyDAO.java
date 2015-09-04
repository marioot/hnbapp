package com.marioot.hnbapp.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CurrencyDAO {

    private final Logger log = LoggerFactory.getLogger(CurrencyDAO.class);

	private NamedParameterJdbcTemplate jdbcTemplateObject;
	
	@Autowired
	public CurrencyDAO(NamedParameterJdbcTemplate jdbcTemplateObject) {
		this.jdbcTemplateObject = jdbcTemplateObject;
	}
	
	public List<Currency> getCurrencies(List<String> currencies, Date start, Date end, String resolution) {
		String SQL = "SELECT * from currency where name in (:currencies) and date between :startDate and :endDate order by date";
		if (resolution.equals("month")) {
			SQL = "SELECT extract(month from date) as mon, extract(year from date) as year, name, min(date) as date, avg(rate) as rate from currency Where name in (:currencies) and date between :startDate and :endDate group by mon, year, name order by year, mon";
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("currencies", currencies);
		paramMap.put("startDate", start);
		paramMap.put("endDate", end);
		List<Currency> selectedCurrencies = jdbcTemplateObject.query(SQL, paramMap, new BeanPropertyRowMapper<Currency>(Currency.class));

		return selectedCurrencies;
	}
}