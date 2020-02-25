package com.company.neophite.repos;

import com.company.neophite.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Order findOrderByNumber(String number);
}
