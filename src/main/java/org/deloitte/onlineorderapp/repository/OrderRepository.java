package org.deloitte.onlineorderapp.repository;


import org.deloitte.onlineorderapp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}