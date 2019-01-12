// Yongun Seong

import java.util.*;
import java.util.stream.*;

public class MyMatrix extends Tensor {
	private List<MyVector> value, trans;

	private MyMatrix() {}

	public MyMatrix(int[][] input) {
		this.value = Arrays.stream(input)
			.map(MyVector::new)
			.collect(Collectors.toList());
		this.trans = MyMatrix.listTrans(this.value);
	}

	public MyMatrix(MyVector... input) {
		this.value = Arrays.asList(input);
		this.trans = MyMatrix.listTrans(this.value);
	}

	public MyMatrix transpose() {
		MyMatrix mat = new MyMatrix();
		mat.value = trans;
		mat.trans = value;
		return mat;
	} 

	private static List<MyVector> listTrans(List<MyVector> mat) {
		int width = mat.isEmpty() ? 0 : mat.get(0).length();
		return IntStream.range(0, width)
			.mapToObj(i -> mat.stream()
				.map(l -> l.get(i)))
			.map(s -> s.toArray(MyScalar[]::new))
			.map(MyVector::new)
			.collect(Collectors.toList());
	}

	public int height() {
		return this.value.size();
	}

	public int width() {
		return this.trans.size();
	}

	public MyVector row(int i) {
		return this.value.get(i);
	}

	public MyVector col(int j) {
		return new MyVector(
			this.value.stream()
			.map(v -> v.get(j))
			.toArray(MyScalar[]::new));
	}

	public MyScalar get(int i, int j) {
		return this.value.get(i).get(j);
	}

	@Override
	public Tensor add(Tensor t) {
		if (t instanceof MyMatrix) {
			MyMatrix mat = (MyMatrix) t;
			if (mat.height() != this.height() || mat.width() != this.width())
				return null;
			Iterator<MyVector> vec = mat.value.iterator();
			return new MyMatrix(
				this.value.stream()
				.map(v -> vec.next().add(v))
				.toArray(MyVector[]::new));
		} else if (t instanceof MyVector) {
			MyVector vec = (MyVector) t;
			if (vec.length() != this.width()) return null;
			return new MyMatrix(
				this.value.stream()
				.map(t::add)
				.toArray(MyVector[]::new));
		} else if (t instanceof MyScalar) {
			return new MyMatrix(
				this.value.stream()
				.map(t::add)
				.toArray(MyVector[]::new));
		}
		return null;
	}

	@Override
	public Tensor multiply(Tensor t) {
		if (t instanceof MyMatrix) {
			// Because there are no loops in my code, time complexity is O(1)!!!
			MyMatrix mat = (MyMatrix) t;
			if (mat.height() != this.width()) return null;
			return new MyMatrix(
				IntStream.range(0, this.height())
				.mapToObj(i ->
					IntStream.range(0, mat.width())
					.mapToObj(j ->
						IntStream.range(0, this.width())
						.mapToObj(k -> this.get(i, k).multiply(mat.get(k, j)))
						.reduce(new MyScalar(0), (a, b) -> (MyScalar) a.add(b))))
				.map(s -> s.toArray(MyScalar[]::new))
				.map(MyVector::new)
				.toArray(MyVector[]::new));
		} else if (t instanceof MyVector) {
			MyVector vec = (MyVector) t;
			if (vec.length() != this.width()) return null;
			MyMatrix mat = (MyMatrix) this.multiply(new MyMatrix(vec).transpose());
			return mat.col(0);
		} else if (t instanceof MyScalar) {
			return new MyMatrix(
				this.value.stream()
				.map(t::multiply)
				.toArray(MyVector[]::new));
		}
		return null;
	}

	@Override
	public String toString() {
		return this.value.stream()
			.map(MyVector::toString)
			.map(s -> s.substring(7))
			.collect(Collectors.joining("\n", "Matrix\n", ""));
	}
}
