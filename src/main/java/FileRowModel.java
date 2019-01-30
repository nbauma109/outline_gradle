import java.io.File;
import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;

import org.netbeans.swing.outline.RowModel;

public class FileRowModel implements RowModel {
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return Date.class;
		case 1:
			return Long.class;
		default:
			assert false;
		}
		return null;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Date" : "Size";
	}

	@Override
	public Object getValueFor(Object node, int column) {
		File f = (File) ((DefaultMutableTreeNode) node).getUserObject();
		switch (column) {
		case 0:
			return new Date(f.lastModified());
		case 1:
			return computeSize(f);
		default:
			assert false;
		}
		return null;
	}

	private Long computeSize(File f) {
		if (f.isFile()) {
			return new Long(f.length());
		}
		long size = 0;
		File[] listOfFiles = f.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				size += computeSize(file);
			}
		}
		return size;
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}

	@Override
	public void setValueFor(Object node, int column, Object value) {
		// do nothing for now
	}
}