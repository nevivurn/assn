// Yongun Seong

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Scanner;

public class Affine_cipher {
	private static final String CHARSET = "#abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!?";

	public static void main(String args[]) throws IOException {
		// Read input
		String line, op;
		int a, b;
		try (Scanner in = new Scanner(new File(args[0]))) {
			line = in.nextLine();
			a = in.nextInt();
			b = in.nextInt();
			op = in.next();
		}

		switch (op) {
			case "e":
				break;
			case "d":
				a = BigInteger.valueOf(a).modInverse(BigInteger.valueOf(CHARSET.length())).intValue();
				b = (-b*a) % CHARSET.length();
				break;
			default:
				return;
		}

		StringBuilder sb = new StringBuilder(line.length());
		for (char c : line.toCharArray()) {
			int i = CHARSET.indexOf(c);
			if (i == -1) {
				sb.setLength(0);
				sb.append("Error, input value " + c + " out of range");
				break;
			}

			i = (i*a + b) % CHARSET.length();
			if (i < 0) i = CHARSET.length()+i;

			sb.append(CHARSET.charAt(i));
		}

		try (PrintWriter out = new PrintWriter(args[1])) {
			out.println(sb.toString());
		}
	}
}
