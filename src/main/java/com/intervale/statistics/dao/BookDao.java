package com.intervale.statistics.dao;

import com.intervale.statistics.exception.RateAlfaBankException;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;

import java.math.BigDecimal;
import java.util.List;

import java.util.Optional;
import java.util.Map;


public interface BookDao {

    String SELECT_BOOK_BY_TITLE = "select b.id, b.isbn, b.title, " +
            "b.author, b.sheets, b.weight, p.price as cost " +
            "from book b\n" +
            "join prices p on p.id = b.price_id\n" +
            "where title ilike ? limit 1";
    String SELECT_HISTORY_BOOKS_PRICE = "select p.date, p.price from prices p\n" +
            "WHERE book_id = (select b.id from book b where title ilike ? limit 1)\n" +
            "order by p.date ASC";
    String ADD_RATE = "INSERT INTO rate (sell_rate, sell_iso, sell_code, buy_rate, buy_iso, buy_code, " +
            "quantity, name, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String GET_RATES_BY_COUNT_DAY = "SELECT * FROM (SELECT * FROM rate ORDER BY id DESC LIMIT ?) " +
            "select_value ORDER BY id";


    Book getCurrentPriceByTitle(String title);

    Map<String, BigDecimal> takeTheHistoryOfBookPriceChange(String title);

    boolean addRate(RateEntity rateEntity);

    Optional<List<RateEntity>> getListRate(Integer dayCount) throws RateAlfaBankException;

}
