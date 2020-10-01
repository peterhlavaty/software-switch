package pack;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PacketCollector {
	
	private BlockingQueue<ReceivedPacket> receivedPackets = new LinkedBlockingQueue<ReceivedPacket>();
	
	public void addPacket(ReceivedPacket rp){
		receivedPackets.add(rp);
	}

	public ReceivedPacket get() {
		try {
			return receivedPackets.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void clear(){
		receivedPackets.clear();
	}
}
