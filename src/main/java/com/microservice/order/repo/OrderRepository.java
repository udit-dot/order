package com.microservice.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
