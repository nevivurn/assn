import java.util.*;

public class AVLTree<K extends Comparable<K>, V> {
	public class Node<K extends Comparable<K>, V> {
		private final K k;
		private final V v;

		private Node<K, V> lt, rt;
		private int height;

		private Node(K k, V v) {
			this.k = k;
			this.v = v;

			this.lt = null;
			this.rt = null;
			this.height = 1;
		}

		private int getHeight() {
			int n = 0;
			if (this.lt != null) n = this.lt.height;
			if (this.rt != null && n < this.rt.height) n = this.rt.height;
			this.height = n+1;
			return this.height;
		}

		private boolean isBalanced() {
			int l = this.lt != null ? this.lt.height : 0;
			int r = this.rt != null ? this.rt.height : 0;
			return Math.abs(l-r) <= 1;
		}

		// insert new node, return new root
		private Node<K, V> insert(Node<K, V> node) {
			if (node.k.compareTo(this.k) < 0) {
				if (this.lt == null) this.lt = node;
				else this.lt = this.lt.insert(node);
			} else {
				if (this.rt == null) this.rt = node;
				else this.rt = this.rt.insert(node);
			}

			Node<K, V> ret = this;
			if (!this.isBalanced()) ret = this.balance();
			this.getHeight();
			return ret;
		}

		// rotate left, return new root
		private Node<K, V> rotateLeft() {
			Node<K, V> root = this.rt;
			this.rt = root.lt;
			root.lt = this;

			this.getHeight();
			root.getHeight();
			return root;
		}

		// rotate right, return new root
		private Node<K, V> rotateRight() {
			Node<K, V> root = this.lt;
			this.lt = root.rt;
			root.rt = this;

			this.getHeight();
			root.getHeight();
			return root;
		}

		// balance the unbalanced tree rooted at this, return new root
		private Node<K, V> balance() {
			boolean fleft = this.rt == null || (this.lt != null && this.lt.height > this.rt.height);
			Node<K, V> sub = fleft ? this.lt : this.rt;
			boolean sleft = sub.rt == null || (sub.lt != null && sub.lt.height > sub.rt.height);

			if (fleft && sleft) {
				return this.rotateRight();
			} else if (!fleft && !sleft) {
				return this.rotateLeft();
			} else if (fleft && !sleft) {
				this.lt = sub.rotateLeft();
				return this.rotateRight();
			} else if (!fleft && sleft) {
				this.rt = sub.rotateRight();
				return this.rotateLeft();
			} else {
				throw new IllegalStateException("should never happen");
			}
		}
	}

	private Node<K, V> root;

	// search for a node in the tree, null if not found
	public V search(K k) {
		Node<K, V> cur = root;
		while (cur != null) {
			int cmp = k.compareTo(cur.k);
			if (cmp == 0) return cur.v;
			cur = cmp < 0 ? cur.lt : cur.rt;
		}
		return null;
	}

	// insert a new node into the tree
	public void insert(K k, V v) {
		Node<K, V> node = new Node<>(k, v);
		if (this.root == null) this.root = node;
		else this.root = this.root.insert(node);
	}

	// preorder traversal
	public List<V> traverse() {
		Deque<Node<K, V>> q = new LinkedList<>();
		List<V> l = new LinkedList<>();
		if (this.root != null) q.addFirst(this.root);

		while (!q.isEmpty()) {
			Node<K, V> cur = q.removeFirst();
			l.add(cur.v);
			if (cur.rt != null) q.addFirst(cur.rt);
			if (cur.lt != null) q.addFirst(cur.lt);
		}
		return l;
	}
}
