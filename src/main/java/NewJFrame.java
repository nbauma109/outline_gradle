import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

public class NewJFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public NewJFrame() {
		JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			FileTreeModel treeMdl = new FileTreeModel(fileChooser.getSelectedFile());
			OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, new FileRowModel(), true, "File System");
			Outline outline = new Outline();
			RenderData renderData = new RenderData();
			outline.setRenderDataProvider(renderData);
			outline.setRootVisible(false);
			outline.setModel(mdl);
			JScrollPane jScrollPane1 = new JScrollPane(outline);
			jScrollPane1.setViewportView(outline);
			JMenuBar mb = new JMenuBar();
			JLabel filterLabel = new JLabel("  Search :  ");
			mb.add(filterLabel);
			JTextField filterTextField = new JTextField();
			filterLabel.setLabelFor(filterTextField);
			mb.add(filterTextField);
			setJMenuBar(mb);
			getContentPane().add(jScrollPane1);
			pack();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);

			filterTextField.addKeyListener(new KeyAdapter() {

				private int nthMatch = 0;

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (e.isShiftDown()) {
							nthMatch--;
						} else {
							nthMatch++;
						}
					}
					List<TreeNode> matchingNodes = treeMdl.findNodesWithText(filterTextField.getText());
					if (matchingNodes.size() > 0) {
						if (nthMatch >= matchingNodes.size()) {
							nthMatch = 0;
						}
						TreeNode matchingNode = matchingNodes.get(nthMatch);
						renderData.setCurrentMatch(matchingNode);
						TreePath matchingNodePath = new TreePath(((DefaultMutableTreeNode) matchingNode).getPath());
						outline.expandPath(matchingNodePath);
						outline.scrollRectToVisible(outline.getPathBounds(matchingNodePath));
						renderData.setFilter(filterTextField.getText());
					} 
					outline.repaint();
				}

			});
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new NewJFrame();
			}
		});

	}
}
