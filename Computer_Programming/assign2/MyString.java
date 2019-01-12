// Yongun Seong

import java.util.Arrays;

public class MyString {
	char[] arr;

	public MyString(char[] arr) {
		this.arr = Arrays.copyOf(arr, arr.length);
	}

	public char[] toCharArray() {
		return Arrays.copyOf(this.arr, this.length());
	}

	public boolean equals(MyString str) {
		if (str == null) return false;
		return Arrays.equals(this.arr, str.arr);
	}

	public boolean equalsIgnoreCase(MyString str) {
		if (str == null) return false;
		return this.toLowerCase().equals(str.toLowerCase());
	}

	public MyString substring(int start) {
		return this.substring(start, this.length());
	}

	public MyString substring(int start, int end) {
		return new MyString(Arrays.copyOfRange(this.arr, start, end));
	}

	public boolean startsWith(MyString str) {
		if (this.length() < str.length()) return false;
		return this.substring(0, str.length()).equals(str);
	}

	public boolean endsWith(MyString str) {
		if (this.length() < str.length()) return false;
		return this.substring(this.length()-str.length()).equals(str);
	}

	public boolean contains(MyString str) {
		return this.indexOf(str) >= 0;
	}

	public int indexOf(MyString str) {
		for (int i = 0; i < this.length()-str.length()+1; i++) {
			if (this.substring(i).startsWith(str)) return i;
		}
		return -1;
	}

	public int length() {
		return this.arr.length;
	}

	public MyString toLowerCase() {
		char[] arr = Arrays.copyOf(this.arr, this.length());
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] >= 'A' && arr[i] <= 'Z') {
				arr[i] -= 'A'-'a';
			}
		}

		return new MyString(arr);
	}

	public MyString toUpperCase() {
		char[] arr = Arrays.copyOf(this.arr, this.length());
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] >= 'a' && arr[i] <= 'z') {
				arr[i] -= 'a'-'A';
			}
		}

		return new MyString(arr);
	}
}
