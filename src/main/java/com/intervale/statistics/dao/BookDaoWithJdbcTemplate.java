package com.intervale.statistics.dao;

import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;


@Slf4j
@Repository
@RequiredArgsConstructor
public class BookDaoWithJdbcTemplate implements BookDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * getCurrentPriceByTitle : получить текущую цену по названию
     * @param title название книги
     * @return получить текущую цену по названию
     */
    @Override
    public Book getCurrentPriceByTitle(String title) {
        try {
            Book book = jdbcTemplate.queryForObject(SELECT_BOOK_BY_TITLE, new BeanPropertyRowMapper<>(Book.class),
                    "%" + title + "%");
            log.info("Prices successfully received by title: {}", title);
            return book;
        } catch (EmptyResultDataAccessException e) {
            log.error("Price not found by title: {}", title);
            return null;
        }
    }

    /**
     * takeTheHistoryOfBookPriceChange : взять историю изменения цен на книги
     * @param title история книги
     * @return возрощает историю измениение цен на книги
     */
    @Override
    public Map<String, BigDecimal> takeTheHistoryOfBookPriceChange(String title) {
        try {
            return jdbcTemplate.query(SELECT_HISTORY_BOOKS_PRICE,
                    ps -> ps.setString(1, "%" + title + "%"),
                    (ResultSetExtractor<Map<String, BigDecimal>>) rs -> {
                        HashMap<String, BigDecimal> result= new LinkedHashMap<>();
                        while(rs.next()){
                            result.put(rs.getString("date"),
                                    rs.getBigDecimal("price"));
                        }
                        return result;
                    });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * addRate : добавить ставку
     * @param rateEntity ставка
     * @return Оценка успешно сохранена в базе данных
     */
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

    /**
     *  getListRate : получить прейскурантную ставку
     * @param dayCount счетчик дней
     * @return  Курсы из базы данных успешно получены
     */
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
