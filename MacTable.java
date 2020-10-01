package pack;

import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

public class MacTable implements Runnable {

	// map that stores all actual MAC Table Entries
	private ConcurrentHashMap<String, MacTableEntry> hm = new ConcurrentHashMap<String, MacTableEntry>();
	// time to live for MAC Table Entries
	private Integer maxDelay = 10000;

	private MacTableEntry mte;

	// Thread that deletes old values from MAC Table each second
	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			deleteOldValues(System.currentTimeMillis());
		}
	}

	public void start() {
		Thread t = new Thread(this, "MAC");
		t.start();
	}

	public synchronized void actualize(String srcMac, int port) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (maxDelay == 0) {
					return;
				}
				if (hm.containsKey(srcMac)) {
					if ((hm.get(srcMac)).port==port) {
						hm.get(srcMac).time = System.currentTimeMillis();
						MainWindow.dtmMac.setRowCount(0);
						for (MacTableEntry mte : hm.values()) {
							MainWindow.dtmMac.addRow(new Object[] {
									mte.MacAddress,
									getPort(mte.port),
									(maxDelay - System.currentTimeMillis() + mte.time) / 1000 });
						}
					} else {
						SwitchClass.delete=true;
						hm.clear();
						MainWindow.dtmMac.setRowCount(0);
						hm.put(srcMac,
								(mte = new MacTableEntry(srcMac, System
										.currentTimeMillis(), port)));
						MainWindow.dtmMac.addRow(new Object[] {
								mte.MacAddress,
								getPort(mte.port),
								(maxDelay - System.currentTimeMillis() + mte.time) / 1000 });
					}

				} else {
					hm.put(srcMac,
							(mte = new MacTableEntry(srcMac, System
									.currentTimeMillis(), port)));
					MainWindow.dtmMac.addRow(new Object[] {
							mte.MacAddress,
							getPort(mte.port),
							(maxDelay - System.currentTimeMillis() + mte.time) / 1000 });
				}
			}
		});
	}

	public synchronized int getDestPort(String dstMac) {
		if (hm.containsKey(dstMac)) {
			return hm.get(dstMac).port;
		} else {
			return 0;
		}
	}

	public synchronized void deleteOldValues(long now) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (MacTableEntry mte : hm.values()) {
					if (now - mte.time > maxDelay) {
						hm.remove(mte.MacAddress);
					}
				}
				MainWindow.dtmMac.setRowCount(0);
				for (MacTableEntry mte : hm.values()) {
					MainWindow.dtmMac.addRow(new Object[] {
							mte.MacAddress,
							getPort(mte.port),
							(maxDelay - System.currentTimeMillis() + mte.time) / 1000 });
				}
			}
		});
	}

	public synchronized String getPort(int port) {
		if (port==1) {
			return "Port 1";
		} else
			return "Port 2";
	}

	public synchronized void clearhm() {
		hm.clear();	
	}

	public synchronized void setMaxDelay(int i) {
		maxDelay = i;	
	}

}
