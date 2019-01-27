import java.awt.Color;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.netbeans.swing.outline.RenderDataProvider;

public class RenderData implements RenderDataProvider {

	private String filter;
	private TreeNode currentMatch;

	@Override
	public java.awt.Color getBackground(Object o) {
		return null;
	}

	@Override
	public String getDisplayName(Object o) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
		String fileName = ((File) node.getUserObject()).getName();
		if (filter == null || filter.length() == 0) {
			return fileName;
		}
		try {
			Pattern pattern = Pattern.compile("(" + filter + ")");
			Matcher matcher = pattern.matcher(fileName);
			Color bgColor = Color.YELLOW;
			if (matcher.find()) {
				if (node == currentMatch) {
					bgColor = Color.ORANGE;
				}
			}
			String bgHexColor = Integer.toHexString(bgColor.getRGB() & 0xffffff);
			String replacement = matcher.replaceAll("<span style=\"background-color: #" + bgHexColor + "\">$1</span>");
			return "<html>" + replacement + "</html>";
		} catch (Exception e) {
			return fileName;
		}
	}

	@Override
	public java.awt.Color getForeground(Object o) {
		File f = (File) ((DefaultMutableTreeNode) o).getUserObject();
		if (!f.isDirectory() && !f.canWrite()) {
			return UIManager.getColor("controlShadow");
		}
		return null;
	}

	@Override
	public javax.swing.Icon getIcon(Object o) {
		return null;
	}

	@Override
	public String getTooltipText(Object o) {
		File f = (File) ((DefaultMutableTreeNode) o).getUserObject();
		return f.getAbsolutePath();
	}

	@Override
	public boolean isHtmlDisplayName(Object o) {
		return false;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setCurrentMatch(TreeNode currentMatch) {
		this.currentMatch = currentMatch;
	}
}