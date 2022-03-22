package com.intervale.statistics.dao;

import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookDaoWithJdbcTemplate implements BookDao{

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
    public List<RateEntity> getListRate(String dataRange) {

        try {
            List<RateEntity> rateEntityList = jdbcTemplate
                    .query(GET_ALL_RATES, new BeanPropertyRowMapper<>(RateEntity.class));

            log.info("Rates from data base successfully received");
            return rateEntityList;
        } catch (EmptyResultDataAccessException e) {
            log.error("Rates from data base do not received");
            return null;
        }
    }
}
