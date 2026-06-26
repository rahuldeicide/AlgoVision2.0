package com.algovision.model;

/**
 * Represents a catalog item. Used by the Cataloging module (array storage)
 * and the Search Engine module (BST keyed on price/productId).
 */
public class Product {

    private final int productId;
    private final String name;
    private final String category;
    private final double price;
    private final int stock;

    public Product(int productId, String name, String category, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    @Override
    public String toString() {
        return String.format("Product#%-3d | %-12s | %-11s | Rs.%-8.2f | Stock:%d",
                productId, name, category, price, stock);
    }
}
