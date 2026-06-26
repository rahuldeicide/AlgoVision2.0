package com.algovision.catalog;

import com.algovision.model.Product;

import java.util.Arrays;

/**
 * MODULE 2: CATALOGING
 * --------------------------------------------------------------
 * DSA Concepts: Arrays (contiguous storage), Linear Search, Binary Search,
 * Sorting (used to prep data for binary search and reporting).
 *
 * Real-world mapping: A product catalog needs fast bulk operations
 * (sort by price for display, filter by category) and predictable
 * memory layout for cache-friendly iteration -- arrays are the natural
 * fit, just as a real catalog/inventory table would be backed by
 * contiguous storage internally.
 *
 * Complexity:
 *   addProduct          -> O(1) amortized (array doubles when full)
 *   linearSearchByName  -> O(n)
 *   sortByPrice         -> O(n log n)   (Arrays.sort -> Dual-Pivot Quicksort for primitives,
 *                                        TimSort for objects)
 *   binarySearchByPrice -> O(log n)  (requires the array to be sorted by price first)
 */
public class CatalogEngine {

    private Product[] catalog;
    private int size;

    public CatalogEngine(int initialCapacity) {
        catalog = new Product[Math.max(initialCapacity, 4)];
        size = 0;
    }

    /** Adds a product to the catalog array, growing it (doubling) if full. */
    public void addProduct(Product product) {
        if (size == catalog.length) {
            catalog = Arrays.copyOf(catalog, catalog.length * 2);
        }
        catalog[size++] = product;
    }

    public int size() {
        return size;
    }

    public Product[] getAll() {
        return Arrays.copyOf(catalog, size);
    }

    /** O(n) linear search -- baseline before we introduce the BST in the Search Engine module. */
    public Product linearSearchByName(String name) {
        for (int i = 0; i < size; i++) {
            if (catalog[i].getName().equalsIgnoreCase(name)) {
                return catalog[i];
            }
        }
        return null;
    }

    /** Sorts the live catalog array in place by price, ascending. O(n log n). */
    public void sortByPrice() {
        Arrays.sort(catalog, 0, size, (a, b) -> Double.compare(a.getPrice(), b.getPrice()));
    }

    /**
     * O(log n) binary search by exact price match.
     * PRECONDITION: catalog must already be sorted by price (call sortByPrice() first).
     */
    public Product binarySearchByPrice(double targetPrice) {
        int lo = 0, hi = size - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            double midPrice = catalog[mid].getPrice();
            if (Double.compare(midPrice, targetPrice) == 0) {
                return catalog[mid];
            } else if (midPrice < targetPrice) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return null;
    }

    /** Filters products by category. O(n) -- arrays don't index by category, only the BST/heap will be faster for specific keys. */
    public Product[] filterByCategory(String category) {
        return Arrays.stream(catalog, 0, size)
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .toArray(Product[]::new);
    }
}
