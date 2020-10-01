package pack;

public class LLDPTableEntry {
	public LLDPTableEntry(String s, String p, int t, String r, long a,String c) {
		this.chassis = c;
		this.port = p;
		this.ttl = t;
		this.receivedOn = r;
		this.added = a;
		this.switchName = s;
	}

	public String switchName;
	public String chassis;
	public String port;
	public int ttl;
	public String receivedOn;
	public long added;
}
