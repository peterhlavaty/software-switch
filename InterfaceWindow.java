package pack;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InterfaceWindow {

	private static List<PcapNetworkInterface> alldevs;
	private static JFrame frame = new JFrame();
	private static JTable table;
	private static DefaultTableModel dtm = new DefaultTableModel(
			new String[] { "Interface" }, 0);
	private static JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					InterfaceWindow.frame.setVisible(true);
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static synchronized void createTable() {

		try {
			alldevs = Pcaps.findAllDevs();
		} catch (PcapNativeException e) {
			e.printStackTrace();
		}

		int i = 1;
		for (PcapNetworkInterface device : alldevs) {
			String description = (device.getDescription() != null) ? device
					.getDescription() : "No description available";
			dtm.addRow(new Object[] { i++ + ":" + description
					+ device.getName() });
		}
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private static void initialize() {
		frame.setBounds(100, 100, 794, 470);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		table = new JTable(dtm) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		table.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		table.setBounds(31, 21, 375, 209);
		frame.getContentPane().add(table);

		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(32, 36, 724, 360);
		frame.getContentPane().add(scrollPane);

		JLabel lblVyberSietoveKarty = new JLabel("Choose 2 network adapters:");
		lblVyberSietoveKarty.setFont(new Font("Arial", Font.BOLD, 13));
		lblVyberSietoveKarty.setBounds(32, 11, 176, 14);
		frame.getContentPane().add(lblVyberSietoveKarty);

		JButton btnVyber = new JButton("Choose");
		btnVyber.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 2) {
					new SwitchClass(alldevs.get(table.getSelectedRows()[0]),
							alldevs.get(table.getSelectedRows()[1]));
					frame.dispose();
				}
			}
		});
		btnVyber.setBounds(349, 407, 89, 23);
		frame.getContentPane().add(btnVyber);
		createTable();
	}
}
