package com.jesus.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.time.OffsetDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public long create(long productId, int quantity, BigDecimal unitPrice){
        String sql = """
                INSERT INTO orders (product_id, quantity, unit_price)
                VALUES (?, ?, ?)
                RETURNING id
                """;
        Long orderId = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                productId,
                quantity,
                unitPrice
        );

        if(orderId == null){
            throw new IllegalStateException("Database did not return an order ID.");
        }
        return orderId;
    }

    public List<Order> findAll(){
        String sql = """
               SELECT o.id,
               o.product_id,
               p.name AS product_name,
               o.quantity,
               o.unit_price,
               o.status,
               o.created_at
              FROM orders o
              JOIN products p ON p.id = o.product_id
              ORDER BY o.created_at DESC, o.id DESC
              """;

        return jdbcTemplate.query(sql, (row, rowNumber) -> new Order(
                row.getLong("id"),
                row.getLong("product_id"),
                row.getString("product_name"),
                row.getInt("quantity"),
                row.getBigDecimal("unit_price"),
                row.getString("status"),
                row.getObject("created_at", OffsetDateTime.class)
        ));
    }

}
