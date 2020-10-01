package pack;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JLabel;

public class FilterWindow {

	public static JFrame frame = new JFrame("Filters");
	private static JTable table;
	public static DefaultTableModel dtm = new DefaultTableModel(new String[] {
			"Order", "Filter", "Allow/Deny" }, 0) {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return row == dtm.getRowCount() - 1;/*column == 1 || column == 0
					|| (row == dtm.getRowCount() - 1 && column != 0);*/
		}
	};
	private static final short filternr = 1, ordernr = 0, adnr = 2;
	private static JScrollPane scrollPane;
	private static SortedMap<Integer, Filter> filters = new TreeMap<Integer, Filter>();

	public static synchronized boolean cond(int row, int t) {
		if (dtm.getValueAt(row, t) == null) {
			return false;
		} else if (dtm.getValueAt(row, t).toString().trim().length() == 0) {
			return false;
		}
		return true;
	}

	public static synchronized Filter makeFilter(int row) {
		Filter filter = new Filter();
		if (cond(row, filternr)) {
			filter.regex = dtm.getValueAt(row, filternr).toString();
		} else {
			filter.regex = ".*";
		}
		if (cond2(row, ordernr)) {
			filter.order = Integer.parseInt(dtm.getValueAt(row, ordernr)
					.toString());
		} else {
			int ink = 1;
			while (filters.containsKey(ink)) {
				ink++;
			}
			filter.order = ink;
		}
		if (cond(row, adnr)) {
			if (dtm.getValueAt(row, adnr).toString()
					.matches("^[AaLlOoWw]{1,}$")) {
				filter.allow = true;
			} else {
				filter.allow = false;
			}
		} else {
			filter.allow = true;
		}
		return filter;
	}

	private static boolean cond2(int row, int i) {
		if (dtm.getValueAt(row, i) == null) {
			return false;
		} else if (dtm.getValueAt(row, i).toString().trim().length() == 0) {
			return false;
		} else if (!dtm.getValueAt(row, i).toString().matches("^[:blank:]*[0-9]{1,9}[:blank:]*$")) {
			return false;
		} else if (filters.containsKey(Integer.parseInt(dtm.getValueAt(row, i)
				.toString()))) {
			return false;
		}
		//System.out.print("true");
		return true;
	}

	public synchronized static boolean allow(String frame) {
		for (Filter val : filters.values()) {
			//System.out.print(frame + " - " + val.regex + "\n");
			if (frame.matches(val.regex)) {
				//System.out.print("match\n");
				return val.allow;
			}
		}
		return true;
	}

	/**
	 * Launch the application.
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					FilterWindow.frame.setVisible(true);
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private static void initialize() {
		frame.setBounds(100, 100, 918, 470);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		table = new JTable(dtm) {

			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer tcr, int row,
					int column) {
				Component c = super.prepareRenderer(tcr, row, column);

				if (isRowSelected(row)) {

					c.setForeground(getSelectionForeground());
					c.setBackground(getSelectionBackground());

				} else {

					c.setForeground(getForeground());
					c.setBackground((row % 2 == 0) ? getBackground()
							: Color.lightGray);
				}

				int rendererWidth = c.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(Math.max(rendererWidth
						+ getIntercellSpacing().width,
						tableColumn.getPreferredWidth()));

				DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
				rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);
				table.getColumnModel().getColumn(column)
						.setCellRenderer(rightRenderer);
				return c;
			}

		};
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (e.getClickCount() == 2 && row >= 0 && col >= 0) {
					if ((row != dtm.getRowCount() - 1) && (cond(row, col))) {

						if (col == adnr) {
							if (dtm.getValueAt(row, col).toString()
									.equals("Deny")) {
								dtm.setValueAt("Allow", row, col);
								filters.get(Integer.parseInt(dtm.getValueAt(
										row, ordernr).toString())).allow = true;
							} else {
								dtm.setValueAt("Deny", row, col);
								filters.get(Integer.parseInt(dtm.getValueAt(
										row, ordernr).toString())).allow = false;
							}
						}
					}
				}
			}
		});
		table.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		table.setBounds(31, 21, 375, 209);
		frame.getContentPane().add(table);

		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(36, 32, 839, 364);
		frame.getContentPane().add(scrollPane);

	/*	Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				if (tcl.getOldValue() != tcl.getNewValue()) {
					if (tcl.getRow() != table.getRowCount() - 1) {
						if (cond(tcl.getRow(), tcl.getColumn())) {
							if (tcl.getColumn() == filternr) {
								filters.get(Integer.parseInt(dtm.getValueAt(
										tcl.getRow(), ordernr).toString())).regex = tcl
										.getNewValue().toString();
							}
							if (tcl.getColumn() == ordernr) {
								if (cond2(tcl.getRow(), tcl.getColumn())) {
									filters.get(Integer.parseInt(tcl
											.getOldValue().toString())).order = Integer
											.parseInt(tcl.getNewValue()
													.toString());
								} else {
									int ink = 1;
									while (filters.containsKey(ink)) {
										ink++;
									}
									filters.get(Integer.parseInt(tcl
											.getOldValue().toString())).order = ink;
									dtm.setValueAt(ink, tcl.getRow(),
											tcl.getColumn());
								}
							}
						} else {
							dtm.setValueAt(tcl.getOldValue(), tcl.getRow(),
									tcl.getColumn());
						}
					}
				}
			}
		};*/

		// TableCellListener tcl =
		//new TableCellListener(table, action);

		dtm.addRow(new Object[] { null, null, null });

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = dtm.getRowCount() - 1;
				if (table.getCellEditor() != null) {
					table.getCellEditor().stopCellEditing();
				}
				Filter newfilter = makeFilter(row);
				filters.put(newfilter.order, newfilter);
				dtm.setValueAt(newfilter.order, row, ordernr);
				dtm.setValueAt(newfilter.regex, row, filternr);
				if (newfilter.allow) {
					dtm.setValueAt("Allow", row, adnr);
				} else {
					dtm.setValueAt("Deny", row, adnr);
				}
				dtm.addRow(new Object[] { null, null, null });
			}
		});
		btnAdd.setBounds(244, 407, 89, 23);
		frame.getContentPane().add(btnAdd);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				while ((table.getSelectedRowCount() > 0)
						&& (dtm.getRowCount() > 1)) {
					if (cond(table.getSelectedRows()[0], ordernr)) {
						filters.remove(Integer.parseInt(dtm.getValueAt(
								table.getSelectedRows()[0], ordernr).toString()));
						dtm.removeRow(table.getSelectedRows()[0]);
					} else {
						break;
					}
				}
			}
		});
		btnDelete.setBounds(577, 407, 89, 23);
		frame.getContentPane().add(btnDelete);
		
		JLabel lblFilterPatternSrc = new JLabel("Filter pattern: src mac;dst mac;src ip;dst ip;src port tcp;dst port tcp;src port udp;dst port udp;ip;tcp;udp;arp;icmp;port;direction");
		lblFilterPatternSrc.setBounds(36, 9, 839, 14);
		frame.getContentPane().add(lblFilterPatternSrc);
	}
}
