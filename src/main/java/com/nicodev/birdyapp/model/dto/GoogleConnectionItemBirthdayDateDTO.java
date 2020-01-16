package com.nicodev.birdyapp.model.dto;

public class GoogleConnectionItemBirthdayDateDTO {

    private int month;
    private int day;
    private int year;

    public GoogleConnectionItemBirthdayDateDTO() {
    }

    public GoogleConnectionItemBirthdayDateDTO(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }
}
