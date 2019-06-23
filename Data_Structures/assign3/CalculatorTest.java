import java.io.*;
import java.util.Stack;

public class CalculatorTest {
	private static final boolean DEBUG = false;

	public static void main(String args[]) throws IOException {
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			out = new PrintWriter(System.out, DEBUG);
			in = new BufferedReader(new InputStreamReader(System.in));

			String input = in.readLine();
			while (input != null && !input.equalsIgnoreCase("q")) {
				try {
					command(input, out);
				} catch (EvalException e) {
					out.println(e.getMessage());
				}
				input = in.readLine();
			}
			if (input == null) out.println("ERROR");
		} catch (IOException e) {
			out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		} finally {
			in.close();
			out.close();
		}
	}

	private static void command(String in, PrintWriter out) throws EvalException {
		String postfix = convert(in);
		long val = evaluate(postfix);
		out.println(postfix);
		out.println(val);
	}

	// True if a < b, ie. b should be executed before a.
	private static boolean compareOp(String a, String b) {
		int ap = precedence(a);
		int bp = precedence(b);

		if (ap != bp) return ap > bp;
		if (a.equals("^") || a.equals("~")) return false;
		return true;
	}

	// Return a relative precedence of the operators.
	private static int precedence(String op) {
		switch (op) {
			case "^": return 1;
			case "~": return 2;
			case "*":
			case "/":
			case "%": return 3;
			case "+":
			case "-": return 4;
			case "(": return 5; // Lowest precedence, since all ops inside parens happen before end of parens
			default: throw new IllegalArgumentException("incorrect operator");
		}
	}

	// Convert an infix string to postfix.
	private static String convert(String in) throws EvalException {
		Tokenizer tok = new Tokenizer(in);

		StringBuilder sb = new StringBuilder();
		Stack<String> ops = new Stack<>();

		boolean wasOp = true; // whether the previous token was an operator

		for (String s = tok.next(); s != null; s = tok.next()) {
			switch (s.charAt(0)) {
				case '-':
					if (wasOp) s = "~"; // unary
					wasOp = false; // avoid errors below
				case '+':
				case '*':
				case '/':
				case '%':
				case '^':
					if (wasOp) throw new EvalException("invalid operator position");
					while (!ops.isEmpty()) {
						String op = ops.peek();
						if (!compareOp(s, op)) {
							break;
						}
						sb.append(" " + ops.pop());
					}
					ops.push(s);

					wasOp = true;
					break;
				case '(':
					if (!wasOp) throw new EvalException("invalid operator position");
					ops.push(s);

					wasOp = true;
					break;
				case ')':
					if (wasOp) throw new EvalException("invalid operator position");
					while (!ops.empty() && !ops.peek().equals("(")) {
						sb.append(" " + ops.pop());
					}
					if (ops.empty()) throw new EvalException("mismatched parenthesis: no start");
					ops.pop();

					wasOp = false;
					break;
				default: // digits
					if (!wasOp) throw new EvalException("invalid digit position");
					sb.append(" " + s);
					wasOp = false;
			}
		}

		if (wasOp) throw new EvalException("trailing operator");

		while (!ops.isEmpty()) {
			String op = ops.pop();
			if (op.equals("(")) throw new EvalException("mismatched parenthesis: no end");
			sb.append(" " + op);
		}

		if (sb.length() == 0) throw new EvalException("empty expression");
		return sb.substring(1);
	}

	private static long evaluate(String in) throws EvalException {
		Stack<Long> nums = new Stack<>();

		try {
			for (String s : in.split(" ")) {
				long a, b;
				switch (s.charAt(0)) {
					case '^':
						b = nums.pop().longValue();
						a = nums.pop().longValue();
						if (a == 0 && b < 0) throw new EvalException("divide by zero");
						nums.push((long) Math.pow(a, b));
						break;
					case '~':
						a = nums.pop().longValue();
						nums.push(-a);
						break;
					case '*':
						b = nums.pop().longValue();
						a = nums.pop().longValue();
						nums.push(a * b);
						break;
					case '/':
						b = nums.pop().longValue();
						a = nums.pop().longValue();
						if (b == 0) throw new EvalException("divide by zero");
						nums.push(a / b);
						break;
					case '%':
						b = nums.pop().longValue();
						a = nums.pop().longValue();
						if (b == 0) throw new EvalException("divide by zero");
						nums.push(a % b);
						break;
					case '+':
						b = nums.pop().longValue();
						a = nums.pop().longValue();
						nums.push(a + b);
						break;
					case '-':
						b = nums.pop().longValue();
						a = nums.pop().longValue();
						nums.push(a - b);
						break;
					default: // digits
						nums.push(Long.parseLong(s));
				}
			}

			long ans = nums.pop();
			if (!nums.isEmpty()) throw new EvalException("stack not empty");
			return ans;
		} catch (RuntimeException e) { // This should be a EmptyStackException, but we're not allowed to import that, so I catch the superclass instead.
			throw new EvalException("mismatched operators");
		}
	}

	// Streaming token decoder.
	private static class Tokenizer {
		private final String in;
		private int ind;

		private Tokenizer(String in) {
			this.in = in;
			this.ind = 0;
		}

		// Retrieve the next token, returns null after last token.
		private String next() throws EvalException {
			// Skip whitespace
			while (ind < in.length()) {
				char c = in.charAt(ind);
				if (c != ' ' && c != '\t') break;
				ind++;
			}
			if (ind >= in.length()) return null;

			char c = in.charAt(ind++);

			// Digits
			if (c >= '0' && c <= '9') {
				int start = ind-1;
				while (ind < in.length()) {
					char cn = in.charAt(ind);
					if (cn < '0' || cn > '9') break;
					ind++;
				}

				if (ind-start > 1 && in.charAt(start) == '0') throw new EvalException("invalid leading zero");
				return in.substring(start, ind);
			}

			switch (c) {
				case '+':
				case '-':
				case '*':
				case '/':
				case '%':
				case '^':
				case ')':
				case '(':
					return in.substring(ind-1, ind);
				default:
					throw new EvalException("invalid input: '" + c + "' at " + (ind-1));
			}
		}
	}

	// Generic error for errors while evaluating.
	private static class EvalException extends Exception {
		private EvalException(String msg) {
			super(DEBUG ? msg : "ERROR");
		}
	}
}
