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

    @Column(name = "first_name")
    private String name;

    @Column(name = "second_name")
    private String surnmae;

    public User() {
    }

    public User(String username, String name, String surnmae) {
        this.username = username;
        this.name = name;
        this.surnmae = surnmae;
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

    public void setUsersOrder(Order order) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurnmae() {
        return surnmae;
    }

    public void setSurnmae(String surnmae) {
        this.surnmae = surnmae;
    }
}
