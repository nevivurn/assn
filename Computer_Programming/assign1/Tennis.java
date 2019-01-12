// Yongun Seong

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Tennis {
	private static int type, sex;

	private static enum Side {
		LEFT("Left"), RIGHT("Right");

		private String name;

		private Side(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private static class TennisGame {
		private static final String[] NAMES = {"0", "15", "30", "40", "40A"};

		private final boolean tiebreak; // Whether this is a tiebreaker game
		private int l, r; // Game score

		public TennisGame(boolean tiebreak) {
			this.tiebreak = tiebreak;
		}

		public boolean isTiebreak() {
			return this.tiebreak;
		}

		public void win(Side side) {
			if (side == Side.LEFT) {
				this.l++;
			} else if (side == side.RIGHT) {
				this.r++;
			}

			if (this.tiebreak) {
				return;
			}

			if (this.l == 4 && this.r == 4) {
				this.l = 3;
				this.r = 3;
			}
		}

		public boolean isFinished() {
			int score = 4;
			if (this.tiebreak) {
				score = 7;
			}
			return (this.l >= score || this.r >= score)
				&& Math.abs(this.l-this.r) >= 2;
		}

		public Side getWinner() {
			if (!this.isFinished()) {
				throw new IllegalStateException("Game is not over");
			}

			return this.l > this.r ? Side.LEFT : Side.RIGHT;
		}

		public String getStatus() {
			if (this.l == 0 && this.r == 0) {
				return "";
			}

			if (this.tiebreak) {
				return String.format("(%d-%d)", this.l, this.r);
			}

			String l = NAMES[this.l <= 4 ? this.l : 4];
			String r = NAMES[this.r <= 4 ? this.r : 4];
			return String.format("(%s-%s)", l, r);
		}
	}

	private static class TennisSet {
		private final boolean tiebreak; // Whether this is a tiebreaker set
		private int l, r; // Set score
		private TennisGame game; // Current game

		public TennisSet(boolean tiebreak) {
			this.tiebreak = tiebreak;
			this.game = new TennisGame(false);
		}

		public void win(Side side) {
			this.game.win(side);
			if (this.game.isFinished()) {
				if (this.game.getWinner() == Side.LEFT) {
					this.l++;
				} else {
					this.r++;
				}

				if (this.isFinished()) {
					if (!this.game.isTiebreak()) {
						this.game = new TennisGame(false);
					}
					return;
				}
				this.game = new TennisGame(this.tiebreak
					&& this.l == 6 & this.r == 6);
			}
		}

		public boolean isFinished() {
			if ((this.l >= 6 || this.r >= 6)
				&& Math.abs(this.l-this.r) >= 2) {
				return true;
			}

			if (this.tiebreak) {
				return this.l == 7 || this.r == 7;
			}

			return false;
		}

		public Side getWinner() {
			if (!this.isFinished()) {
				throw new IllegalStateException("Set is not over");
			}

			return this.l > this.r ? Side.LEFT : Side.RIGHT;
		}

		public String getStatus() {
			if (this.l == 0 && this.r == 0 && this.game.getStatus().isEmpty()) {
				return "";
			}

			return String.format("%d-%d", this.l, this.r)
				+ game.getStatus();
		}
	}

	private static class TennisMatch {
		private final int maxSet;
		private final boolean tiebreak; // Whether deciding set is tiebreak.
		private List<TennisSet> prev;
		private TennisSet set;
		private int l, r;

		public TennisMatch(int maxSet, boolean tiebreak) {
			this.maxSet = maxSet;
			this.tiebreak = tiebreak;

			this.prev = new ArrayList<>();
			this.set = new TennisSet(true);
		}

		public void win(Side side) {
			this.set.win(side);

			if (this.set.isFinished()) {
				if (this.set.getWinner() == Side.LEFT) {
					this.l++;
				} else {
					this.r++;
				}

				if (this.isFinished()) {
					return;
				}
				prev.add(this.set);

				if (this.l == this.maxSet/2 && this.r == this.maxSet/2) {
					this.set = new TennisSet(this.tiebreak);
				} else {
					this.set = new TennisSet(true);
				}
			}
		}

		public boolean isFinished() {
			return this.l > this.maxSet/2 || this.r > this.maxSet/2;
		}

		public String getStatus() {
			StringBuilder sb = new StringBuilder();
			for (TennisSet set : this.prev) {
				sb.append(" " + set.getStatus());
			}

			if (!this.set.getStatus().isEmpty()) {
				sb.append(" " + this.set.getStatus());
			}
			return sb.toString();
		}
	}

	public static void main(String args[]) throws IOException {
		/*
		// DEBUG
		PrintWriter out = new PrintWriter(System.out);
		Scanner sc = new Scanner(System.in);
		/**/
		PrintWriter out = new PrintWriter(new File(args[1]));
		Scanner sc = new Scanner(new File(args[0]));
		/**/

		char[] in = sc.nextLine().toCharArray();
		sc.close();

		boolean tiebreak; // Whether the final deciding set is tiebreak
		if (in[0] == 'A') {
			tiebreak = false;
			out.print("Australian Open/");
		} else {
			tiebreak = true;
			out.print("US Open/");
		}

		int maxSet; // The maximum total number of sets
		if (in[1] == 'M') {
			maxSet = 5;
			out.println("Male chosen.");
		} else {
			maxSet = 3;
			out.println("Female chosen.");
		}

		TennisMatch match = new TennisMatch(maxSet, tiebreak);
		out.println("Current: 0-0");

		for (int i = 2; i < in.length; i++) {
			Side side = in[i] == 'L' ? Side.LEFT : Side.RIGHT;
			out.println(side + " wins");

			match.win(side);
			out.println("Current:" + match.getStatus());

			if (match.isFinished()) {
				out.println("Game finished!");
				break;
			}
		}

		out.close();
	}
}
