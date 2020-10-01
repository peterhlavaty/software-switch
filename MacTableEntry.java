package pack;

public class MacTableEntry {
	public MacTableEntry(String macAddress, Long time, int port2) {
		this.MacAddress = macAddress;
		this.time = time;
		this.port = port2;
	}

	public String MacAddress;
	public Long time;
	public int port;

}
