// Yongun Seong
// Computer Programming final exam: problem 3
// WordCount.java

import java.util.*;
import java.io.*;

public class WordCount {
	public static void main(String[] args) throws IOException {
		String input = args[0];
		String output = args[1];

		Scanner sc = new Scanner(new File(input));
		String line = sc.nextLine();

		line = line.replaceAll("[\\.,]", " ");

		String[] split = line.split(" ");
		int len = 0;
		int cnt = 0;
		for (String w : split) {
			if (w.length() == 0) continue;
			len += w.length();
			cnt++;
		}

		double avg = (double) len / cnt;
		int overcnt = 0;
		for (String w : split) {
			if (w.length() > avg) overcnt++;
		}

		PrintWriter out = new PrintWriter(output);
		out.println("Number of words = " + cnt);
		out.printf("Average length of a word = %.2f\n", avg);
		out.println("Number of words above the average length = " + overcnt);
		out.flush();
	}
}
