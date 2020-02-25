package com.company.neophite.entity;

import javax.persistence.*;
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

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    @OneToMany
    @JoinTable(
            name = "usersOrders",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "orderId")
    )
    public List<Order> usersOrders = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsersOrder(Order order){
        this.usersOrders.add(order);
    }

    public List<Order> getUsersOrders() {
        return usersOrders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
