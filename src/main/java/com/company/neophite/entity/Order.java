package com.company.neophite.entity;

import javax.persistence.*;

@Entity
@Table(name = "order")
public class Order {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    private String number;
    private boolean status;

    public Order(String number, boolean status) {
        this.number = number;
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNumber(String trackNumber) {
        this.number = trackNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", number='" + number + '\'' +
                '}';
    }
}
