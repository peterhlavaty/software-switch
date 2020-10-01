package pack;

import java.util.Arrays;
import java.util.HashSet;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

public class SwitchClass implements Runnable {

	private Packet actpacket;
	private static PcapNetworkInterface Int1;
	private PcapNetworkInterface Int2;
	private static final int offset = 14;
	private static final String packetp1 = "0180c200000e",
			packetp2 = "88cc020704", packetp3 = "040605506f72743",
			packetp4 = "060200780a0e536f6674776172655377697463680000";
	private static String switchName;
	private static String port;
	private static String chassis;
	private static String toFilter;
	private static String protocol;
	private static int ttl;
	private static final LLDP lldp = new LLDP();
	private static int icmpc1i = 0, udpc1i = 0, ipc1i = 0, tcpc1i = 0,
			arpc1i = 0, ip6c1i = 0, all1i = 0, icmpc1o = 0, udpc1o = 0,
			ipc1o = 0, tcpc1o = 0, arpc1o = 0, ip6c1o = 0, all1o = 0,
			icmpc2i = 0, udpc2i = 0, ipc2i = 0, tcpc2i = 0, arpc2i = 0,
			ip6c2i = 0, all2i = 0, icmpc2o = 0, udpc2o = 0, ipc2o = 0,
			tcpc2o = 0, arpc2o = 0, ip6c2o = 0, all2o = 0;
	private static boolean icmpbo1 = false, udpbo1 = false, ipbo1 = false,
			tcpbo1 = false, arpbo1 = false, ip6bo1 = false, icmpbo2 = false,
			udpbo2 = false, ipbo2 = false, tcpbo2 = false, arpbo2 = false,
			ip6bo2 = false, allbo1 = false, allbo2 = false;
	public static int[] devind;
	static MacTable mt;
	public static String HW1;
	public static String HW2;
	public static boolean b = true, lldpb = true;
	public static boolean delete = false;
	public static HashSet<String> hs = new HashSet<String>();

	public SwitchClass(PcapNetworkInterface pcapIf, PcapNetworkInterface pcapIf2) {
		SwitchClass.Int1 = pcapIf;
		this.Int2 = pcapIf2;
		start();
	}

	public static String toHexString(byte[] b) {
		return String.format("%02X:%02X:%02X:%02X:%02X:%02X", b[0], b[1], b[2],
				b[3], b[4], b[5]);
	}

	public static String toHexString2(byte[] b) {
		return String.format("%02X%02X%02X%02X%02X%02X", b[0], b[1], b[2],
				b[3], b[4], b[5]);
	}

	public static String getPort(int port2) {
		if (port2 == 1) {
			return "Port 1";
		} else
			return "Port 2";
	}

	public static String packetString(byte[] b, int port2) {
		String s = "";
		for (int i = 0; i < b.length; i++) {
			s += b[i] + ";";
		}
		if (port2 == 1) {
			return s + "Port1";
		} else {
			return s + "Port2";
		}
	}

	public static String packetString2(byte[] b, int port2) {
		String s = "";
		for (int i = 0; i < b.length; i++) {
			s += b[i] + ";";
		}
		if (port2 == 1) {
			return s + "Port2";
		} else {
			return s + "Port1";
		}
	}

	public static String getPort3(int p) {
		if (p == 1) {
			return "1";
		} else
			return "2";
	}

	public static String getPort4(int p) {
		if (p == 1) {
			return "2";
		} else
			return "1";
	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {

		HW1 = toHexString(Int1.getLinkLayerAddresses().get(0).getAddress());
		HW2 = toHexString(Int2.getLinkLayerAddresses().get(0).getAddress());

		String HW12 = toHexString2(Int1.getLinkLayerAddresses().get(0)
				.getAddress());
		String HW22 = toHexString2(Int1.getLinkLayerAddresses().get(0)
				.getAddress());
		String description = (Int1.getDescription() != null) ? Int1
				.getDescription() : "No description available/";
		MainWindow.port1 = "Port 1: " + description + " " + Int1.getName();
		description = (Int1.getDescription() != null) ? Int2.getDescription()
				: "No description available/";
		MainWindow.port2 = "Port 2: " + description + " " + Int2.getName();

		// StringBuilder errbuf = new StringBuilder();
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		// int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10;

		PcapHandle ph1 = null;
		try {
			ph1 = Int1.openLive(snaplen, PromiscuousMode.PROMISCUOUS, timeout);
		} catch (PcapNativeException e1) {
			e1.printStackTrace();
		}
		PcapHandle ph2 = null;
		try {
			ph2 = Int2.openLive(snaplen, PromiscuousMode.PROMISCUOUS, timeout);
		} catch (PcapNativeException e1) {
			e1.printStackTrace();
		}

		mt = new MacTable();
		new MainWindow(mt, lldp);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		final PacketCollector packetCollector = new PacketCollector();
		new PortListener(Int1, packetCollector, 1);
		new PortListener(Int2, packetCollector, 2);


		new LLDPSend(Int1.getName(), Int2.getName(), packetp1 + HW12 + packetp2
				+ HW12 + packetp3 + "1" + packetp4, packetp1 + HW22 + packetp2
				+ HW22 + packetp3 + "2" + packetp4).start();


		for (;;) {
			try {
				ReceivedPacket receivedPacket = packetCollector.get();
				if (receivedPacket != null) {

					if (hs.contains(packetString(
							receivedPacket.packet.getRawData(),
							receivedPacket.port))) {
						hs.remove(packetString(
								receivedPacket.packet.getRawData(),
								receivedPacket.port));
					} else {

						actpacket = receivedPacket.packet;
						
						byte[] srcMac = new byte[6];
						byte[] destMac = new byte[6];
						srcMac = Arrays.copyOfRange(actpacket.getRawData(), 6,
								12);
						destMac = Arrays.copyOfRange(actpacket.getRawData(), 0,
								6);
						String hexSrcMac = toHexString(srcMac);
						String hexDestMac = toHexString(destMac);
						if ((hexDestMac.equals("01:80:C2:00:00:0E"))
								&& lldpb
								&& (!hexSrcMac.equals(HW1) && (!hexSrcMac
										.equals(HW2)))) {
							// System.out.print("LLDP");
							int l1 = ((Arrays.copyOfRange(
									actpacket.getRawData(), offset, offset + 1)[0] & 1) << 8)
									| Arrays.copyOfRange(
											actpacket.getRawData(), offset + 1,
											offset + 2)[0];
							l1--;

							byte[] ch = new byte[6];
							ch = Arrays.copyOfRange(actpacket.getRawData(),
									offset + 3, offset + 9);
							chassis = toHexString(ch);

							int l2 = ((Arrays.copyOfRange(
									actpacket.getRawData(), offset + 3 + l1,
									offset + 4 + l1)[0] & 1) << 8)
									| Arrays.copyOfRange(
											actpacket.getRawData(), offset + 4
													+ l1, offset + 5 + l1)[0];
							l2--;

							port = new String(Arrays.copyOfRange(
									actpacket.getRawData(), offset + 6 + l1,
									offset + 6 + l1 + l2), "UTF-8");

							ttl = (Arrays.copyOfRange(actpacket.getRawData(),
									offset + 8 + l1 + l2, offset + 9 + l1 + l2)[0] << 8)
									| Arrays.copyOfRange(
											actpacket.getRawData(), offset + 9
													+ l1 + l2, offset + 10 + l1
													+ l2)[0];
							int l3 = ((Arrays.copyOfRange(
									actpacket.getRawData(), offset + 10 + l1
											+ l2, offset + 11 + l1 + l2)[0] & 1) << 8)
									| (Arrays.copyOfRange(
											actpacket.getRawData(), offset + 11
													+ l1 + l2, offset + 12 + l1
													+ l2)[0]);

							switchName = new String(Arrays.copyOfRange(
									actpacket.getRawData(), offset + 12 + l1
											+ l2, offset + 12 + l1 + l2 + l3),
									"UTF-8");

							lldp.update(new LLDPTableEntry(switchName, port,
									ttl * 1000, getPort(receivedPacket.port),
									System.currentTimeMillis(), chassis));

						} else if (((!HW1.equals(hexSrcMac))
								&& (!HW1.equals(hexDestMac))
								&& (!HW2.equals(hexSrcMac)) && (!HW2
									.equals(hexDestMac))) && b) {
							mt.actualize(hexSrcMac, receivedPacket.port);
							if (delete) {
								hs.clear();
								packetCollector.clear();
								delete = false;
							}

							protocol = "";
							toFilter = hexSrcMac + ";" + hexDestMac + ";";

							if (receivedPacket.port == 1) {
								if (actpacket.contains(IpV4Packet.class)) {
									IpPacket ip = actpacket.get(IpPacket.class);
									ipc1i++;
									ipbo2 = true;
									toFilter += ip.getHeader().getSrcAddr()
											.toString().substring(1)
											+ ";"
											+ ip.getHeader().getDstAddr()
													.toString().substring(1)
											+ ";";
									protocol += "ip;";
								} else {
									toFilter += "-;-;";
									protocol += "-;";
								}

								if (actpacket.contains(TcpPacket.class)) {
									TcpPacket tcp = actpacket
											.get(TcpPacket.class);
									tcpc1i++;
									tcpbo2 = true;
									toFilter += tcp.getHeader().getSrcPort()
											+ ";"
											+ tcp.getHeader().getDstPort()
											+ ";";
									protocol += "tcp;";
								} else {
									toFilter += "-;-;";
									protocol += "-;";
								}

								if (actpacket.contains(UdpPacket.class)) {
									UdpPacket udp = actpacket
											.get(UdpPacket.class);
									udpc1i++;
									udpbo2 = true;
									toFilter += udp.getHeader().getSrcPort()
											+ ";"
											+ udp.getHeader().getDstPort();
									protocol += "udp;";
								} else {
									toFilter += "-;-;";
									protocol += "-;";
								}

								if (actpacket.contains(ArpPacket.class)) {
									arpc1i++;
									arpbo2 = true;
									protocol += "arp;";
								} else {
									protocol += "-;";
								}
								if (actpacket
										.contains(IcmpV4CommonPacket.class)) {
									icmpc1i++;
									icmpbo2 = true;
									protocol += "icmp;";
								} else {
									protocol += "-;";
								}
								if (actpacket.contains(IpV6Packet.class)) {
									ip6c1i++;
									ip6bo2 = true;
								}
								all1i++;
								allbo2 = true;
							} else {
								if (actpacket.contains(IpV4Packet.class)) {
									IpPacket ip = actpacket.get(IpPacket.class);
									ipbo1 = true;
									ipc2i++;
									toFilter += ip.getHeader().getSrcAddr()
											.toString().substring(1)
											+ ";"
											+ ip.getHeader().getDstAddr()
													.toString().substring(1)
											+ ";";
									protocol += "ip;";
								} else {
									toFilter += "-;-;";
									protocol += "-;";
								}

								if (actpacket.contains(TcpPacket.class)) {
									TcpPacket tcp = actpacket
											.get(TcpPacket.class);
									tcpbo1 = true;
									tcpc2i++;
									toFilter += tcp.getHeader().getSrcPort()
											+ ";"
											+ tcp.getHeader().getDstPort()
											+ ";";
									protocol += "tcp;";
								} else {
									toFilter += "-;-;";
									protocol += "-;";
								}

								if (actpacket.contains(UdpPacket.class)) {
									UdpPacket udp = actpacket
											.get(UdpPacket.class);
									udpbo1 = true;
									udpc2i++;
									toFilter += udp.getHeader().getSrcPort()
											+ ";"
											+ udp.getHeader().getDstPort();
									protocol += "udp;";
								} else {
									toFilter += "-;-;";
									protocol += "-;";
								}

								if (actpacket.contains(ArpPacket.class)) {
									arpbo1 = true;
									arpc2i++;
									protocol += "arp;";
								} else {
									protocol += "-;";
								}
								if (actpacket
										.contains(IcmpV4CommonPacket.class)) {
									icmpbo1 = true;
									icmpc2i++;
									protocol += "icmp;";
								} else {
									protocol += "-;";
								}
								if (actpacket.contains(IpV6Packet.class)) {
									ip6bo1 = true;
									ip6c2i++;
								}
								allbo1 = true;
								all2i++;
							}
							toFilter += protocol;

							int port = mt.getDestPort(hexDestMac);

							if (((port == 0) || (port != receivedPacket.port))
									&& (FilterWindow.allow(toFilter
											+ getPort3(receivedPacket.port)
											+ ";" + "in"))
									&& (FilterWindow.allow(toFilter
											+ getPort4(receivedPacket.port)
											+ ";" + "out"))) {

								if (receivedPacket.port == 1) {
									ph2.sendPacket(receivedPacket.packet);
									if (arpbo2) {
										arpc2o++;
									}
									if (ipbo2) {
										ipc2o++;
									}
									if (tcpbo2) {
										tcpc2o++;
									}
									if (icmpbo2) {
										icmpc2o++;
									}
									if (udpbo2) {
										udpc2o++;
									}
									if (ip6bo2) {
										ip6c2o++;
									}
									if (allbo2) {
										all2o++;
									}
									// System.out.print("Send2\n");
								} else {
									ph1.sendPacket(receivedPacket.packet);
									if (arpbo1) {
										arpc1o++;
									}
									if (ipbo1) {
										ipc1o++;
									}
									if (tcpbo1) {
										tcpc1o++;
									}
									if (icmpbo1) {
										icmpc1o++;
									}
									if (udpbo1) {
										udpc1o++;
									}
									if (ip6bo1) {
										ip6c1o++;
									}
									if (allbo1) {
										all1o++;
									}
									// System.out.print("Send1\n");
								}
								hs.add(packetString2(
										receivedPacket.packet.getRawData(),
										receivedPacket.port));

							}

							icmpbo1 = false;
							udpbo1 = false;
							ipbo1 = false;
							tcpbo1 = false;
							arpbo1 = false;
							ip6bo1 = false;
							icmpbo2 = false;
							udpbo2 = false;
							ipbo2 = false;
							tcpbo2 = false;
							arpbo2 = false;
							ip6bo2 = false;
							allbo1 = false;
							allbo2 = false;

							MainWindow.updateStats(new Object[] { "port 1 in",
									arpc1i, icmpc1i, ipc1i, ip6c1i, tcpc1i,
									udpc1i, all1i }, new Object[] {
									"port 1 out", arpc1o, icmpc1o, ipc1o,
									ip6c1o, tcpc1o, udpc1o, all1o },
									new Object[] { "port 2 in", arpc2i,
											icmpc2i, ipc2i, ip6c2i, tcpc2i,
											udpc2i, all2i }, new Object[] {
											"port 2 out", arpc2o, icmpc2o,
											ipc2o, ip6c2o, tcpc2o, udpc2o,
											all2o });
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static synchronized void clear() {
		arpc1i = 0;
		icmpc1i = 0;
		ipc1i = 0;
		ip6c1i = 0;
		tcpc1i = 0;
		udpc1i = 0;
		all1i = 0;
		arpc1o = 0;
		icmpc1o = 0;
		ipc1o = 0;
		ip6c1o = 0;
		tcpc1o = 0;
		udpc1o = 0;
		all1o = 0;
		arpc2i = 0;
		icmpc2i = 0;
		ipc2i = 0;
		ip6c2i = 0;
		tcpc2i = 0;
		udpc2i = 0;
		all2i = 0;
		arpc2o = 0;
		icmpc2o = 0;
		ipc2o = 0;
		ip6c2o = 0;
		tcpc2o = 0;
		udpc2o = 0;
		all2o = 0;
	}

	public static synchronized void CheckB() {
		if (SwitchClass.b) {
			SwitchClass.b = false;
		} else {
			SwitchClass.b = true;
		}
	}

	public static synchronized void CheckLLDPB() {
		if (SwitchClass.lldpb) {
			SwitchClass.lldpb = false;
		} else {
			SwitchClass.lldpb = true;
		}
	}

	public static synchronized boolean getLLDPB() {
		return lldpb;
	}
}
