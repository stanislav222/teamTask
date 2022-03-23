package com.intervale.statistics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookDto {
    @JsonIgnore
    private Integer id;
    @NotBlank(message = "isbn is mandatory")
    private String isbn;
    @NotEmpty(message = "title is mandatory")
    private String title;
    @NotBlank(message = "author is mandatory")
    private String author;
    @NotBlank(message = "sheets is mandatory")
    private String sheets;
    @NotBlank(message = "weight is mandatory")
    private String weight;
    @Positive
    private BigDecimal cost;

    /**
     *
     * @param isbn
     * @param title
     * @param author
     * @param sheets
     * @param weight
     * @param cost
     */
    public BookDto(String isbn, String title, String author, String sheets, String weight, BigDecimal cost) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.sheets = sheets;
        this.weight = weight;
        this.cost = cost;
    }
}
