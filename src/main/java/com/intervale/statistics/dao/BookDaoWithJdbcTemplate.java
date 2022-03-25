package com.intervale.statistics.dao;

import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookDaoWithJdbcTemplate implements BookDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Book getPriceByTitle(String title) {
        try {
            Book book = jdbcTemplate.queryForObject(selectBookByTitle, new BeanPropertyRowMapper<>(Book.class),
                    "%" + title + "%");
            log.info("Prices successfully received by title: {}", title);
            return book;
        } catch (EmptyResultDataAccessException e) {
            log.error("Price not found by title: {}", title);
            return null;
        }
    }

    @Override
    public boolean addRate(RateEntity rateEntity) {

        try {
            jdbcTemplate.update(ADD_RATE, rateEntity.getSellRate(), rateEntity.getSellIso(), rateEntity.getSellCode(),
                    rateEntity.getBuyRate(), rateEntity.getBuyIso(), rateEntity.getBuyCode(), rateEntity.getQuantity(),
                    rateEntity.getName(), rateEntity.getDate());

            log.info("Rate successfully saved into data base. Rate: {}", rateEntity);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.error("Rate do not saved into data base. Rate: {}", rateEntity);
            return false;
        }
    }

    @Override
    public Optional<List<RateEntity>> getListRate(Integer dayCount) {

        List<RateEntity> resultQuery = null;
        int rateCount = dayCount * 3;

        try {
            resultQuery = jdbcTemplate
                    .query(GET_RATES_BY_COUNT_DAY, new BeanPropertyRowMapper<>(RateEntity.class), rateCount);

            log.info("Rates from data base successfully received");

        } catch (EmptyResultDataAccessException e) {
            log.error("Rates from data base do not received");
        }
        return Optional.ofNullable(resultQuery);
    }
}
