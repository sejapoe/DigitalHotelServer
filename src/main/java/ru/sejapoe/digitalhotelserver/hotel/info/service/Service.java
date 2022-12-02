package ru.sejapoe.digitalhotelserver.hotel.info.service;

public class Service {
    private final String name;
    private final String description;
    private final double price;

    public Service(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}

