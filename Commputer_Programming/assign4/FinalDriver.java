// Yongun Seong

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FinalDriver {
	public static void main(String[] args) {
		tensor(args[0],args[1]);
	}

	public static void tensor(String input, String output) {
		Scanner in = new Scanner(readfile(input));
		int n = in.nextInt(), x = in.nextInt();

		StringBuilder out = new StringBuilder();

		Tensor[] tens = new Tensor[n];
		for (int i = 0; i < n; i++) {
			Tensor t;
			switch (in.next()) {
			case "S":
				t = new MyScalar(in.nextInt());
				break;
			case "V":
				int dim = in.nextInt();
				int[] vec = new int[dim];
				for (int j = 0; j < dim; j++) {
					vec[j] = in.nextInt();
				}
				t = new MyVector(vec);
				break;
			case "M":
				int h = in.nextInt(), w = in.nextInt();
				int[][] mat = new int[h][w];
				for (int mi = 0; mi < h; mi++) {
					for (int mj = 0; mj < w; mj++) {
						mat[mi][mj] = in.nextInt();
					}
				}
				t = new MyMatrix(mat);
				break;
			default:
				return;
			}

			out.append(t);
			out.append("\n");
			tens[i] = t;
		}

		while (x-- > 0) {
			Tensor a = tens[in.nextInt()-1];
			String op = in.next();
			Tensor b = tens[in.nextInt()-1];

			switch (op) {
			case "+":
				out.append(a.add(b));
				break;
			case "*":
				out.append(a.multiply(b));
				break;
			case "p=":
				if (!(a instanceof MyVector)) out.append("False");
				else out.append(((MyVector) a).permuteCompare(b) ? "True" : "False");
				break;
			default:
				return;
			}
			out.append("\n");
		}

		writefile(output, out.toString());
	}

	public static String readfile(String file) {
		String everything = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return everything;
	}

	public static void writefile(String file, String everything) {
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.print(everything);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
