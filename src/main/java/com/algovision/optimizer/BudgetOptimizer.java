package com.algovision.optimizer;

import com.algovision.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 4 (Part B): OPTIMIZER -- DYNAMIC PROGRAMMING
 * --------------------------------------------------------------
 * DSA Concept: 0/1 Knapsack (Dynamic Programming).
 *
 * Real-world mapping: Given a fixed delivery/cargo budget, pick the subset
 * of orders that maximizes total VALUE (e.g. customer priority converted
 * to a value score, or order value itself) without exceeding the budget.
 * Unlike the Greedy module, this guarantees the mathematically OPTIMAL
 * subset -- directly demonstrating the classic Greedy vs DP tradeoff
 * (fast heuristic vs. guaranteed-optimal but costlier).
 *
 * We treat order price as both "weight" (cost) and use a value derived
 * from (6 - priority) i.e. higher urgency (priority 1) -> higher value,
 * modeling "fit in the highest-value, most urgent orders within budget."
 *
 * Complexity:
 *   Time  -> O(n * W)  where n = number of orders, W = budget (scaled to int)
 *   Space -> O(n * W) for the DP table (kept for backtracking the chosen items)
 *
 * This is pseudo-polynomial -- efficient for reasonably bounded budgets,
 * but it's why real-world systems cap or discretize the budget/weight axis.
 */
public class BudgetOptimizer {

    /**
     * Solves 0/1 knapsack where weight = order price (rounded to nearest int unit)
     * and value = urgency-derived score. Returns the optimal subset of orders.
     */
    public List<Order> optimizeForBudget(List<Order> orders, int budget) {
        int n = orders.size();
        int[] weight = new int[n];
        int[] value = new int[n];

        for (int i = 0; i < n; i++) {
            weight[i] = (int) Math.round(orders.get(i).getPrice());
            value[i] = 6 - orders.get(i).getPriority(); // priority 1 -> value 5 (most urgent/valuable)
        }

        int[][] dp = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= budget; w++) {
                if (weight[i - 1] <= w) {
                    dp[i][w] = Math.max(
                            dp[i - 1][w],                                  // skip this order
                            value[i - 1] + dp[i - 1][w - weight[i - 1]]    // take this order
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Backtrack to find which orders were chosen
        List<Order> chosen = new ArrayList<>();
        int w = budget;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                chosen.add(orders.get(i - 1));
                w -= weight[i - 1];
            }
        }

        return chosen;
    }

    public int maxAchievableValue(List<Order> orders, int budget) {
        List<Order> chosen = optimizeForBudget(orders, budget);
        int total = 0;
        for (Order o : chosen) {
            total += (6 - o.getPriority());
        }
        return total;
    }
}
