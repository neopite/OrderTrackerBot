package com.company.neophite.entity;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    private String username;

    @Column(name = "first_name")
    private String name;

    @Column(name = "second_name")
    private String surname;

    public User() {
    }

    public User(String username, String name, String surnmae) {
        this.username = username;
        this.name = name;
        this.surname = surnmae;
    }

    @OneToMany(fetch = FetchType.EAGER  , targetEntity = Order.class , cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
