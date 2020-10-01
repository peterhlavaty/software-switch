package pack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;

public class PortListener implements Runnable {

	private PcapNetworkInterface Int1;
	private PacketCollector packetCollector;
	private ExecutorService pool = Executors.newCachedThreadPool();
	private PcapHandle ph1 = null;
	private int port;

	public PortListener(PcapNetworkInterface Int1, PacketCollector pc,int p) {
		this.Int1 = Int1;
		this.packetCollector = pc;
		this.port=p;
		start();
	}

	PacketListener listener = new PacketListener() {
		@Override
		public void gotPacket(Packet packet) {
			packetCollector.addPacket(new ReceivedPacket(port,packet));
		}
	};

	public void run() {
		try {
			ph1 = Int1.openLive(64*1024, PromiscuousMode.PROMISCUOUS, 10);
		} catch (PcapNativeException e1) {
			e1.printStackTrace();
		}
		try {
			ph1.loop(-1, listener,pool);
		} catch (PcapNativeException | InterruptedException | NotOpenException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

}
