package de.kuei.metafora.planningtoolmapcreator.server;

import java.util.Vector;

import javax.servlet.http.HttpServlet;

import de.kuei.metafora.planningtoolmapcreator.server.mysql.ChannelDescription;
import de.kuei.metafora.planningtoolmapcreator.server.mysql.MysqlConnector;
import de.kuei.metafora.planningtoolmapcreator.server.mysql.MysqlInitConnector;
import de.kuei.metafora.planningtoolmapcreator.server.mysql.ServerDescription;
import de.kuei.metafora.xmppbridge.xmpp.NameConnectionMapper;
import de.kuei.metafora.xmppbridge.xmpp.ServerConnection;
import de.kuei.metafora.xmppbridge.xmpp.XmppMUC;
import de.kuei.metafora.xmppbridge.xmpp.XmppMUCManager;

public class StartupServlet extends HttpServlet {

	public static String sending_tool = "PLANNING_TOOL_MAP_CREATOR";
	public static boolean logged = true;
	public static String receiving_tool = "PLANNING_TOOL";
	public static String tomcatserver = "https://metaforaserver.ku.de";

	public static XmppMUC command = null;

	public void init() {

		MysqlInitConnector.getInstance().loadData("PlanningToolMapCreator");

		sending_tool = MysqlInitConnector.getInstance().getParameter(
				"SENDING_TOOL");

		receiving_tool = MysqlInitConnector.getInstance().getParameter(
				"RECEIVING_TOOL");

		if (MysqlInitConnector.getInstance().getParameter("logged")
				.toLowerCase().equals("false")) {
			logged = false;
		}

		System.err.println("SENDING_TOOL: " + sending_tool);
		System.err.println("RECEIVING_TOOL: " + receiving_tool);
		System.err.println("logged: " + logged);

		ServerDescription tomcatServer = MysqlInitConnector.getInstance()
				.getAServer("tomcat");

		StartupServlet.tomcatserver = tomcatServer.getServer();

		System.err.println("Tomcat server: " + tomcatserver);

		// init mysql
		ServerDescription mysqlServer = MysqlInitConnector.getInstance()
				.getAServer("mysql");

		MysqlConnector.url = "jdbc:mysql://" + mysqlServer.getServer()
				+ "/metafora?useUnicode=true&characterEncoding=UTF-8";
		MysqlConnector.user = mysqlServer.getUser();
		MysqlConnector.password = mysqlServer.getPassword();

		System.err.println("MySQL server: " + MysqlConnector.url);

		// configure xmpp
		Vector<ServerDescription> xmppServers = MysqlInitConnector
				.getInstance().getServer("xmpp");

		System.err.println(xmppServers.size() + " XMPP connections found.");

		for (ServerDescription xmppServer : xmppServers) {
			System.err.println("XMPP server: " + xmppServer.getServer());
			System.err.println("XMPP user: " + xmppServer.getUser());
			System.err.println("XMPP password: " + xmppServer.getPassword());
			System.err.println("XMPP device: " + xmppServer.getDevice());
			System.err.println("Modul: " + xmppServer.getModul());

			System.err.println("Starting XMPP connection...");

			NameConnectionMapper.getInstance().createConnection(
					xmppServer.getConnectionName(), xmppServer.getServer(),
					xmppServer.getUser(), xmppServer.getPassword(),
					xmppServer.getDevice());

			NameConnectionMapper.getInstance()
					.getConnection(xmppServer.getConnectionName()).login();
		}

		Vector<ChannelDescription> channels = MysqlInitConnector.getInstance()
				.getXMPPChannels();

		for (ChannelDescription channeldesc : channels) {
			ServerConnection connection = NameConnectionMapper.getInstance()
					.getConnection(channeldesc.getConnectionName());

			if (connection == null) {
				System.err.println("StartupServlet: Unknown connection: "
						+ channeldesc.getUser());
				continue;
			}

			System.err.println("Joining channel " + channeldesc.getChannel()
					+ " as " + channeldesc.getAlias());

			XmppMUC muc = XmppMUCManager.getInstance().getMultiUserChat(
					channeldesc.getChannel(), channeldesc.getAlias(),
					connection);
			muc.join(0);

			if (channeldesc.getChannel().equals("command")) {
				System.err.println("StartupServlet: command configured.");
				command = muc;
			}
		}
	}

	@Override
	public void destroy() {
		Vector<ServerDescription> xmppServers = MysqlInitConnector
				.getInstance().getServer("xmpp");

		for (ServerDescription xmppServer : xmppServers) {
			NameConnectionMapper.getInstance()
					.getConnection(xmppServer.getConnectionName()).disconnect();
		}
	}
}
