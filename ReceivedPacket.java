package pack;

import org.pcap4j.packet.Packet;

public class ReceivedPacket {
	public final int port;
    public final Packet packet;

    protected ReceivedPacket(int port, Packet packet2) {
        this.port = port;
        this.packet = packet2;
    }
}
