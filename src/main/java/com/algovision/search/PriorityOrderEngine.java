package com.algovision.search;

import com.algovision.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Min-Heap over Order.priority (1 = most urgent).
 * Part of MODULE 3: SEARCH ENGINE.
 *
 * Real-world mapping: Logistics/fintech systems must always serve the
 * most urgent item next (express delivery, fraud-flagged transaction,
 * high-priority support ticket) without re-scanning the entire order
 * list every time -- exactly the job a heap is built for.
 *
 * Complexity:
 *   insertOrder    -> O(log n)
 *   peekMostUrgent -> O(1)
 *   extractMostUrgent -> O(log n)
 *   getTopKUrgent  -> O(k log n)
 */
public class PriorityOrderEngine {

    // Java's PriorityQueue is a binary min-heap by default.
    private final PriorityQueue<Order> minHeap =
            new PriorityQueue<>((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));

    public void insertOrder(Order order) {
        minHeap.offer(order);
    }

    public Order peekMostUrgent() {
        return minHeap.peek();
    }

    public Order extractMostUrgent() {
        return minHeap.poll();
    }

    public boolean isEmpty() {
        return minHeap.isEmpty();
    }

    public int size() {
        return minHeap.size();
    }

    /**
     * Returns the top-K most urgent orders WITHOUT mutating the underlying heap,
     * by heapifying a temporary copy. O(k log n).
     */
    public List<Order> getTopKUrgent(int k) {
        PriorityQueue<Order> copy = new PriorityQueue<>(minHeap);
        List<Order> result = new ArrayList<>();
        int count = 0;
        while (!copy.isEmpty() && count < k) {
            result.add(copy.poll());
            count++;
        }
        return result;
    }
}
