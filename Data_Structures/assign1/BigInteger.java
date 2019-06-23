import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigInteger {
	private static final String QUIT_COMMAND = "quit";
	private static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

	/**
	 * Regular expression for parsing inputs.
	 * Matches entire line, groups are aSign, aNum, operator, bSign, bNum.
	 */
	private static final Pattern EXPRESSION_PATTERN = Pattern.compile("^ *(\\+|-)? *(\\d+) *(\\+|-|\\*) *(\\+|-)? *(\\d+) *$");

	private boolean sign; // True if negative
	private int[] nums;   // Least-significant digits first

	/**
	 * Construct a BigInteger with default values.
	 * This constructs an unusable BigInteger.
	 */
	private BigInteger() {
		this.sign = false;
		this.nums = null;
	}

	/**
	 * Construct a BigInteger with the given values.
	 *
	 * @param s    input string, only digits allowed.
	 * @param sign sign of the number, true is negative
	 */
	public BigInteger(String s, boolean sign) {
		this.nums = new int[s.length()];
		for (int i = 0; i < s.length(); i++) {
			nums[nums.length - i - 1] = (int) (s.charAt(i) - '0');
		}
		this.sign = sign;
	}

	/**
	 * Returns true only if the absolute value of this BigInteger is greater
	 * than the other BigInteger.
	 *
	 * @param other the other BigInteger to compare with.
	 * @return true if this is greater than other.
	 */
	private boolean compare(BigInteger other) {
		if (this.nums.length != other.nums.length)
			return this.nums.length > other.nums.length;

		for (int i = this.nums.length-1; i >= 0; i--)
			if (this.nums[i] != other.nums[i])
				return this.nums[i] > other.nums[i];
		return false;
	}

	/**
	 * Return a padded copy of this number.
	 * If this method returns, the returned value will be exactly the given
	 * length. Does not allocate any new memory if already of the given
	 * length.
	 *
	 * @param length the length to pad this number to.
	 * @throws IllegalArgumentException if the length is smaller than the current length
	 * @return this number, padded to the given lenght.
	 */
	private BigInteger pad(int length) {
		if (this.nums.length > length)
			throw new IllegalArgumentException("padding to a smaller length");
		else if (this.nums.length == length)
			return this;

		BigInteger padded = new BigInteger();
		padded.sign = this.sign;
		padded.nums = new int[length];
		for (int i = 0; i < this.nums.length; i++)
			padded.nums[i] = this.nums[i];
		return padded;
	}

	/**
	 * Return a copy of this number multiplied by -1.
	 * Does cause allocation for the digits.
	 *
	 * @return this number multiplied by -1.
	 */
	private BigInteger inverse() {
		BigInteger inverse = new BigInteger();
		inverse.sign = !this.sign;
		inverse.nums = this.nums;
		return inverse;
	}

	/**
	 * Add this number to other.
	 */
	public BigInteger add(BigInteger other) {
		// Only handle cases when the signs match.
		if (this.sign != other.sign)
			return this.subtract(other.inverse());

		// Pad numbers to avoid errors with array indexes.
		BigInteger a = this, b = other;
		if (this.compare(other))
			b = other.pad(this.nums.length);
		else
			a = this.pad(other.nums.length);

		// Perform addition digit-wise.
		int carry = 0;
		int[] sum = new int[a.nums.length+1];
		for (int i = 0; i < a.nums.length; i++) {
			int s = a.nums[i] + b.nums[i] + carry;
			sum[i] = s%10;
			carry = s/10;
		}
		sum[a.nums.length] = carry; // Add carry.

		BigInteger out = new BigInteger();
		out.sign = a.sign;
		out.nums = sum;
		return out;
	}

	/**
	 * Subtract other from this number.
	 */
	public BigInteger subtract(BigInteger other) {
		// Only handle cases when the signs match.
		if (this.sign != other.sign)
			return this.add(other.inverse());

		// Pad numbers, and make sure a is greater than b.
		boolean flip = false;
		BigInteger a, b;
		if (this.compare(other)) {
			a = this;
			b = other.pad(this.nums.length);
		} else {
			a = other;
			b = this.pad(other.nums.length);
			flip = true;
		}

		// Perform subtraction.
		// Since |a| > |b|, no need to handle final borrow.
		boolean borrow = false;
		int[] diff = new int[a.nums.length];
		for (int i = 0; i < a.nums.length; i++) {
			int d = a.nums[i] - b.nums[i] - (borrow ? 1 : 0);
			if (d < 0) {
				d += 10;
				borrow = true;
			} else {
				borrow = false;
			}
			diff[i] = d;
		}

		BigInteger out = new BigInteger();
		out.sign = a.sign;
		out.nums = diff;
		// Flip sign if we changed the subtraction order.
		if (flip) out = out.inverse();
		return out;
	}

	/**
	 * Multiply this number with other.
	 */
	public BigInteger multiply(BigInteger other) {
		int[] prod = new int[this.nums.length + other.nums.length];
		BigInteger a = this, b = other;

		for (int i = 0; i < b.nums.length; i++) {
			int carry = 0;
			// Add the multiplied value of each digit to the correct
			// position.
			for (int j = 0; j < a.nums.length; j++) {
				prod[i+j] += a.nums[j] * b.nums[i] + carry;
				carry = prod[i+j]/10;
				prod[i+j] %= 10;
			}

			// Add carried.
			for (int j = a.nums.length; carry != 0; j++) {
				prod[i+j] += carry;
				carry = prod[i+j]/10;
				prod[i+j] %= 10;
			}
		}

		BigInteger out = new BigInteger();
		out.sign = this.sign ^ other.sign;
		out.nums = prod;
		return out;
	}

	@Override
	public String toString() {
		int length = this.nums.length;

		// Skip leading zeroes
		while (length > 0 && this.nums[length-1] == 0) length--;
		if (length == 0) return "0";

		StringBuilder sb = new StringBuilder();
		if (this.sign) sb.append('-');
		while (length > 0) {
			length--;
			sb.append((char) (this.nums[length] + '0'));
		}
		return sb.toString();
	}

	/**
	 * Evaluate the given expression.
	 *
	 * @throws IllegalArgumentException if the input is of some invalid format.
	 */
	static BigInteger evaluate(String input) {
		Matcher match = EXPRESSION_PATTERN.matcher(input);
		if (!match.matches())
			throw new IllegalArgumentException("invalid expression");

		// Parse out groups
		boolean asign = match.group(1) != null && match.group(1).equals("-");
		String anum = match.group(2);
		boolean bsign = match.group(4) != null && match.group(4).equals("-");
		String bnum = match.group(5);

		BigInteger a = new BigInteger(anum, asign);
		BigInteger b = new BigInteger(bnum, bsign);
		String oper = match.group(3);

		switch (oper) {
			case "+":
				return a.add(b);
			case "-":
				return a.subtract(b);
			case "*":
				return a.multiply(b);
			default:
				throw new IllegalArgumentException("invalid operation");
		}
	}

	public static void main(String[] args) throws Exception {
		try (InputStreamReader isr = new InputStreamReader(System.in)) {
			try (BufferedReader reader = new BufferedReader(isr)) {
				boolean done = false;
				while (!done) {
					String input = reader.readLine();

					try {
						done = processInput(input);
					} catch (IllegalArgumentException e) {
						System.err.println(MSG_INVALID_INPUT);
					}
				}
			}
		}
	}

	/**
	 * Test if the given string is quit, otherwise evaluate.
	 *
	 * @return true if the given string was a quit command.
	 */
	private static boolean processInput(String input) {
		if (input.equalsIgnoreCase(QUIT_COMMAND)) return true;

		BigInteger result = evaluate(input);
		System.out.println(result.toString());
		return false;
	}
}
