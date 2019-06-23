public class HashTable<E> {
	private E[] arr;
	
	public HashTable(int size) {
		@SuppressWarnings("unchecked")
		E[] arr = (E[]) new Object[100];
		this.arr = arr;
	}

	public E get(int i) {
		return arr[i];
	}

	// assume no collisions
	public void put(int i, E e) {
		arr[i] = e;
	}
}
