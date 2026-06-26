# AlgoVision 2.0 — Unified Analytics Platform

**DSA Capstone Major Project**
Integrates core Data Structures & Algorithms into one console-based analytics
platform spanning logistics, e-commerce, and fintech use cases.

---

## 1. Module-to-DSA Mapping

| # | Module | DSA Concepts | Java Package | Real-World Mapping |
|---|--------|---------------|---------------|----------------------|
| 1 | Data Ingestion | Queue, Hashing | `com.algovision.ingestion` | Orders arrive continuously; processed FIFO (Queue) while indexed for instant lookup (HashMap) |
| 2 | Cataloging | Arrays, Linear/Binary Search, Sorting | `com.algovision.catalog` | Product catalog stored contiguously; supports fast bulk sort + search |
| 3 | Search Engine | BST, Min-Heap | `com.algovision.search` | BST enables price range queries; Heap surfaces most urgent orders instantly |
| 4 | Optimizer | Greedy, Dynamic Programming (0/1 Knapsack) | `com.algovision.optimizer` | Greedy batches max order count under capacity; DP finds the provably optimal value-for-budget batch |
| 5 | Visualization | Weighted Graph, Dijkstra, BFS | `com.algovision.visualization` | Models a warehouse-to-city delivery network; computes shortest routes and renders them in the console |

`com.algovision.app.App` is the single entry point that wires all five
modules together against one shared dataset (Option 6 in the menu runs the
full pipeline end-to-end).

---

## 2. Time & Space Complexity Summary

| Operation | Complexity | Notes |
|---|---|---|
| Queue enqueue/dequeue | O(1) | LinkedList-backed `Queue` |
| HashMap get/put | O(1) average | O(n) worst case (hash collisions) |
| Array insert (amortized) | O(1) | Doubles capacity when full |
| Linear search | O(n) | Baseline before BST/hash lookups |
| `Arrays.sort` | O(n log n) | TimSort for objects |
| Binary search | O(log n) | Requires pre-sorted array |
| BST insert/search | O(log n) average, O(n) worst | Unbalanced BST — worst case on skewed insertion order |
| BST range query | O(k + log n) | k = number of matching results |
| Heap insert/extract | O(log n) | Java `PriorityQueue` (binary heap) |
| Top-K from heap | O(k log n) | |
| Greedy batching | O(n log n) | Sort once, single pass |
| 0/1 Knapsack (DP) | O(n × W) time, O(n × W) space | Pseudo-polynomial; W = budget |
| Dijkstra (graph) | O((V + E) log V) | Min-heap based |
| BFS | O(V + E) | |

**Key tradeoff to highlight in your report/demo:** Greedy is faster and
simpler but only *near-optimal*; DP is slower but *provably optimal* for the
knapsack-style budget allocation problem. This is intentionally demonstrated
side-by-side in Module 4 on the *same* dataset and budget so the difference
in output is visible directly.

**Known design tradeoff:** The BST is intentionally left unbalanced (no
AVL/Red-Black self-balancing) to keep the implementation teachable at
capstone level. This is worth mentioning in your report as a "future
improvement" — it shows the mentor you understand the limitation, not just
the happy path.

---

## 3. How to Build & Run

### Option A — IntelliJ IDEA (recommended, since you're using it)
1. Unzip the project folder anywhere on your machine.
2. Open IntelliJ → `File` → `Open` → select the **AlgoVision2.0** folder (the
   one containing `pom.xml`).
3. IntelliJ will detect it as a Maven project and prompt to load it — click
   **Load Maven Project** (or it may auto-import).
4. Wait for indexing to finish (progress bar at bottom-right).
5. Open `src/main/java/com/algovision/app/App.java`.
6. Click the green ▶ Run arrow next to `public static void main(...)`.
7. The console panel at the bottom will show the interactive menu — type a
   number (1–6) and press Enter to explore each module, or `0` to exit.

If IntelliJ doesn't auto-detect Maven, right-click `pom.xml` → **Add as Maven
Project**.

### Option B — Plain javac/java (no Maven, no IDE needed)
From the project root folder (the one containing `src/`):
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.algovision.app.App
```

### Option C — Maven CLI
```bash
mvn clean package
java -jar target/AlgoVision2.0.jar
```

---

## 4. Suggested Demo Flow (for mentor evaluation)

1. Run option **6 (Full Pipeline Demo)** first — shows all 5 modules working
   on one shared dataset, proving "unified platform" integration.
2. Then revisit individual modules (1–5) to pause and explain the complexity
   of each operation as it prints.
3. For Module 4, explicitly point out that Greedy and DP are run on the
   **same** order list and **same** budget, and explain why their outputs
   differ — this is usually the most impressive 30 seconds of the demo.
4. For Module 5, explain that Dijkstra is exactly what real route-planning
   engines (delivery apps, maps) use, just at a much larger scale.

---

## 5. Possible Extensions (mention in "Reflection" section of your report)

- Replace the unbalanced BST with a self-balancing AVL or Red-Black tree.
- Persist data via a file or embedded DB instead of in-memory seed data.
- Swap the console visualization for a JavaFX or web dashboard.
- Add unit tests (JUnit) per module for the "Code Quality" criterion.
