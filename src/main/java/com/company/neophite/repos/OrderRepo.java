package com.company.neophite.repos;

import com.company.neophite.entity.Order;
import com.company.neophite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Order findOrderByNumber(String number);
    void deleteById(Long id);
}
