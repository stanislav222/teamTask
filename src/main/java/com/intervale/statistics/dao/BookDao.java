package com.intervale.statistics.dao;

import com.intervale.statistics.exception.RateAlfaBankException;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    String selectBookByTitle = "select * from book where title ilike ? limit 1";
    String ADD_RATE = "INSERT INTO rate (sell_rate, sell_iso, sell_code, buy_rate, buy_iso, buy_code, " +
            "quantity, name, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String GET_RATES_BY_COUNT_DAY = "SELECT * FROM (SELECT * FROM rate ORDER BY id DESC LIMIT ?) " +
            "select_value ORDER BY id";


    Book getPriceByTitle(String title);

    boolean addRate(RateEntity rateEntity);

    Optional<List<RateEntity>> getListRate(Integer dayCount) throws RateAlfaBankException;

}
