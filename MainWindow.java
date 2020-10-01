package pack;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;

public class MainWindow {

	public static String port1, port2;
	public static JScrollPane scrollPane;
	private static JFrame frame = new JFrame("Switch");
	public static JTable statsTable;
	public static JTable macTable;
	private static JTextField TTLField;
	private static boolean first = true;
	private static MacTable mt;
	private static LLDP lldp;
	public static DefaultTableModel dtmStats = new DefaultTableModel(
			new String[] { "","Arp", "Icmp", "Ip", "Ip6", "Tcp", "Udp","All" }, 4);
	public static DefaultTableModel dtmMac = new DefaultTableModel(
			new String[] { "Mac Address", "Port", "Time To Live (s)" }, 0);
	public static DefaultTableModel dtmLLDP = new DefaultTableModel(
			new String[] { "Remote Hostname", "Remote Port", "Time To Live (s)", "Local Hostname", "Local Port" },
			0);
	private static JTable LLDPTable;

	public MainWindow(MacTable mt, LLDP lldp) {
		MainWindow.mt=mt;
		MainWindow.lldp=lldp;
		start();
	}

	private void start() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow.frame.setVisible(true);
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private static void initialize() {
		frame.setResizable(false);
		frame.setBounds(70, 8, 1219, 710);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblMacTable = new JLabel("Mac Table");
		lblMacTable.setFont(new Font("Arial", Font.BOLD, 14));
		lblMacTable.setBounds(571, 203, 71, 17);
		frame.getContentPane().add(lblMacTable);

		JLabel lblStatistics = new JLabel("Statistics");
		lblStatistics.setFont(new Font("Arial", Font.BOLD, 14));
		lblStatistics.setBounds(574, 94, 64, 14);
		frame.getContentPane().add(lblStatistics);

		JButton btnClearStatistics = new JButton("Clear Statistics");
		btnClearStatistics.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
				SwitchClass.clear();
			}
		});
		btnClearStatistics.setBounds(869, 11, 154, 25);
		frame.getContentPane().add(btnClearStatistics);

		JButton btnClearMacTable = new JButton("Clear Mac Table");
		btnClearMacTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dtmMac.setRowCount(0);
				mt.clearhm();
			}
		});
		btnClearMacTable.setBounds(528, 11, 154, 25);
		frame.getContentPane().add(btnClearMacTable);

		JButton btnFilters = new JButton("Filters");
		btnFilters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (first) {
					SwingUtilities.invokeLater(new Runnable(){@Override
					public void run(){
						FilterWindow.start();
					}});
					first = false;
				} else {
					SwingUtilities.invokeLater(new Runnable(){@Override
					public void run(){
						FilterWindow.frame.setVisible(true);
					}});
				}
			}
		});
		btnFilters.setBounds(187, 11, 154, 25);
		frame.getContentPane().add(btnFilters);

		statsTable = new JTable(dtmStats) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		statsTable.setBounds(10, 159, 1064, 174);
		statsTable.setAutoCreateColumnsFromModel(true);
		frame.getContentPane().add(statsTable);

		JCheckBox chckbxReceive = new JCheckBox("Receive Packets");
		chckbxReceive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwitchClass.CheckB();
			}
		});
		chckbxReceive.setSelected(true);
		chckbxReceive.setBounds(1066, 68, 134, 17);
		frame.getContentPane().add(chckbxReceive);

		macTable = new JTable(dtmMac) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};

		macTable.setBounds(10, 376, 1064, 174);
		macTable.setAutoCreateColumnsFromModel(true);
		frame.getContentPane().add(macTable);

		TTLField = new JTextField();
		TTLField.setToolTipText("Time to live for mac table entries in seconds.");
		TTLField.setText("10");
		TTLField.setBounds(108, 57, 51, 20);
		frame.getContentPane().add(TTLField);
		TTLField.setColumns(10);

		JButton btnSetTimeTo = new JButton("Set Time To Live");
		btnSetTimeTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TTLField.getText() != null) {
					if (TTLField.getText().toString().trim().length() != 0) {
						if (TTLField.getText().toString().matches("[0-9]{1,9}")){
							mt.setMaxDelay(Integer.parseInt(TTLField.getText()) * 1000);
						}
					}
				} 
			}
		});
		btnSetTimeTo.setBounds(169, 57, 130, 20);
		frame.getContentPane().add(btnSetTimeTo);

		JLabel lblTimeToLive = new JLabel("Time To Live:");
		lblTimeToLive.setFont(new Font("Arial", Font.PLAIN, 14));
		lblTimeToLive.setBounds(13, 60, 87, 14);
		frame.getContentPane().add(lblTimeToLive);

		scrollPane = new JScrollPane(statsTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 108, 1193, 86);
		frame.getContentPane().add(scrollPane);

		JScrollPane scrollPane_1 = new JScrollPane(macTable);
		scrollPane_1.setBounds(10, 218, 1193, 191);
		frame.getContentPane().add(scrollPane_1);

		JLabel lblPort = new JLabel(port1);
		lblPort.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPort.setBounds(13, 643, 1190, 14);
		frame.getContentPane().add(lblPort);

		JLabel lblPort_1 = new JLabel(port2);
		lblPort_1.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPort_1.setBounds(13, 661, 1190, 14);
		frame.getContentPane().add(lblPort_1);

		JLabel LLDP = new JLabel("LLDP");
		LLDP.setFont(new Font("Arial", Font.BOLD, 14));
		LLDP.setBounds(589, 419, 35, 17);
		frame.getContentPane().add(LLDP);

		LLDPTable = new JTable(dtmLLDP) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		LLDPTable.setBounds(13, 493, 1190, 144);
		frame.getContentPane().add(LLDPTable);

		JScrollPane scrollPane_2 = new JScrollPane(LLDPTable);
		scrollPane_2.setBounds(10, 435, 1194, 191);
		frame.getContentPane().add(scrollPane_2);
		
		JCheckBox chckbxEnableLldp = new JCheckBox("Enable LLDP");
		chckbxEnableLldp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwitchClass.CheckLLDPB();
			}
		});
		chckbxEnableLldp.setSelected(true);
		chckbxEnableLldp.setBounds(1066, 44, 97, 17);
		frame.getContentPane().add(chckbxEnableLldp);
		
		mt.start();
		lldp.start();
	}

	protected static synchronized void clear() {
		for (int i = 0; i < 7; i++) {
			dtmStats.setValueAt(0, 0, i);
		}
	}

	public static synchronized void updateStats(Object[] o1, Object[] o2, Object[] o3, Object[] o4) {
		SwingUtilities.invokeLater(new Runnable(){@Override
		public void run(){
			for (int i = 0; i < 8; i++) {
				dtmStats.setValueAt(o1[i], 0, i);
				dtmStats.setValueAt(o2[i], 1, i);
				dtmStats.setValueAt(o3[i], 2, i);
				dtmStats.setValueAt(o4[i], 3, i);
			}
		}});
	}
	
}
