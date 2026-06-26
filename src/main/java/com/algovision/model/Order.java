package com.algovision.model;

/**
 * Core data entity representing a customer order flowing through the
 * AlgoVision pipeline: Ingestion -> Cataloging -> Search -> Optimizer -> Visualization.
 */
public class Order {

    private final int orderId;
    private final String customerName;
    private final String productName;
    private final double price;
    private final int priority;     // 1 (highest) - 5 (lowest), used by the heap-based search engine
    private final int destinationNodeId; // maps to a node in the logistics graph

    public Order(int orderId, String customerName, String productName,
                 double price, int priority, int destinationNodeId) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.price = price;
        this.priority = priority;
        this.destinationNodeId = destinationNodeId;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getPriority() { return priority; }
    public int getDestinationNodeId() { return destinationNodeId; }

    @Override
    public String toString() {
        return String.format("Order#%-3d | %-10s | %-12s | Rs.%-8.2f | Priority:%d | -> Node %d",
                orderId, customerName, productName, price, priority, destinationNodeId);
    }
}
