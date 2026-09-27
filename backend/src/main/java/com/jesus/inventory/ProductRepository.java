package com.jesus.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<Product> findAll(){
        String sql = """
                 SELECT id, name, price, stock
                FROM products
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, (row, rowNumber) -> new Product(
                row.getLong("id"),
                row.getString("name"),
                row.getBigDecimal("price"),
                row.getInt("stock")
        ));
    }
    public int updateStock(long id, int stock){
        String sql = "UPDATE products SET stock = ? WHERE id = ?";

        return jdbcTemplate.update(sql, stock, id);
    }

    public boolean reduceStock(long productId, Integer quantity){
        if (quantity <= 0){
            throw new IllegalArgumentException("Quantity must be positive");
        }

        String sql = """
                UPDATE products
                SET stock = stock - ?
                WHERE id = ? AND stock >= ?
                 """;
        int updatedRows = jdbcTemplate.update(
                sql,
                quantity,
                productId,
                quantity
        );
        return updatedRows == 1;
    }

    public Optional<Product> findById(long productId){
        String sql = """
                SELECT id, name, price, stock
                FROM products
                WHERE id = ?
                """;

        List<Product> products = jdbcTemplate.query(
                sql,
                (row, rowNumber) -> new Product(
                        row.getLong("id"),
                        row.getString("name"),
                        row.getBigDecimal("price"),
                        row.getInt("stock")
                ),
                productId
        );
        return products.stream().findFirst();
    }

}
