# AlgoVision 2.0

DSA capstone project for my internship. The idea is to take the core data
structures and algorithms I've learned and actually put them to use in
something that looks like a real analytics system instead of solving them
as isolated leetcode-style problems. I built it around three domains that
get used a lot in industry talks - logistics, e-commerce, and fintech -
because most of the operations you need there map pretty cleanly onto
specific DSA topics.

## What it does

There are 5 modules, each living in its own package, and a main `App.java`
that ties them together so you can run them individually or all at once.

**1. Data Ingestion** (`ingestion/`) - simulates orders coming in from
customers. Uses a Queue so orders get processed in the order they arrive
(FIFO), and a HashMap on the side so I can still pull up any order by ID or
customer name instantly instead of scanning through everything.

**2. Cataloging** (`catalog/`) - basic product catalog backed by a plain
array. Has linear search, sorting, and binary search on top of it so you
can compare how much faster binary search is once the array is sorted.

**3. Search Engine** (`search/`) - this is where it gets more interesting.
A BST keyed by price so you can do range queries (e.g. "show me everything
between ₹3000-10000"), and a min-heap that always tells you the most urgent
order without having to look through the whole list.

**4. Optimizer** (`optimizer/`) - the part I'm most proud of. There's a
greedy algorithm that packs as many orders as possible into a delivery
batch under a budget, and a separate 0/1 knapsack DP solution that finds
the actual optimal batch for the same budget. Running both on the same
data side by side really shows why greedy isn't always "good enough" -
DP picks a different, better combination almost every time.

**5. Visualization** (`visualization/`) - models a small delivery network
(warehouse + a few cities) as a weighted graph, then runs Dijkstra to find
the shortest route from the warehouse to any city. Also renders the graph
and the route as plain text in the console since I didn't want to deal
with a GUI library for this.

## Why these specific DS choices

- Queue + Hash for ingestion because that's literally how it works in real
  systems - FIFO processing but O(1) lookup when support/another module
  needs a specific order.
- Arrays for the catalog because it's the simplest possible structure and
  a good baseline before introducing the BST.
- BST + Heap together because they solve different problems - BST is good
  when you care about order/range, heap is good when you only ever care
  about "what's most urgent right now."
- Greedy vs DP is the classic tradeoff example - greedy is faster (O(n log n))
  but doesn't guarantee the best outcome, DP is slower (O(n*budget)) but
  guarantees optimal. I wanted both in the same project so it's obvious why
  you'd pick one over the other.
- Graph + Dijkstra because routing/logistics problems are basically always
  shortest-path problems underneath.

## Known limitation

The BST isn't self-balancing (no AVL/Red-Black logic), so technically it
can degrade to O(n) in the worst case if products get inserted in already-
sorted order. Didn't implement balancing since it adds a lot of extra code
for something that wasn't the main point of this project, but it's a fair
thing to bring up if asked - I'd add it as a next step if I extended this.



## Complexity reference

| Module | Operation | Time | Space |
|---|---|---|---|
| Ingestion | enqueue/dequeue | O(1) | O(n) |
| Ingestion | hashmap lookup | O(1) avg | O(n) |
| Catalog | linear search | O(n) | O(1) |
| Catalog | sort | O(n log n) | O(n) |
| Catalog | binary search | O(log n) | O(1) |
| Search Engine | BST insert/search | O(log n) avg, O(n) worst | O(n) |
| Search Engine | heap insert/extract | O(log n) | O(n) |
| Optimizer | greedy batching | O(n log n) | O(n) |
| Optimizer | DP knapsack | O(n * budget) | O(n * budget) |
| Visualization | Dijkstra | O((V+E) log V) | O(V) |

Put this together for the mentor evaluation round - happy to extend it with
unit tests or a proper report doc if that's needed for submission too.
