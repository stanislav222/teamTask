package com.intervale.statistics.dao;

import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.model.entity.Book;
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
    public boolean addRate(RateDto rateDto) {
        return false;
    }

    @Override
    public List<RateDto> getListRate(String dataRange) {
        return null;
    }
}
