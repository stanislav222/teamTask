package com.intervale.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Integer id;
    private String isbn;
    private String title;
    private String author;
    private String sheets;
    private String weight;
    private BigDecimal cost;
    private int isDeleted;

    /**
     *
     * @param isbn
     * @param title
     * @param author
     * @param sheets
     * @param weight
     * @param cost
     */
    public Book(String isbn, String title, String author, String sheets, String weight, BigDecimal cost) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.sheets = sheets;
        this.weight = weight;
        this.cost = cost;
    }

    /**
     *
     * @param id
     * @param isbn
     * @param title
     * @param author
     * @param sheets
     * @param weight
     * @param cost
     */
    public Book(Integer id, String isbn, String title, String author, String sheets, String weight, BigDecimal cost) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.sheets = sheets;
        this.weight = weight;
        this.cost = cost;
    }
}
