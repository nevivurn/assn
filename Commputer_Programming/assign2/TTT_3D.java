// Yongun Seong

import java.util.Scanner;
import java.io.IOException;

public class TTT_3D {
	private static enum Side {
		X("X"), O("O"), NO("-");

		private String c;

		private Side(String c) {
			this.c = c;
		}

		public String toString() {
			return this.c;
		}

		private static String board(Side s) {
			if (s == null) return " ";
			return s.toString().toLowerCase();
		}
	}

	private static Side[][][] grid = new Side[3][3][3];

	public static void main(String[] args) {
		grid[1][1][1] = Side.NO;

		Scanner sc = new Scanner(System.in);
		Side s = Side.X;

		while (finished() == null) {
			System.out.println("Enter Input for Player "+s+":");

			String line = sc.nextLine();
			String[] spl = line.trim().split("\\s+");
			if (spl.length != 3) {
				System.out.println("Invalid Input! Try again.");
				continue;
			}

			int p = -1;
			switch (spl[0]) {
			case "T":
				p = 0;
				break;
			case "M":
				p = 1;
				break;
			case "B":
				p = 2;
				break;
			default:
				System.out.println("Invalid Input! Try again.");
				continue;
			}

			int c, r;
			try {
				c = Integer.parseInt(spl[1])-1;
				r = 3-Integer.parseInt(spl[2]);
			} catch (NumberFormatException e) {
				System.out.println("Invalid Input! Try again.");
				continue;
			}

			if (r < 0 || r >= 3 || c < 0 || c >= 3 || grid[p][r][c] != null) {
				System.out.println("Invalid Input! Try again.");
				continue;
			}

			grid[p][r][c] = s;
			if (s == Side.X) s = Side.O;
			else s = Side.X;

			printStatus();
		}

		switch (finished()) {
		case X:
			System.out.println("Player 1 win!");
			break;
		case O:
			System.out.println("Player 2 win!");
			break;
		case NO:
			System.out.println("The game is a tie. There is no winner.");
			break;
		}
	}

	private static final String[] LABELS = {"Top", "Mid", "Bot"};
	private static void printStatus() {
		for (int p = 0; p < 3; p++) {
			System.out.println(LABELS[p]);

			for (int r = 0; r < 3; r++) {
				System.out.println("+---+---+---+");
				System.out.printf("| %s | %s | %s |\n",
					Side.board(grid[p][r][0]),
					Side.board(grid[p][r][1]),
					Side.board(grid[p][r][2]));
			}
			System.out.println("+---+---+---+");
			System.out.println();
		}
	}

	private static interface Plane {
		public Side get(int r, int c);
	}
	private static final Plane[] PLANES = {
		(int r, int c) -> grid[0][r][c],
		(int r, int c) -> grid[1][r][c],
		(int r, int c) -> grid[2][r][c],
		(int r, int c) -> grid[r][0][c],
		(int r, int c) -> grid[r][2][c],
		(int r, int c) -> grid[r][c][0],
		(int r, int c) -> grid[r][c][2]
	};
	private static Side finished() {
		for (Plane pl : PLANES) {
			Side s = checkPlane(pl);
			if (s != null) return s;
		}

		// Tie
		for (int p = 0; p < 3; p++) {
			for (int r = 0; r < 3; r++) {
				for (int c = 0; c < 3; c++) {
					if (grid[p][r][c] == null) {
						return null;
					}
				}
			}
		}
		return Side.NO;
	}

	private static Side checkPlane(Plane pl) {
		for (int r = 0; r < 3; r++) {
			boolean pass = true;
			for (int c = 0; c < 2; c++) {
				if (pl.get(r, c) != pl.get(r, c+1)) {
					pass = false;
					break;
				}
			}
			if (pass && pl.get(r, 0) != null) return pl.get(r, 0);
		}

		for (int c = 0; c < 3; c++) {
			boolean pass = true;
			for (int r = 0; r < 2; r++) {
				if (pl.get(r, c) != pl.get(r+1, c)) {
					pass = false;
					break;
				}
			}
			if (pass && pl.get(0, c) != null) return pl.get(0, c);
		}

		if (pl.get(1, 1) == pl.get(0, 0) && pl.get(1, 1) == pl.get(2, 2))
			return pl.get(1, 1);
		if (pl.get(1, 1) == pl.get(2, 0) && pl.get(1, 1) == pl.get(0, 2))
			return pl.get(1, 1);

		return null;
	}
}
