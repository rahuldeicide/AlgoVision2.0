package com.algovision.search;

import com.algovision.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree keyed on Product price.
 * Part of MODULE 3: SEARCH ENGINE.
 *
 * Real-world mapping: Enables fast range queries -- e.g. "show all products
 * between Rs.500 and Rs.2000" -- which a plain HashMap cannot do efficiently
 * since it has no notion of order. This is exactly why catalogs/price engines
 * in e-commerce backends use ordered indexes (B-Trees in real DBs; BST here
 * as the DSA-level analogue).
 *
 * Complexity (average, balanced-ish input):
 *   insert        -> O(log n) average, O(n) worst case (skewed tree)
 *   searchByPrice -> O(log n) average, O(n) worst case
 *   rangeQuery    -> O(k + log n) where k = number of matching nodes
 *
 * Note: This is an unbalanced BST for clarity/teaching purposes. A
 * production system would use a self-balancing tree (AVL/Red-Black) to
 * guarantee O(log n); this tradeoff is intentionally called out in the
 * complexity report.
 */
public class ProductBST {

    private static class Node {
        Product product;
        Node left, right;

        Node(Product product) {
            this.product = product;
        }
    }

    private Node root;
    private int nodeCount = 0;

    public void insert(Product product) {
        root = insertRec(root, product);
        nodeCount++;
    }

    private Node insertRec(Node node, Product product) {
        if (node == null) {
            return new Node(product);
        }
        if (product.getPrice() < node.product.getPrice()) {
            node.left = insertRec(node.left, product);
        } else {
            node.right = insertRec(node.right, product);
        }
        return node;
    }

    public Product searchByPrice(double price) {
        Node current = root;
        while (current != null) {
            if (Double.compare(price, current.product.getPrice()) == 0) {
                return current.product;
            }
            current = price < current.product.getPrice() ? current.left : current.right;
        }
        return null;
    }

    /** Returns all products with price in [minPrice, maxPrice], inclusive, in ascending order. */
    public List<Product> rangeQuery(double minPrice, double maxPrice) {
        List<Product> result = new ArrayList<>();
        rangeQueryRec(root, minPrice, maxPrice, result);
        return result;
    }

    private void rangeQueryRec(Node node, double min, double max, List<Product> result) {
        if (node == null) return;
        if (node.product.getPrice() > min) {
            rangeQueryRec(node.left, min, max, result);
        }
        if (node.product.getPrice() >= min && node.product.getPrice() <= max) {
            result.add(node.product);
        }
        if (node.product.getPrice() < max) {
            rangeQueryRec(node.right, min, max, result);
        }
    }

    /** In-order traversal -> sorted product list by price. O(n). */
    public List<Product> inOrder() {
        List<Product> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node node, List<Product> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.product);
        inOrderRec(node.right, result);
    }

    public int height() {
        return heightRec(root);
    }

    private int heightRec(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRec(node.left), heightRec(node.right));
    }

    public int size() {
        return nodeCount;
    }
}
