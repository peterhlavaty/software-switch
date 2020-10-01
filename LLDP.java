package pack;

import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

public class LLDP implements Runnable {

	private ConcurrentHashMap<String, LLDPTableEntry> lldpHM = new ConcurrentHashMap<String, LLDPTableEntry>();
	public synchronized void update(LLDPTableEntry lte) {
		if ((lldpHM.containsKey(lte.chassis))
				&& (lldpHM.get(lte.chassis).port.equals(lte.port))
				&& (lldpHM.get(lte.chassis).receivedOn.equals(lte.receivedOn))
				&& (lldpHM.get(lte.chassis).switchName.equals(lte.switchName))) {
			lldpHM.get(lte.chassis).ttl = lte.ttl;
			lldpHM.get(lte.chassis).added = lte.added;
		} else {
			lldpHM.put(lte.chassis, lte);
		}
	}

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
		Thread t = new Thread(this);
		t.start();
	}
	

	public synchronized void deleteOldValues(long now) {
		SwingUtilities.invokeLater(new Runnable(){@Override
		public void run(){
			for (LLDPTableEntry lte : lldpHM.values()) {
				if (now - lte.added > lte.ttl) {
					lldpHM.remove(lte.chassis);
				}
			}
			MainWindow.dtmLLDP.setRowCount(0);
			for (LLDPTableEntry lte : lldpHM.values()) {
				MainWindow.dtmLLDP.addRow(new Object[] { lte.switchName, lte.port,
						(lte.ttl - now + lte.added) / 1000, "SoftwareSwitch",
						lte.receivedOn });
			}
		}});
	}

}
