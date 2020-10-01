package pack;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;


public class LLDPSend implements Runnable {
	public static String port1, port2;
	private static Pcap portf1;
	private static Pcap portf2;
	private static JPacket packet1;
	private static JPacket packet2;

	public LLDPSend(String p1, String p2, String pack1, String pack2) {
		port1 = p1;
		port2 = p2;
		packet1 = new JMemoryPacket(JProtocol.IP4_ID,pack1);
		packet2 = new JMemoryPacket(JProtocol.IP4_ID,pack2);
	}

	@Override
	public void run() {
		StringBuilder errbuf = new StringBuilder();
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		portf1 = Pcap.openLive(port1, snaplen, flags, timeout, errbuf);

		if (port1 == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}

		portf2 = Pcap.openLive(port2, snaplen, flags, timeout, errbuf);

		if (port2 == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}
		for (;;) {
			//System.out.print(packet1.toHexdump());
			//System.out.print(packet2.toHexdump());
			if(portf1.sendPacket(packet1)!=Pcap.OK){
				System.err.println(portf1.getErr()); 
			}
			if(portf2.sendPacket(packet2)!=Pcap.OK){
				System.err.println(portf2.getErr()); 
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

}
