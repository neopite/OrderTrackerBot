package com.company.neophite.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private int id;
    private String username;

    @Column(name = "first_name")
    private String name;

    @Column(name = "second_name")
    private String surname;

    public User() {
    }

    public User(int id,String username, String name, String surname) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
    }

    @OneToMany(fetch = FetchType.EAGER  , targetEntity = Order.class , cascade = {
            CascadeType.REMOVE,
    })
    @JoinTable(name = "user_orders" ,
            joinColumns = @JoinColumn(name = "user_id") ,
            inverseJoinColumns = @JoinColumn (name = "order_id")
    )
    Set<Order> orders ;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Order order) {
        this.orders.add(order);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surnmae) {
        this.surname = surnmae;
    }
}
