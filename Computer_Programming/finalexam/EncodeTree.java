// Yongun Seong
// Computer Programming final exam: problem 1
// EncodeTree.java

public class EncodeTree {
	private Node root;

	public EncodeTree(Node root) {
		this.root = root;
	}

	public String encode() {
		return this.encode(this.root);
	}

	public String encode(Node root) {
		if (root == null) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(root.label);
		sb.append(encode(root.left));
		sb.append(encode(root.right));
		sb.append(root.label);
		return sb.toString();
	}
}
