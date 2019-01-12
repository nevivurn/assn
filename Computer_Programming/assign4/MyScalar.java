// Yongun Seong

public class MyScalar extends Tensor {
	private int value;

	public MyScalar(int input) {
		this.value = input;
	}

	public int get() {
		return this.value;
	}

	@Override
	public Tensor add(Tensor t) {
		if (t instanceof MyScalar) {
			MyScalar scl = (MyScalar) t;
			return new MyScalar(this.value + scl.value);
		}
		return t.add(this);
	}

	@Override
	public Tensor multiply(Tensor t) {
		if (t instanceof MyScalar) {
			MyScalar scl = (MyScalar) t;
			return new MyScalar(this.value * scl.value);
		}
		return t.multiply(this);
	}

	@Override
	public String toString() {
		return "Scalar " + this.value;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MyScalar)) return false;
		return ((MyScalar) o).value == this.value;
	}

	@Override
	public int hashCode() {
		return this.value;
	}
}
