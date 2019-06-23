import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Matching {
	public static void main(String args[]) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//PrintWriter out = new PrintWriter(System.out);
		PrintWriter out = new PrintWriter(System.out, true);

		while (true) {
			try {
				String line = in.readLine();
				if (line.equals("QUIT")) break;
				command(out, line);
			}
			catch (IOException e) {
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
		out.close();
	}

	private static class Pair<E extends Comparable<E>> implements Comparable<Pair<E>> {
		private final E a, b;
		private Pair(E a, E b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public String toString() {
			return String.format("(%s, %s)", this.a, this.b);
		}

		@Override
		public int hashCode() {
			return Objects.hash(a, b);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pair)) return false;
			Pair op = (Pair) o;
			return this.a.equals(op.a) && this.b.equals(op.b);
		}

		@Override
		public int compareTo(Pair<E> o) {
			int i = this.a.compareTo(o.a);
			if (i != 0) return i;
			return this.b.compareTo(o.b);
		}
	}

	// State
	private static List<String> lines;
	private static HashTable<AVLTree<String, List<Pair<Integer>>>> map;

	private static void command(PrintWriter out, String line) throws IOException {
		String args = line.substring(2);
		switch (line.charAt(0)) {
			case '<':
				cmdRead(out, args);
				break;
			case '@':
				cmdPrint(out, args);
				break;
			case '?':
				cmdSearch(out, args);
				break;
		}
	}

	private static void cmdRead(PrintWriter out, String args) throws IOException {
		lines = new LinkedList<>();
		map = new HashTable<>(100);

		Scanner in = new Scanner(new File(args));
		for (int i = 0; in.hasNextLine(); i++) {
			String line = in.nextLine();
			if (line.length() < 6) continue;
			lines.add(line);

			int sum = 0;
			for (int j = 0; j < 6; j++) {
				sum = (sum + line.charAt(j)) % 100;
			}
			insert(sum, line, new Pair<>(i, 0));

			for (int j = 0; j < line.length()-6; j++) {
				sum = (sum + line.charAt(j+6) - line.charAt(j)) % 100;
				if (sum < 0) sum += 100;
				insert(sum, line, new Pair<>(i, j+1));
			}
		}
	}

	private static void insert(int sum, String line, Pair<Integer> node) {
		String sub = line.substring(node.b, node.b+6);

		AVLTree<String, List<Pair<Integer>>> tree = map.get(sum);
		if (tree == null) {
			tree = new AVLTree<>();
			map.put(sum, tree);
		}

		List<Pair<Integer>> lst = tree.search(sub);
		if (lst == null) {
			lst = new LinkedList<>();
			tree.insert(sub, lst);
		}
		lst.add(node);
	}

	private static void cmdPrint(PrintWriter out, String args) {
		AVLTree<String, List<Pair<Integer>>> tree = map.get(Integer.parseInt(args));
		if (tree == null) {
			out.println("EMPTY");
			return;
		}

		boolean first = true;
		for (List<Pair<Integer>> l : tree.traverse()) {
			Pair<Integer> p = l.get(0);
			if (!first) out.print(" ");
			out.print(lines.get(p.a).substring(p.b, p.b+6));
			first = false;
		}
		out.println();
	}

	private static void cmdSearch(PrintWriter out, String args) {
		List<Pair<Integer>> full = null;
		
		boolean first = true;
		for (int i = 0; i < args.length(); i += 6) {
			int offset = i;
			if (i+6 > args.length()) {
				offset = args.length()-6;
			}

			String sub = args.substring(offset, offset+6);
			int sum = 0;
			for (int j = 0; j < sub.length(); j++) {
				sum = (sum + sub.charAt(j)) % 100;
			}
			
			AVLTree<String, List<Pair<Integer>>> tree = map.get(sum);
			if (tree == null) {
				full = null;
				break;
			}

			List<Pair<Integer>> lst = tree.search(sub);
			if (lst == null) {
				full = null;
				break;
			}

			List<Pair<Integer>> cur = new ArrayList<>(lst.size());
			for (Pair<Integer> p : lst) {
				cur.add(new Pair<>(p.a, p.b-offset));
			}

			if (first) full = cur;
			else {
				Iterator<Pair<Integer>> it = full.iterator();
				while (it.hasNext()) {
					Pair<Integer> p = it.next();
					// binsearch
					int lo = 0, hi = cur.size()-1;
					boolean found = false;
					while (lo <= hi) {
						int mid = (lo+hi)/2;
						int cmp = p.compareTo(cur.get(mid));
						if (cmp == 0) {
							found = true;
							break;
						}
						if (cmp < 0) hi = mid-1;
						else lo = mid+1;
					}
					if (!found) it.remove();
				}
				if (full.size() == 0) break;
			}
			first = false;
		}

		if (full == null || full.isEmpty()) {
			out.println("(0, 0)");
			return;
		}

		List<Pair<Integer>> sorted = full.stream().sorted().collect(Collectors.toList());
		first = true;
		for (Pair<Integer> p : sorted) {
			if (!first) out.print(" ");
			out.printf("(%s, %s)", p.a+1, p.b+1);
			first = false;
		}
		out.println();
	}
}
