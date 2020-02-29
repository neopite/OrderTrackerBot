package com.company.neophite.entity;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "delevered")
    private boolean delevered;

    public Order(String number, boolean status) {
        this.number = number;
        this.delevered = status;
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }


    public void setNumber(String trackNumber) {
        this.number = trackNumber;
    }

    public boolean isDelevered() {
        return delevered;
    }

    public void setDelevered(boolean delevered) {
        this.delevered = delevered;
    }


    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", number='" + number + '\'' +
                '}';
    }
}
