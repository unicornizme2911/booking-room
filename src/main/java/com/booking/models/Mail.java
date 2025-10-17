package com.booking.models;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Builder
public class Mail {
    private String to;
    private String subject;
    private String content;
    private int code;
    private Date date;
    private float price;
    private String address;
    private String product;
}