package com.marioot.hnbapp.repository;

import com.marioot.hnbapp.domain.Currency;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Currency entity.
 */
public interface CurrencyRepository extends JpaRepository<Currency,Long> {

    List<Currency> findAllByNameInAndDateBetween(List<Object> currencies,DateTime start, DateTime end);

    @Query("SELECT c FROM Currency c WHERE c.name IN :currencies and c.date BETWEEN :start AND :end")
    List<Currency> findByNameAndDate(@Param("currencies") List<Object> currencies,
                                   @Param("start") DateTime start, 
                                   @Param("start") DateTime end);
}
