// ===================================================================
// AlgoVision 2.0 — browser port of the Java console app.
// Same data, same algorithms, just running client-side in JS instead
// of on the JVM so it's viewable with zero setup.
// ===================================================================

const ORDERS = [
  {id:101, customer:'Aman',   product:'Phone',     price:25000, priority:2, dest:3},
  {id:102, customer:'Riya',   product:'Headset',   price:2500,  priority:4, dest:2},
  {id:103, customer:'Karan',  product:'Laptop',    price:55000, priority:1, dest:4},
  {id:104, customer:'Sneha',  product:'Chair',     price:3500,  priority:5, dest:1},
  {id:105, customer:'Vikram', product:'Monitor',   price:9000,  priority:3, dest:5},
  {id:106, customer:'Divya',  product:'Desk',      price:6000,  priority:2, dest:2},
  {id:107, customer:'Imran',  product:'Bookshelf', price:4500,  priority:4, dest:3},
];

const PRODUCTS = [
  {id:1, name:'Laptop',    category:'Electronics', price:55000, stock:12},
  {id:2, name:'Phone',     category:'Electronics', price:25000, stock:30},
  {id:3, name:'Desk',      category:'Furniture',   price:6000,  stock:8},
  {id:4, name:'Chair',     category:'Furniture',   price:3500,  stock:20},
  {id:5, name:'Headset',   category:'Electronics', price:2500,  stock:40},
  {id:6, name:'Monitor',   category:'Electronics', price:9000,  stock:15},
  {id:7, name:'Bookshelf', category:'Furniture',   price:4500,  stock:10},
];

function fmtRs(n){ return '\u20B9' + Math.round(n).toLocaleString('en-IN'); }

// -------------------------------------------------------------------
// MODULE 1 — Ingestion (Queue + Hash)
// -------------------------------------------------------------------
const ingestion = (() => {
  let queue = [...ORDERS];
  let processedCount = 0;
  const hashIndex = new Map(ORDERS.map(o => [o.id, o]));

  function render(){
    const cellsEl = document.getElementById('queue-cells');
    cellsEl.innerHTML = '';
    queue.forEach((o, i) => {
      const c = document.createElement('div');
      c.className = 'cell' + (i === 0 ? ' active' : '');
      c.textContent = '#' + o.id + ' ' + o.product;
      cellsEl.appendChild(c);
    });
    if(queue.length === 0){
      cellsEl.innerHTML = '<div class="cell done">queue empty</div>';
    }
    document.getElementById('queue-pending').textContent = queue.length;
    document.getElementById('queue-processed').textContent = processedCount;
  }

  function processNext(){
    if(queue.length === 0) return;
    queue.shift();
    processedCount++;
    render();
  }

  function reset(){
    queue = [...ORDERS];
    processedCount = 0;
    render();
  }

  function lookup(){
    const id = parseInt(document.getElementById('lookup-id').value, 10);
    const log = document.getElementById('lookup-log');
    const found = hashIndex.get(id); // O(1) average lookup, regardless of queue state
    if(found){
      log.innerHTML =
        '<span class="ok">hash hit</span> — O(1) average, no scanning needed\n' +
        '#' + found.id + '  ' + found.customer + '  ' + found.product +
        '  ' + fmtRs(found.price) + '  priority:' + found.priority;
    } else {
      log.innerHTML = '<span class="hl">no order with id ' + id + '</span> (try 101-107)';
    }
  }

  render();
  return { processNext, reset, lookup };
})();

// -------------------------------------------------------------------
// MODULE 2 — Catalog (Array, linear search, sort, binary search)
// -------------------------------------------------------------------
const catalogDemo = (() => {
  let arr = [...PRODUCTS];

  function render(){
    const el = document.getElementById('catalog-cells');
    el.innerHTML = '';
    arr.forEach(p => {
      const c = document.createElement('div');
      c.className = 'cell';
      c.id = 'prod-' + p.id;
      c.textContent = p.name + ' ' + fmtRs(p.price);
      el.appendChild(c);
    });
  }

  async function linearSearch(){
    const target = document.getElementById('linear-target').value.trim().toLowerCase();
    const log = document.getElementById('linear-log');
    let steps = 0;
    let found = null;
    document.querySelectorAll('#catalog-cells .cell').forEach(c => c.classList.remove('active','done'));
    for(let i = 0; i < arr.length; i++){
      steps++;
      const cellEl = document.getElementById('prod-' + arr[i].id);
      if(cellEl) cellEl.classList.add('active');
      await new Promise(r => setTimeout(r, 160));
      if(cellEl) cellEl.classList.remove('active');
      if(arr[i].name.toLowerCase() === target){
        found = arr[i];
        if(cellEl) cellEl.classList.add('done');
        break;
      }
    }
    log.innerHTML = found
      ? '<span class="ok">found</span> "' + found.name + '" after ' + steps + ' comparison(s) — ' + fmtRs(found.price)
      : '<span class="hl">not found</span> after scanning all ' + steps + ' items';
  }

  function binarySearch(){
    const target = parseFloat(document.getElementById('binary-target').value);
    const log = document.getElementById('binary-log');

    // sort by price first (precondition for binary search), mirrors CatalogEngine.sortByPrice()
    arr.sort((a, b) => a.price - b.price);
    render();

    let lo = 0, hi = arr.length - 1, steps = 0, found = null;
    const trace = [];
    while(lo <= hi){
      steps++;
      const mid = Math.floor(lo + (hi - lo) / 2);
      trace.push(arr[mid].name + ' (' + fmtRs(arr[mid].price) + ')');
      if(arr[mid].price === target){ found = arr[mid]; break; }
      else if(arr[mid].price < target) lo = mid + 1;
      else hi = mid - 1;
    }
    log.innerHTML =
      'sorted catalog, then checked: ' + trace.join(' &rarr; ') + '\n' +
      (found
        ? '<span class="ok">found</span> "' + found.name + '" in ' + steps + ' comparison(s)'
        : '<span class="hl">no exact price match</span> after ' + steps + ' comparison(s)');
  }

  render();
  return { linearSearch, binarySearch };
})();

// -------------------------------------------------------------------
// MODULE 3a — BST (range queries on price)
// -------------------------------------------------------------------
const bstDemo = (() => {
  class Node {
    constructor(product){ this.product = product; this.left = null; this.right = null; }
  }
  let root = null;
  PRODUCTS.forEach(p => { root = insert(root, p); });

  function insert(node, product){
    if(!node) return new Node(product);
    if(product.price < node.product.price) node.left = insert(node.left, product);
    else node.right = insert(node.right, product);
    return node;
  }

  // assign x,y coordinates for drawing, in-order traversal gives x ordering
  function layout(){
    const positions = new Map();
    let counter = 0;
    function depth(node, d){
      if(!node) return;
      depth(node.left, d + 1);
      positions.set(node.product.id, { x: 60 + counter * 80, y: 30 + d * 55, node });
      counter++;
      depth(node.right, d + 1);
    }
    depth(root, 0);
    return positions;
  }

  function draw(highlightIds = new Set()){
    const positions = layout();
    const svg = document.getElementById('bst-svg');
    let edges = '';
    let nodes = '';

    function walk(node){
      if(!node) return;
      const pos = positions.get(node.product.id);
      if(node.left){
        const lp = positions.get(node.left.product.id);
        edges += `<line x1="${pos.x}" y1="${pos.y}" x2="${lp.x}" y2="${lp.y}" stroke="#262E37" stroke-width="1.5"/>`;
      }
      if(node.right){
        const rp = positions.get(node.right.product.id);
        edges += `<line x1="${pos.x}" y1="${pos.y}" x2="${rp.x}" y2="${rp.y}" stroke="#262E37" stroke-width="1.5"/>`;
      }
      walk(node.left); walk(node.right);
    }
    walk(root);

    positions.forEach(({x, y, node}) => {
      const hl = highlightIds.has(node.product.id);
      const stroke = hl ? '#3DBFAD' : '#262E37';
      const fill = hl ? '#1F4843' : '#171D24';
      const textColor = hl ? '#3DBFAD' : '#9CA3AE';
      nodes += `<g>
        <rect x="${x-34}" y="${y-16}" width="68" height="32" rx="6" fill="${fill}" stroke="${stroke}" stroke-width="1.5"/>
        <text x="${x}" y="${y+4}" text-anchor="middle" font-family="JetBrains Mono, monospace" font-size="10.5" fill="${textColor}">₹${node.product.price}</text>
      </g>`;
    });

    svg.innerHTML = edges + nodes;
  }

  function rangeQuery(){
    const min = parseFloat(document.getElementById('range-min').value);
    const max = parseFloat(document.getElementById('range-max').value);
    const matches = [];
    function search(node){
      if(!node) return;
      if(node.product.price > min) search(node.left);
      if(node.product.price >= min && node.product.price <= max) matches.push(node.product);
      if(node.product.price < max) search(node.right);
    }
    search(root);
    draw(new Set(matches.map(p => p.id)));
    const log = document.getElementById('bst-log');
    log.innerHTML = matches.length
      ? '<span class="ok">' + matches.length + ' match(es)</span> between ' + fmtRs(min) + ' and ' + fmtRs(max) + ': ' +
        matches.map(p => p.name).join(', ')
      : '<span class="hl">no products</span> in that range';
  }

  draw();
  return { rangeQuery };
})();

// -------------------------------------------------------------------
// MODULE 3b — Min-heap (priority orders)
// -------------------------------------------------------------------
const heapDemo = (() => {
  let heap = [];

  function siftUp(i){
    while(i > 0){
      const parent = Math.floor((i - 1) / 2);
      if(heap[parent].priority <= heap[i].priority) break;
      [heap[parent], heap[i]] = [heap[i], heap[parent]];
      i = parent;
    }
  }
  function siftDown(i){
    const n = heap.length;
    while(true){
      let smallest = i;
      const l = 2*i+1, r = 2*i+2;
      if(l < n && heap[l].priority < heap[smallest].priority) smallest = l;
      if(r < n && heap[r].priority < heap[smallest].priority) smallest = r;
      if(smallest === i) break;
      [heap[smallest], heap[i]] = [heap[i], heap[smallest]];
      i = smallest;
    }
  }
  function insert(order){
    heap.push(order);
    siftUp(heap.length - 1);
  }
  function extractMin(){
    if(heap.length === 0) return null;
    const top = heap[0];
    const last = heap.pop();
    if(heap.length > 0){ heap[0] = last; siftDown(0); }
    draw();
    return top;
  }
  function reset(){
    heap = [];
    ORDERS.forEach(insert);
    draw();
  }

  function draw(){
    const svg = document.getElementById('heap-svg');
    if(heap.length === 0){
      svg.innerHTML = `<text x="320" y="100" text-anchor="middle" font-family="JetBrains Mono, monospace" font-size="13" fill="#5C6470">heap empty — click reset</text>`;
      return;
    }
    const xs = [], ys = [];
    const positions = [];
    const levelGap = 55, xGap = 70;
    heap.forEach((node, i) => {
      const depth = Math.floor(Math.log2(i + 1));
      const levelStart = Math.pow(2, depth) - 1;
      const posInLevel = i - levelStart;
      const levelWidth = Math.pow(2, depth);
      const totalWidth = 600;
      const x = (totalWidth / (levelWidth + 1)) * (posInLevel + 1) + 20;
      const y = 30 + depth * levelGap;
      positions.push({x, y, node});
    });

    let edges = '';
    let nodes = '';
    positions.forEach((p, i) => {
      const l = 2*i+1, r = 2*i+2;
      if(l < positions.length) edges += `<line x1="${p.x}" y1="${p.y}" x2="${positions[l].x}" y2="${positions[l].y}" stroke="#262E37" stroke-width="1.5"/>`;
      if(r < positions.length) edges += `<line x1="${p.x}" y1="${p.y}" x2="${positions[r].x}" y2="${positions[r].y}" stroke="#262E37" stroke-width="1.5"/>`;
    });
    positions.forEach((p, i) => {
      const isRoot = i === 0;
      const stroke = isRoot ? '#E8A33D' : '#262E37';
      const fill = isRoot ? '#5C4A2A' : '#171D24';
      const textColor = isRoot ? '#E8A33D' : '#9CA3AE';
      nodes += `<g>
        <rect x="${p.x-32}" y="${p.y-15}" width="64" height="30" rx="6" fill="${fill}" stroke="${stroke}" stroke-width="1.5"/>
        <text x="${p.x}" y="${p.y+4}" text-anchor="middle" font-family="JetBrains Mono, monospace" font-size="10" fill="${textColor}">P${p.node.priority} #${p.node.id}</text>
      </g>`;
    });
    svg.innerHTML = edges + nodes;
  }

  function extractMinUI(){
    const order = extractMin();
    if(!order) return;
  }

  reset();
  return { extractMin: extractMinUI, reset };
})();

// -------------------------------------------------------------------
// MODULE 4 — Optimizer (Greedy vs DP knapsack)
// -------------------------------------------------------------------
const optimizerDemo = (() => {
  const slider = document.getElementById('budget-slider');
  slider.addEventListener('input', () => {
    document.getElementById('budget-val').textContent = fmtRs(slider.value);
  });

  function greedyBatch(orders, capacity){
    const sorted = [...orders].sort((a, b) => a.price - b.price);
    const batch = [];
    let remaining = capacity;
    for(const o of sorted){
      if(o.price <= remaining){ batch.push(o); remaining -= o.price; }
    }
    return batch;
  }

  function dpKnapsack(orders, budget){
    const n = orders.length;
    const weight = orders.map(o => Math.round(o.price));
    const value = orders.map(o => 6 - o.priority);
    const dp = Array.from({length: n+1}, () => new Array(budget+1).fill(0));

    for(let i = 1; i <= n; i++){
      for(let w = 0; w <= budget; w++){
        if(weight[i-1] <= w){
          dp[i][w] = Math.max(dp[i-1][w], value[i-1] + dp[i-1][w - weight[i-1]]);
        } else {
          dp[i][w] = dp[i-1][w];
        }
      }
    }
    const chosen = [];
    let w = budget;
    for(let i = n; i > 0; i--){
      if(dp[i][w] !== dp[i-1][w]){
        chosen.push(orders[i-1]);
        w -= weight[i-1];
      }
    }
    return { chosen, value: dp[n][budget] };
  }

  function renderTable(elId, batch){
    const el = document.getElementById(elId);
    el.innerHTML = `<tr><th>order</th><th>customer</th><th>price</th><th>priority</th></tr>` +
      batch.map(o => `<tr class="chosen"><td>#${o.id}</td><td>${o.customer}</td><td>${fmtRs(o.price)}</td><td>P${o.priority}</td></tr>`).join('');
  }

  function run(){
    const budget = parseInt(slider.value, 10);
    const greedy = greedyBatch(ORDERS, budget);
    const dp = dpKnapsack(ORDERS, budget);

    renderTable('greedy-table', greedy);
    renderTable('dp-table', dp.chosen);

    document.getElementById('greedy-count').textContent = greedy.length;
    document.getElementById('greedy-cost').textContent = fmtRs(greedy.reduce((s,o) => s + o.price, 0));
    document.getElementById('dp-count').textContent = dp.chosen.length;
    document.getElementById('dp-value').textContent = dp.value;
  }

  return { run };
})();

// -------------------------------------------------------------------
// MODULE 5 — Graph (Dijkstra shortest path)
// -------------------------------------------------------------------
const graphDemo = (() => {
  const nodeNames = {0:'Warehouse', 1:'City A', 2:'City B', 3:'City C', 4:'City D', 5:'City E'};
  const nodePos = {
    0: {x:80,  y:160},
    1: {x:240, y:60},
    2: {x:240, y:260},
    3: {x:420, y:60},
    4: {x:420, y:260},
    5: {x:560, y:160},
  };
  const edgesList = [
    [0,1,4], [0,2,2], [1,2,1], [1,3,5],
    [2,3,8], [2,4,10], [3,4,2], [3,5,6], [4,5,3]
  ];
  const adjacency = {};
  Object.keys(nodeNames).forEach(id => adjacency[id] = []);
  edgesList.forEach(([a,b,w]) => {
    adjacency[a].push([b,w]);
    adjacency[b].push([a,w]);
  });

  function dijkstra(source, target){
    const dist = {}; const prev = {}; const visited = new Set();
    Object.keys(nodeNames).forEach(id => dist[id] = Infinity);
    dist[source] = 0;
    const pq = [[source, 0]];
    while(pq.length){
      pq.sort((a,b) => a[1]-b[1]);
      const [u, d] = pq.shift();
      if(visited.has(u)) continue;
      visited.add(u);
      if(parseInt(u) === target) break;
      for(const [v, w] of adjacency[u]){
        if(dist[u] + w < dist[v]){
          dist[v] = dist[u] + w;
          prev[v] = u;
          pq.push([v, dist[v]]);
        }
      }
    }
    const path = [];
    let step = target;
    if(dist[target] === Infinity) return { path: [], dist };
    while(step !== undefined){
      path.unshift(parseInt(step));
      step = prev[step];
    }
    return { path, dist };
  }

  function draw(highlightPath = []){
    const svg = document.getElementById('graph-svg');
    const pathEdges = new Set();
    for(let i = 0; i < highlightPath.length - 1; i++){
      pathEdges.add(highlightPath[i] + '-' + highlightPath[i+1]);
      pathEdges.add(highlightPath[i+1] + '-' + highlightPath[i]);
    }
    let edgesHtml = '';
    edgesList.forEach(([a,b,w]) => {
      const onPath = pathEdges.has(a+'-'+b);
      const pa = nodePos[a], pb = nodePos[b];
      const midX = (pa.x+pb.x)/2, midY = (pa.y+pb.y)/2;
      edgesHtml += `<line x1="${pa.x}" y1="${pa.y}" x2="${pb.x}" y2="${pb.y}" stroke="${onPath ? '#3DBFAD' : '#262E37'}" stroke-width="${onPath ? 3 : 1.5}"/>`;
      edgesHtml += `<rect x="${midX-12}" y="${midY-10}" width="24" height="16" rx="3" fill="#0A0E12"/>`;
      edgesHtml += `<text x="${midX}" y="${midY+2}" text-anchor="middle" font-family="JetBrains Mono, monospace" font-size="10" fill="${onPath ? '#3DBFAD' : '#5C6470'}">${w}</text>`;
    });
    let nodesHtml = '';
    Object.entries(nodePos).forEach(([id, p]) => {
      const onPath = highlightPath.includes(parseInt(id));
      const isSource = parseInt(id) === 0;
      const stroke = onPath ? '#3DBFAD' : (isSource ? '#E8A33D' : '#262E37');
      const fill = onPath ? '#1F4843' : (isSource ? '#5C4A2A' : '#171D24');
      const textColor = onPath ? '#3DBFAD' : (isSource ? '#E8A33D' : '#9CA3AE');
      nodesHtml += `<g>
        <rect x="${p.x-44}" y="${p.y-16}" width="88" height="32" rx="6" fill="${fill}" stroke="${stroke}" stroke-width="1.5"/>
        <text x="${p.x}" y="${p.y+4}" text-anchor="middle" font-family="JetBrains Mono, monospace" font-size="11" fill="${textColor}">${nodeNames[id]}</text>
      </g>`;
    });
    svg.innerHTML = edgesHtml + nodesHtml;
  }

  function findPath(){
    const target = parseInt(document.getElementById('graph-target').value, 10);
    const { path, dist } = dijkstra(0, target);
    draw(path);
    const log = document.getElementById('graph-log');
    if(path.length === 0){
      log.innerHTML = '<span class="hl">unreachable</span>';
      return;
    }
    log.innerHTML =
      '<span class="ok">shortest distance: ' + dist[target] + '</span>\n' +
      'route: ' + path.map(id => nodeNames[id]).join(' &rArr; ');
  }

  draw();
  return { findPath };
})();

// -------------------------------------------------------------------
// Hero strip — small animated thesis: queue -> heap -> graph chain
// -------------------------------------------------------------------
(function heroAnim(){
  const svg = document.getElementById('hero-svg');
  const steps = [
    {x:40,  label:'queue'},
    {x:220, label:'hash'},
    {x:400, label:'BST'},
    {x:580, label:'heap'},
    {x:760, label:'graph'},
  ];
  let html = '';
  for(let i = 0; i < steps.length - 1; i++){
    html += `<line x1="${steps[i].x+50}" y1="45" x2="${steps[i+1].x}" y2="45" stroke="#262E37" stroke-width="1.5" marker-end="url(#hero-arrow)"/>`;
  }
  html = `<defs><marker id="hero-arrow" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
    <path d="M2 1L8 5L2 9" fill="none" stroke="#5C6470" stroke-width="1.5"/></marker></defs>` + html;
  steps.forEach((s, i) => {
    html += `<g>
      <rect x="${s.x}" y="29" width="50" height="32" rx="6" fill="#171D24" stroke="#262E37" stroke-width="1.5"/>
      <text x="${s.x+25}" y="50" text-anchor="middle" font-family="JetBrains Mono, monospace" font-size="11" fill="#9CA3AE">${s.label}</text>
    </g>`;
  });
  svg.innerHTML = html;
})();

// Set GitHub link if hosted alongside the repo (best-effort, harmless if not).
document.getElementById('github-link').setAttribute('href', '../');
