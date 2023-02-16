package com.trade.file.dto;

import com.opencsv.bean.CsvBindByPosition;

public class TradeWithProduct {
    @CsvBindByPosition(position = 0)
    private String date;
    @CsvBindByPosition(position = 1)
    private String product_name;
    @CsvBindByPosition(position = 2)
    private String currency;
    @CsvBindByPosition(position = 3)
    private String price;

    public TradeWithProduct(String date, String product_name, String currency, String price) {
        this.date = date;
        this.product_name = product_name;
        this.currency = currency;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "TradeWithProduct{" +
                "date='" + date + '\'' +
                ", product_name='" + product_name + '\'' +
                ", currency='" + currency + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
