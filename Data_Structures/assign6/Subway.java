import java.io.*;
import java.util.*;

public class Subway {
	private static final int TRANSFER_COST = 5;

	public static void main(String[] args) {
		if (args.length < 1) {
			return;
		}

		// set up I/O
		try (
			BufferedReader data = new BufferedReader(new FileReader(args[0]));
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(System.out);
		) {
			process(data, in, out);
		} catch (IOException e) {
			System.err.println("io error: " + e.getMessage());
		}
	}

	private Map<String, Station> stations; // num -> { num, name, line }
	private Map<String, Map<String, Integer>> conns; // num -> { num -> duration }
	private Map<String, Set<String>> transfers; // name -> { num... }

	private static void process(BufferedReader data, BufferedReader in, PrintWriter out) throws IOException {
		// parse data and preprocess
		Subway sub = new Subway();
		sub.stations = parseStations(data);
		sub.conns = parseDurations(data, sub.stations);
		sub.calcTransfers();

		// handle queries
		String line;
		while ((line = in.readLine()) != null) {
			if (line.equals("QUIT")) break;
			String[] split = line.split(" ");
			out.println(sub.handle(split[0], split[1]));
		}
	}

	private static Map<String, Station> parseStations(BufferedReader rd) throws IOException {
		Map<String, Station> stations = new HashMap<>();

		String s;
		while ((s = rd.readLine()) != null) {
			if (s.length() == 0) break; // empty line

			String[] split = s.split(" ");
			stations.put(split[0], new Station(split[0], split[1], split[2]));
		}

		return stations;
	}

	private static Map<String, Map<String, Integer>> parseDurations(BufferedReader rd, Map<String, Station> stations) throws IOException {
		Map<String, Map<String, Integer>> conns = new HashMap<>();
		for (String num : stations.keySet()) {
			conns.put(num, new HashMap<>());
		}

		String s;
		while ((s = rd.readLine()) != null) {
			String[] split = s.split(" ");
			int duration = Integer.parseInt(split[2]);
			conns.get(split[0]).put(split[1], duration);
		}

		return conns;
	}

	private void calcTransfers() {
		Map<String, Set<String>> transfers = new HashMap<>();
		for (Station station : this.stations.values()) {
			Set<String> lines = new HashSet<>();
			Set<String> glines = transfers.putIfAbsent(station.name, lines);
			if (glines != null) lines = glines;
			lines.add(station.num);
		}
		this.transfers = transfers;
	}

	private String handle(String start, String end) {
		Map<String, PartRoute> min = new HashMap<>(); // minimum queued by num
		Map<String, PartRoute> seen = new HashMap<>(); // explored by num
		Set<String> seenNames = new HashSet<>(); // explored by name
		PriorityQueue<PartRoute> pq = new PriorityQueue<PartRoute>(
			(PartRoute a, PartRoute b) -> {
				long diff = a.cost-b.cost;
				if (diff == 0) return 0;
				return (int) (diff / Math.abs(diff));
			}
		); // actual queue

		// initial state
		for (String num : transfers.get(start)) {
			pq.add(new PartRoute(num, null, 0));
		}
		seenNames.add(start);

		// yay, dijkstra
		PartRoute last = pq.peek(); // used for reconstruction
		while (!seenNames.contains(end)) { // don't check for empty queue, assume connected
			PartRoute cur = pq.remove();
			Station curStation = stations.get(cur.cur);
			if (seen.containsKey(curStation.num)) continue; // already explored
			min.remove(curStation.num); // seems to perform better if cleaned up

			// mark explored
			seen.put(curStation.num, cur);
			seenNames.add(curStation.name);

			for (Map.Entry<String, Integer> e : conns.get(curStation.num).entrySet()) { // regular connections
				String dest = e.getKey();
				long cost = cur.cost + e.getValue();
				if (seen.containsKey(dest)) continue; // alredy explored
				if (min.containsKey(dest) && min.get(dest).cost <= cost) continue; // already better

				PartRoute r = new PartRoute(dest, curStation.num, cost);
				pq.add(r);
				min.put(dest, r);
			}

			long transCost = cur.cost + TRANSFER_COST;
			for (String dest : transfers.get(curStation.name)) { // transfers
				if (seen.containsKey(dest)) continue; // alredy explored
				if (min.containsKey(dest) && min.get(dest).cost <= transCost) continue; // already better

				PartRoute r = new PartRoute(dest, curStation.num, transCost);
				pq.add(r);
				min.put(dest, r);
			}
			last = cur;
		}

		// reconstruct path
		List<String> path = new ArrayList<>();
		long cost = last.cost;
		while (last != null) {
			path.add(stations.get(last.cur).name);
			last = seen.get(last.prev);
		}

		StringJoiner join = new StringJoiner(" ", "", "\n" + cost);
		String lastToken = null;
		for (int i = path.size()-1; i >= 0; i--) {
			String curToken = path.get(i);
			if (curToken.equals(lastToken)) {
				lastToken = null;
				join.add("[" + curToken + "]");
				continue;
			} else if (lastToken != null) {
				join.add(lastToken);
			}
			lastToken = curToken;
		}
		if (lastToken != null) join.add(lastToken);

		return join.toString();
	}

	private static class Station {
		private final String num, name, line;

		private Station(String num, String name, String line) {
			this.num = num;
			this.name = name;
			this.line = line;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Station)) return false; 
			Station os = (Station) o;
			if ((this.num == null) != (os.num == null) || !this.num.equals(os.num)) return false;
			if ((this.name == null) != (os.name == null) || !this.name.equals(os.name)) return false;
			if ((this.line == null) != (os.line == null) || !this.line.equals(os.line)) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.num, this.name, this.line);
		}
	}

	// used for partial route info
	private static class PartRoute {
		private final String cur, prev;
		private final long cost;

		private PartRoute(String cur, String prev, long cost) {
			this.prev = prev;
			this.cur = cur;
			this.cost = cost;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof PartRoute)) return false;
			PartRoute op = (PartRoute) o;
			if (this.cost != op.cost) return false;
			if ((this.cur == null) != (op.cur == null) || !this.cur.equals(op.cur)) return false;
			if ((this.prev == null) != (op.prev == null) || !this.prev.equals(op.prev)) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.cur, this.prev, this.cost);
		}
	}
}
