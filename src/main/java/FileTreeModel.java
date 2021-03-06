import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class FileTreeModel extends DefaultTreeModel implements TreeModel {

	private static final long serialVersionUID = 1L;

	public FileTreeModel(File root) {
		super(buildNode(root));
	}

	private static DefaultMutableTreeNode buildNode(File file) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
		if (file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			if (listOfFiles != null) {
				for (File f : listOfFiles) {
					node.add(buildNode(f));
				}
			}
		}
		return node;
	}

	@Override
	public DefaultMutableTreeNode getRoot() {
		return (DefaultMutableTreeNode) super.getRoot();
	}

	public List<TreeNode> findNodesMatchingPattern(Pattern pattern) {
		List<TreeNode> matchingNodes = new ArrayList<>();
		findNodesWithpattern(matchingNodes, getRoot(), pattern);
		return matchingNodes;
	}

	private void findNodesWithpattern(List<TreeNode> matchingNodes, TreeNode node, Pattern pattern) {
		String fileName = ((File) ((DefaultMutableTreeNode) node).getUserObject()).getName();
		if (pattern.matcher(fileName).find()) {
			matchingNodes.add(node);
		}
		Enumeration<DefaultMutableTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			findNodesWithpattern(matchingNodes, children.nextElement(), pattern);
		}
	}

}