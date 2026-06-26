package com.algovision.app;

import com.algovision.catalog.CatalogEngine;
import com.algovision.ingestion.DataIngestionEngine;
import com.algovision.model.Order;
import com.algovision.model.Product;
import com.algovision.optimizer.BudgetOptimizer;
import com.algovision.optimizer.DeliveryBatchOptimizer;
import com.algovision.search.PriorityOrderEngine;
import com.algovision.search.ProductBST;
import com.algovision.visualization.ConsoleVisualizer;
import com.algovision.visualization.LogisticsGraph;

import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class App {

    private static final DataIngestionEngine ingestionEngine = new DataIngestionEngine();
    private static final CatalogEngine catalogEngine = new CatalogEngine(10);
    private static final ProductBST productBST = new ProductBST();
    private static final PriorityOrderEngine priorityEngine = new PriorityOrderEngine();
    private static final DeliveryBatchOptimizer greedyOptimizer = new DeliveryBatchOptimizer();
    private static final BudgetOptimizer dpOptimizer = new BudgetOptimizer();
    private static final LogisticsGraph logisticsGraph = new LogisticsGraph();

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        seedDemoData();
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": demoIngestion(); break;
                case "2": demoCataloging(); break;
                case "3": demoSearchEngine(); break;
                case "4": demoOptimizer(); break;
                case "5": demoVisualization(); break;
                case "6": runFullPipelineDemo(); break;
                case "0": running = false; break;
                default: System.out.println("Invalid option, please try again.");
            }
        }
        System.out.println("\nExiting AlgoVision 2.0. Thank you!");
    }


    private static void seedDemoData() {

        Product[] products = {
                new Product(1, "Laptop", "Electronics", 55000, 12),
                new Product(2, "Phone", "Electronics", 25000, 30),
                new Product(3, "Desk", "Furniture", 6000, 8),
                new Product(4, "Chair", "Furniture", 3500, 20),
                new Product(5, "Headset", "Electronics", 2500, 40),
                new Product(6, "Monitor", "Electronics", 9000, 15),
                new Product(7, "Bookshelf", "Furniture", 4500, 10)
        };
        for (Product p : products) {
            catalogEngine.addProduct(p);
            productBST.insert(p);
        }


        Order[] orders = {
                new Order(101, "Aman", "Phone", 25000, 2, 3),
                new Order(102, "Riya", "Headset", 2500, 4, 2),
                new Order(103, "Karan", "Laptop", 55000, 1, 4),
                new Order(104, "Sneha", "Chair", 3500, 5, 1),
                new Order(105, "Vikram", "Monitor", 9000, 3, 5),
                new Order(106, "Divya", "Desk", 6000, 2, 2),
                new Order(107, "Imran", "Bookshelf", 4500, 4, 3)
        };
        for (Order o : orders) {
            ingestionEngine.enqueueOrder(o);
            priorityEngine.insertOrder(o);
        }


        logisticsGraph.addNode(0, "Warehouse");
        logisticsGraph.addNode(1, "CityA");
        logisticsGraph.addNode(2, "CityB");
        logisticsGraph.addNode(3, "CityC");
        logisticsGraph.addNode(4, "CityD");
        logisticsGraph.addNode(5, "CityE");

        logisticsGraph.addEdge(0, 1, 4);
        logisticsGraph.addEdge(0, 2, 2);
        logisticsGraph.addEdge(1, 2, 1);
        logisticsGraph.addEdge(1, 3, 5);
        logisticsGraph.addEdge(2, 3, 8);
        logisticsGraph.addEdge(2, 4, 10);
        logisticsGraph.addEdge(3, 4, 2);
        logisticsGraph.addEdge(3, 5, 6);
        logisticsGraph.addEdge(4, 5, 3);
    }

    private static void printBanner() {
        System.out.println("============================================================");
        System.out.println("        ALGOVISION 2.0 -- UNIFIED ANALYTICS PLATFORM");
        System.out.println("   Logistics | E-Commerce | Fintech  --  DSA Capstone Demo");
        System.out.println("============================================================");
    }

    private static void printMenu() {
        System.out.println("\n--------------- MAIN MENU ---------------");
        System.out.println("1. Data Ingestion Module      (Queue + Hashing)");
        System.out.println("2. Cataloging Module          (Arrays)");
        System.out.println("3. Search Engine Module       (BST + Heap)");
        System.out.println("4. Optimizer Module           (Greedy + DP)");
        System.out.println("5. Visualization Module       (Graphs)");
        System.out.println("6. Run Full Pipeline Demo     (All modules together)");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    // ---------------------------------------------------------------
    // Module 1: Data Ingestion
    // ---------------------------------------------------------------
    private static void demoIngestion() {
        System.out.println("\n=== MODULE 1: DATA INGESTION (Queue + Hashing) ===");
        System.out.println("Pending orders in queue: " + ingestionEngine.pendingCount());

        System.out.println("\nProcessing orders FIFO from the queue:");
        while (ingestionEngine.hasPendingOrders()) {
            Order processed = ingestionEngine.processNextOrder();
            System.out.println("  Processed -> " + processed);
        }

        System.out.println("\nO(1) average hash lookup demo -- fetching Order#103 directly:");
        Order found = ingestionEngine.getOrderById(103);
        System.out.println("  " + (found != null ? found : "Not found"));

        System.out.println("\nLookup all orders by customer 'Riya' (hash bucket access):");
        List<Order> riyaOrders = ingestionEngine.getOrdersByCustomer("Riya");
        riyaOrders.forEach(o -> System.out.println("  " + o));
    }

    // ---------------------------------------------------------------
    // Module 2: Cataloging
    // ---------------------------------------------------------------
    private static void demoCataloging() {
        System.out.println("\n=== MODULE 2: CATALOGING (Arrays) ===");
        System.out.println("Total catalog size: " + catalogEngine.size());

        System.out.println("\nLinear search O(n) for 'Monitor':");
        System.out.println("  " + catalogEngine.linearSearchByName("Monitor"));

        catalogEngine.sortByPrice();
        System.out.println("\nCatalog sorted by price (O(n log n)):");
        for (Product p : catalogEngine.getAll()) System.out.println("  " + p);

        System.out.println("\nBinary search O(log n) for price 9000.0 (post-sort):");
        System.out.println("  " + catalogEngine.binarySearchByPrice(9000.0));

        System.out.println("\nFilter by category 'Furniture':");
        for (Product p : catalogEngine.filterByCategory("Furniture")) System.out.println("  " + p);

        ConsoleVisualizer.renderPriceBarChart(catalogEngine.getAll());
    }

    // ---------------------------------------------------------------
    // Module 3: Search Engine
    // ---------------------------------------------------------------
    private static void demoSearchEngine() {
        System.out.println("\n=== MODULE 3: SEARCH ENGINE (BST + Heap) ===");

        System.out.println("BST search O(log n avg) for price 6000.0:");
        System.out.println("  " + productBST.searchByPrice(6000.0));

        System.out.println("\nBST range query: products priced between 3000 and 10000:");
        for (Product p : productBST.rangeQuery(3000, 10000)) System.out.println("  " + p);

        ConsoleVisualizer.renderBSTStructure(productBST.inOrder());
        System.out.println("BST height: " + productBST.height() + " (for " + productBST.size() + " nodes)");

        System.out.println("\nTop 3 most urgent orders via Min-Heap (O(k log n)):");
        for (Order o : priorityEngine.getTopKUrgent(3)) System.out.println("  " + o);
    }

    // ---------------------------------------------------------------
    // Module 4: Optimizer
    // ---------------------------------------------------------------
    private static void demoOptimizer() {
        System.out.println("\n=== MODULE 4: OPTIMIZER (Greedy + Dynamic Programming) ===");

        List<Order> allOrders = List.of(
                ingestionEngine.getOrderById(101), ingestionEngine.getOrderById(102),
                ingestionEngine.getOrderById(103), ingestionEngine.getOrderById(104),
                ingestionEngine.getOrderById(105), ingestionEngine.getOrderById(106),
                ingestionEngine.getOrderById(107)
        );

        double capacity = 15000;
        System.out.println("Greedy batch (vehicle capacity = Rs." + capacity + "):");
        List<Order> greedyBatch = greedyOptimizer.batchByGreedyCapacity(allOrders, capacity);
        greedyBatch.forEach(o -> System.out.println("  " + o));
        System.out.printf("  Orders fulfilled: %d | Total cost: Rs.%.2f%n",
                greedyBatch.size(), greedyOptimizer.totalCost(greedyBatch));

        int budget = 15000;
        System.out.println("\nDP (0/1 Knapsack) optimal batch for SAME budget = Rs." + budget + ":");
        List<Order> dpBatch = dpOptimizer.optimizeForBudget(allOrders, budget);
        dpBatch.forEach(o -> System.out.println("  " + o));
        System.out.println("  Optimal urgency-value achieved: " + dpOptimizer.maxAchievableValue(allOrders, budget));

        System.out.println("\n>> Tradeoff: Greedy is faster (O(n log n)) and fits MORE orders by count,");
        System.out.println(">> DP is slower (O(n*W)) but guarantees the OPTIMAL value-for-budget selection.");
    }

    // ---------------------------------------------------------------
    // Module 5: Visualization
    // ---------------------------------------------------------------
    private static void demoVisualization() {
        System.out.println("\n=== MODULE 5: VISUALIZATION (Graphs) ===");
        logisticsGraph.renderAdjacency();

        System.out.println("\nDijkstra shortest distances from Warehouse (Node 0):");
        Map<Integer, Integer> distances = logisticsGraph.dijkstra(0);
        for (int id : logisticsGraph.nodeIds()) {
            System.out.printf("  %-10s : %d%n", logisticsGraph.nameOf(id), distances.get(id));
        }

        System.out.println("\nShortest path Warehouse -> CityE:");
        List<Integer> path = logisticsGraph.shortestPath(0, 5);
        System.out.println("  " + logisticsGraph.renderPath(path));

        System.out.println("\nBFS traversal from Warehouse (network reachability check):");
        List<Integer> bfsOrder = logisticsGraph.bfsTraversal(0);
        bfsOrder.forEach(id -> System.out.print(logisticsGraph.nameOf(id) + "  "));
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Unified pipeline demo -- runs all 5 modules on the same dataset
    // ---------------------------------------------------------------
    private static void runFullPipelineDemo() {
        System.out.println("\n############################################################");
        System.out.println("   FULL PIPELINE DEMO: Ingestion -> Catalog -> Search ->");
        System.out.println("                        Optimizer -> Visualization");
        System.out.println("############################################################");
        demoIngestion();
        demoCataloging();
        demoSearchEngine();
        demoOptimizer();
        demoVisualization();
        System.out.println("\n>> Full pipeline complete: orders ingested, catalog indexed,");
        System.out.println(">> urgent orders surfaced, optimal batch computed, and the");
        System.out.println(">> delivery network visualized end-to-end.");
    }
}
