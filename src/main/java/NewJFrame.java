import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.netbeans.swing.etable.QuickFilter;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

public class NewJFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();

	public NewJFrame() {
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(10000);
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
			JLabel searchPatternLabel = new JLabel("  Search :  ");
			mb.add(searchPatternLabel);
			JTextField searchPatternTextField = new JTextField();
			searchPatternLabel.setLabelFor(searchPatternTextField);
			outline.setQuickFilter(0, new QuickFilter() {

				@Override
				public boolean accept(Object aValue) {
					if (searchPatternTextField.getText() == null || searchPatternTextField.getText().length() == 0) {
						return true;
					}
					if (aValue instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) aValue;
						Enumeration<DefaultMutableTreeNode> children = node.children();
						while (children.hasMoreElements()) {
							DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
							if (accept(child)) {
								return true;
							}
						}
						try {
							String fileName = ((File) ((DefaultMutableTreeNode) node).getUserObject()).getName();
							Pattern searchPattern = Pattern.compile(searchPatternTextField.getText());
							return searchPattern.matcher(fileName).find();
						} catch (PatternSyntaxException ex) {
							return true;
						}
					}
					return false;
				}
			});
			mb.add(searchPatternTextField);
			setJMenuBar(mb);
			getContentPane().add(jScrollPane1);
			pack();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);

			searchPatternTextField.addKeyListener(new KeyAdapter() {

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
					try {
						Pattern searchPattern = Pattern.compile(searchPatternTextField.getText());
						List<TreeNode> matchingNodes = treeMdl.findNodesMatchingPattern(searchPattern);
						if (matchingNodes.size() > 0) {
							if (nthMatch >= matchingNodes.size()) {
								nthMatch = 0;
							}
							TreeNode matchingNode = matchingNodes.get(nthMatch);
							renderData.setCurrentMatch(matchingNode);
							TreePath matchingNodePath = new TreePath(((DefaultMutableTreeNode) matchingNode).getPath());
							outline.expandPath(matchingNodePath);
							outline.scrollRectToVisible(outline.getPathBounds(matchingNodePath));
							renderData.setSearchPattern(searchPatternTextField.getText());
						}
					} catch (PatternSyntaxException ex) {

					}
					outline.repaint();
				}

			});
			this.
			addMouseListener(new MouseAdapter() {

				public void mouseEntered(MouseEvent me) {
					ToolTipManager.sharedInstance().setDismissDelay(60000);
				}

				public void mouseExited(MouseEvent me) {
					ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
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
