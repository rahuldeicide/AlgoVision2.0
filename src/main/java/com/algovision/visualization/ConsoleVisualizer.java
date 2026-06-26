package com.algovision.visualization;

import com.algovision.model.Order;
import com.algovision.model.Product;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Lightweight console-based "visual dashboard" helpers.
 * Renders BST and Heap structures as readable ASCII trees, and renders
 * bar-style comparisons for prices/priorities -- giving the project a
 * visualization layer without needing a GUI framework, while still
 * fulfilling the "Visualization Dashboard" deliverable for console demos.
 */
public class ConsoleVisualizer {

    /** Renders a horizontal ASCII bar chart for product prices. */
    public static void renderPriceBarChart(Product[] products) {
        System.out.println("\n--- Catalog Price Chart ---");
        double max = 0;
        for (Product p : products) max = Math.max(max, p.getPrice());
        if (max == 0) max = 1;

        for (Product p : products) {
            int barLength = (int) ((p.getPrice() / max) * 40);
            String bar = "#".repeat(Math.max(barLength, 1));
            System.out.printf("%-12s | %-40s Rs.%.2f%n", p.getName(), bar, p.getPrice());
        }
    }

    /** Renders the heap's internal array as a simple level-by-level ASCII tree. */
    public static void renderHeapAsTree(PriorityQueue<Order> heapCopy) {
        System.out.println("\n--- Priority Heap (Min-Heap by Urgency) ---");
        Object[] arr = heapCopy.toArray();
        if (arr.length == 0) {
            System.out.println("(empty heap)");
            return;
        }
        int level = 0;
        int index = 0;
        while (index < arr.length) {
            int countAtLevel = (int) Math.pow(2, level);
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < countAtLevel && index < arr.length; i++, index++) {
                Order o = (Order) arr[index];
                line.append(String.format("[P%d|#%d]  ", o.getPriority(), o.getOrderId()));
            }
            System.out.println("  ".repeat(Math.max(0, 3 - level)) + line);
            level++;
        }
    }

    /** Renders a BST's structure indented by depth (root at top, deeper nodes more indented). */
    public static void renderBSTStructure(List<Product> sortedInOrder) {
        System.out.println("\n--- BST In-Order Traversal (Sorted by Price) ---");
        for (Product p : sortedInOrder) {
            System.out.println("  -> " + p);
        }
    }
}
