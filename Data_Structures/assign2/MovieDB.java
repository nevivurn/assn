import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
	private MyLinkedList<MovieDBItem> list;

	public MovieDB() {
		this.list = new MyLinkedList<>();
	}

	public void insert(MovieDBItem item) {
		// FIXME implement this
		// Insert the given item to the MovieDB.

		// Printing functionality is provided for the sake of debugging.
		// This code should be removed before submitting your work.
		//System.err.printf("[trace] MovieDB: INSERT [%s] [%s]\n", item.getGenre(), item.getTitle());

		Node<MovieDBItem> cur = this.list.head;

		while (cur.getNext() != null &&
				item.compareTo(cur.getNext().getItem()) > 0) {
			cur = cur.getNext();
		}

		if (cur.getNext() == null ||
				item.compareTo(cur.getNext().getItem()) < 0) {
			cur.insertNext(item);
			this.list.numItems++;
		}
	}

	public void delete(MovieDBItem item) {
		// FIXME implement this
		// Remove the given item from the MovieDB.

		// Printing functionality is provided for the sake of debugging.
		// This code should be removed before submitting your work.
		//System.err.printf("[trace] MovieDB: DELETE [%s] [%s]\n", item.getGenre(), item.getTitle());

		Node<MovieDBItem> cur = this.list.head;

		while (cur.getNext() != null &&
				item.compareTo(cur.getNext().getItem()) > 0) {
			cur = cur.getNext();
		}

		if (cur.getNext() != null && item.compareTo(cur.getNext().getItem()) == 0) {
			cur.removeNext();
			this.list.numItems--;
		}
	}

	public MyLinkedList<MovieDBItem> search(String term) {
		// FIXME implement this
		// Search the given term from the MovieDB.
		// You should return a linked list of MovieDBItem.
		// The search command is handled at SearchCmd class.

		// Printing search results is the responsibility of SearchCmd class. 
		// So you must not use System.out in this method to achieve specs of the assignment.

		// This tracing functionality is provided for the sake of debugging.
		// This code should be removed before submitting your work.
		//System.err.printf("[trace] MovieDB: SEARCH [%s]\n", term);

		// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
		// This code is supplied for avoiding compilation error.   
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		Node<MovieDBItem> cur = this.list.head;
		Node<MovieDBItem> insert = results.head;

		while (cur.getNext() != null) {
			cur = cur.getNext();

			if (cur.getItem().getTitle().contains(term)) {
				insert.insertNext(cur.getItem());
				insert = insert.getNext();
				results.numItems++;
			}
		}

		return results;
	}

	public MyLinkedList<MovieDBItem> items() {
		// FIXME implement this
		// Search the given term from the MovieDatabase.
		// You should return a linked list of QueryResult.
		// The print command is handled at PrintCmd class.

		// Printing movie items is the responsibility of PrintCmd class. 
		// So you must not use System.out in this method to achieve specs of the assignment.

		// Printing functionality is provided for the sake of debugging.
		// This code should be removed before submitting your work.
		//System.err.printf("[trace] MovieDB: ITEMS\n");

		// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
		// This code is supplied for avoiding compilation error.   
		return this.list;
	}
}

class Genre extends Node<String> implements Comparable<Genre> {
	public Genre(String name) {
		super(name);
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public int compareTo(Genre o) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException("not implemented yet");
	}
}

class MovieList extends MyLinkedList<String> {
	@Override
	public void add(String item) {
		Node<String> cur = this.head;

		while (cur.getNext() != null &&
				item.compareTo(cur.getNext().getItem()) > 0) {
			cur = cur.getNext();
		}

		if (cur.getNext() == null ||
				item.compareTo(cur.getNext().getItem()) < 0) {
			cur.insertNext(item);
			this.numItems++;
		}
	}
}
