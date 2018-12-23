// Yongun Seong

import java.util.*;
import java.util.stream.*;

public class MyVector extends Tensor {
	private List<MyScalar> value;

	public MyVector(int[] input) {
		this.value = Arrays.stream(input)
			.mapToObj(MyScalar::new)
			.collect(Collectors.toList());
	}

	public MyVector(MyScalar... input) {
		this.value = Arrays.asList(input);
	}

	public int length() {
		return this.value.size();
	}

	public MyScalar get(int i) {
		return this.value.get(i);
	}

	public boolean permuteCompare(Tensor t) {
		if (!(t instanceof MyVector)) return false;

		MyVector vec = (MyVector) t;
		Set<MyScalar> a = new HashSet<>(this.value);
		Set<MyScalar> b = new HashSet<>(vec.value);

		return a.equals(b);
	}

	@Override
	public Tensor add(Tensor t) {
		if (t instanceof MyVector) {
			MyVector vec = (MyVector) t;
			if (vec.length() != this.length()) return null;
			Iterator<MyScalar> scl = vec.value.iterator();
			return new MyVector(
				this.value.stream()
				.map(v -> scl.next().add(v))
				.toArray(MyScalar[]::new));
		} else if (t instanceof MyScalar) {
			return new MyVector(
				this.value.stream()
				.map(t::add)
				.toArray(MyScalar[]::new));
		}
		return t.add(this);
	}

	@Override
	public Tensor multiply(Tensor t) {
		if (t instanceof MyMatrix) {
			Tensor m = new MyMatrix(this).multiply(t);
			if (m != null) return ((MyMatrix) m).row(0);
		} else if (t instanceof MyVector) {
			MyVector vec = (MyVector) t;
			if (vec.length() != this.length()) return null;
			Iterator<MyScalar> scl = vec.value.iterator();
			return new MyScalar(
				this.value.stream()
				.map(v -> scl.next().multiply(v))
				.map(MyScalar.class::cast)
				.mapToInt(MyScalar::get)
				.sum());
		} else if (t instanceof MyScalar) {
			return new MyVector(
				this.value.stream()
				.map(t::multiply)
				.toArray(MyScalar[]::new));
		}
		return null;
	}

	@Override
	public String toString() {
		return this.value.stream()
			.mapToInt(MyScalar::get)
			.mapToObj(Integer::toString)
			.collect(Collectors.joining(" ", "Vector ", ""));
	}
}
