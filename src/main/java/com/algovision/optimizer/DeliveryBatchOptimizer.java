package com.algovision.optimizer;

import com.algovision.model.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MODULE 4 (Part A): OPTIMIZER -- GREEDY ALGORITHMS
 * --------------------------------------------------------------
 * DSA Concept: Greedy strategy (Activity Selection variant).
 *
 * Real-world mapping: A delivery vehicle has a fixed capacity (e.g. weight
 * or number of slots) for one trip. We want to greedily pack as MANY
 * orders as possible into a batch by always picking the next-cheapest
 * (lowest-cost) order that still fits -- a classic fractional-knapsack-style
 * greedy heuristic used in real logistics batching/route planning before
 * a full optimization pass.
 *
 * Complexity:
 *   sort        -> O(n log n)
 *   greedy pass -> O(n)
 *   Total       -> O(n log n)
 *
 * Note: Greedy gives a fast, good-enough batch (maximize count of fulfilled
 * orders under budget) but does NOT guarantee the optimal total value --
 * that is exactly why the DP-based BudgetOptimizer exists alongside it,
 * to show the classic greedy-vs-DP tradeoff (speed vs. optimality).
 */
public class DeliveryBatchOptimizer {

    /**
     * Greedily selects the maximum number of orders whose combined price
     * fits within the given budget/capacity, by always taking the cheapest
     * remaining order first.
     */
    public List<Order> batchByGreedyCapacity(List<Order> orders, double capacity) {
        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort(Comparator.comparingDouble(Order::getPrice)); // O(n log n)

        List<Order> batch = new ArrayList<>();
        double remaining = capacity;

        for (Order order : sorted) { // O(n)
            if (order.getPrice() <= remaining) {
                batch.add(order);
                remaining -= order.getPrice();
            }
        }
        return batch;
    }

    public double totalCost(List<Order> batch) {
        return batch.stream().mapToDouble(Order::getPrice).sum();
    }
}
