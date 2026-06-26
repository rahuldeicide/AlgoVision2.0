package com.algovision.ingestion;

import com.algovision.model.Order;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * MODULE 1: DATA INGESTION
 * --------------------------------------------------------------
 * DSA Concepts: Queue (FIFO order processing), Hashing (O(1) lookups)
 *
 * Real-world mapping: In e-commerce/logistics systems, orders arrive
 * continuously from multiple channels (web, app, API). They must be
 * processed in arrival order (fairness/FIFO -> Queue) while still being
 * instantly retrievable by ID or customer (HashMap) for support, fraud
 * checks, and downstream modules.
 *
 * Complexity:
 *   enqueueOrder      -> O(1)
 *   processNextOrder  -> O(1)
 * (HashMap is for direct lookups, not membership inference,
 * so amortized O(1) average case for get/put.)
 *   getOrderById      -> O(1) average
 *   getOrdersByCustomer -> O(1) average for bucket access, O(k) to return k orders
 */
public class DataIngestionEngine {

    // Queue -> preserves arrival order, models a real ingestion pipeline / buffer
    private final Queue<Order> incomingQueue = new LinkedList<>();

    // HashMap -> O(1) average lookup by orderId
    private final Map<Integer, Order> orderIndex = new HashMap<>();

    // HashMap of lists -> O(1) average bucket access by customer name
    private final Map<String, List<Order>> customerIndex = new HashMap<>();

    private final List<Order> processedLog = new LinkedList<>();

    /** Simulates an order arriving at the ingestion endpoint. */
    public void enqueueOrder(Order order) {
        incomingQueue.offer(order);
        orderIndex.put(order.getOrderId(), order);
        customerIndex.computeIfAbsent(order.getCustomerName(), k -> new LinkedList<>()).add(order);
    }

    /** Processes the earliest order that arrived (FIFO). Returns null if queue is empty. */
    public Order processNextOrder() {
        Order processed = incomingQueue.poll();
        if (processed != null) {
            processedLog.add(processed);
        }
        return processed;
    }

    public boolean hasPendingOrders() {
        return !incomingQueue.isEmpty();
    }

    public int pendingCount() {
        return incomingQueue.size();
    }

    /** O(1) average-case direct lookup -- the payoff of hashing vs. a linear scan. */
    public Order getOrderById(int orderId) {
        return orderIndex.get(orderId);
    }

    public List<Order> getOrdersByCustomer(String customerName) {
        return customerIndex.getOrDefault(customerName, new LinkedList<>());
    }

    public List<Order> getProcessedLog() {
        return processedLog;
    }

    public Map<Integer, Order> getAllIndexedOrders() {
        return orderIndex;
    }
}
