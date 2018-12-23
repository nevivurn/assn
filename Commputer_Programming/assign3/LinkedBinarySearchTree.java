// Yongun Seong

import java.util.*;

class LinkedBinarySearchTree {
	public LBST_node root;
	public LBST_node first;

	public void insert(int num) {
		LBST_node e = new LBST_node(num);

		// Empty
		if (root == null) {
			root = e;
			first = e;
			return;
		}

		LBST_node cur = root;
		LBST_node bound = null; // Largest number smaller than val
		while (true) {
			if (cur.val == num) return;

			if (num > cur.val) {
				if (cur.r_child != null) {
					bound = cur;
					cur = cur.r_child;
				} else {
					cur.r_child = e;
					e.next = cur.next;
					cur.next = e;
					break;
				}
			} else {
				if (cur.l_child != null) cur = cur.l_child;
				else {
					cur.l_child = e;
					e.next = cur;

					if (bound != null) bound.next = e;
					break;
				}
			}
		}

		if (num < first.val) first = e;
	}

	public void remove(int num) {
		// Search for node to remove
		LBST_node cur = root;
		LBST_node bound = null, parent = null;
		while (cur != null && cur.val != num) {
			parent = cur;
			if (num > cur.val) {
				bound = cur;
				cur = cur.r_child;
			} else cur = cur.l_child;
		}
		if (cur == null) return;

		// Find replacement, then replace it with its child
		LBST_node repl, replParent = null;
		if (cur.l_child != null) {
			repl = cur.l_child;
			while (repl.r_child != null) {
				replParent = repl;
				repl = repl.r_child;
			}
			if (replParent != null) replParent.r_child = repl.l_child;
			else cur.l_child  = repl.l_child;
		} else if (cur.r_child != null) {
			repl = cur.r_child;
			while (repl.l_child != null) {
				replParent = repl;
				repl = repl.l_child;
			}
			if (replParent != null) replParent.l_child = repl.r_child;
			else cur.r_child  = repl.r_child;
		} else repl = null; // No children

		// Set replacement's children
		if (repl != null) {
			repl.l_child = cur.l_child;
			repl.r_child = cur.r_child;
		}

		// Remove from list
		if (repl != null && repl.val < num) repl.next = cur.next;
		else if (bound != null) bound.next = cur.next;
		else first = first.next; // First removed

		// Remove from tree
		if (parent != null) {
			if (cur.equals(parent.r_child)) parent.r_child = repl;
			else parent.l_child = repl;
		} else root = repl; // Root removed
	}

	public boolean search(int num) {
		LBST_node cur = root;
		while (cur != null) {
			if (num == cur.val) return true;
			if (num > cur.val) cur = cur.r_child;
			else cur = cur.l_child;
		}
		return false;
	}

	public boolean range_search(int left, int right, int n) {
		LBST_node cur = first;
		for (int i = 0; i < left; i++) {
			if (cur == null) return false;
			cur = cur.next;
		}

		for (int i = 0; i < right-left+1; i++) {
			if (cur == null) return false;
			if (cur.val == n) return true;
			cur = cur.next;
		}

		return false;
	}

	public LBST_node[] list() {
		List<LBST_node> arr = new ArrayList<>();

		LBST_node cur = first;
		while (cur != null) {
			arr.add(cur);
			cur = cur.next;
		}

		return arr.toArray(new LBST_node[arr.size()]);
	}
}
