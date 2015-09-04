package com.marioot.hnbapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.marioot.hnbapp.dao.CurrencyDAO;
import com.marioot.hnbapp.domain.Currency;
import com.marioot.hnbapp.repository.CurrencyRepository;
import com.marioot.hnbapp.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Currency.
 */
@RestController
@RequestMapping("/api")
public class CurrencyResource {

    private final Logger log = LoggerFactory.getLogger(CurrencyResource.class);

    @Inject
    private CurrencyRepository currencyRepository;

    @Inject
    private CurrencyDAO currencyDAO;
    
    /**
     * POST  /currencys -> Create a new currency.
     */
    @RequestMapping(value = "/currencys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Currency> create(@Valid @RequestBody Currency currency) throws URISyntaxException {
        log.debug("REST request to save Currency : {}", currency);
        if (currency.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new currency cannot already have an ID").body(null);
        }
        Currency result = currencyRepository.save(currency);
        return ResponseEntity.created(new URI("/api/currencys/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("currency", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /currencys -> Updates an existing currency.
     */
    @RequestMapping(value = "/currencys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Currency> update(@Valid @RequestBody Currency currency) throws URISyntaxException {
        log.debug("REST request to update Currency : {}", currency);
        if (currency.getId() == null) {
            return create(currency);
        }
        Currency result = currencyRepository.save(currency);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("currency", currency.getId().toString()))
                .body(result);
    }

    /**
     * GET  /currencys -> get all the currencys.
     */
    @RequestMapping(value = "/currencys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Currency> getAll() {
        log.debug("REST request to get all Currencys");
        return currencyRepository.findAll();
    }

    /**
     * GET  /currencys/:id -> get the "id" currency.
     */
    @RequestMapping(value = "/currencys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Currency> get(@PathVariable Long id) {
        log.debug("REST request to get Currency : {}", id);
        return Optional.ofNullable(currencyRepository.findOne(id))
            .map(currency -> new ResponseEntity<>(
                currency,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * GET  /currencys/rates -> get specific currencys based on parameters.
     */
    @RequestMapping(value = "/currencys/rates",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<com.marioot.hnbapp.dao.Currency> getCurrentRates(@RequestParam MultiValueMap<String, String> parameters) {
        log.debug("REST request to get parameter specific Currencys");
        String start = parameters.getFirst("start");
        String end = parameters.getFirst("end");
        String resolution = parameters.getFirst("resolution");
        List<String> currencies = parameters.get("currencies");
        log.debug("start: {}, end: {}, resolution: {}, currencies: {}", parameters.get("start"), parameters.get("end"), parameters.get("resolution"), parameters.get("currencies"));
        return currencyDAO.getCurrencies(currencies, new Date(Long.parseLong(start)), new Date(Long.parseLong(end)), resolution);
    }

    /**
     * DELETE  /currencys/:id -> delete the "id" currency.
     */
    @RequestMapping(value = "/currencys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Currency : {}", id);
        currencyRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("currency", id.toString())).build();
    }
}
