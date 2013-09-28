package de.kuei.metafora.planningtoolmapcreator.server;

import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.kuei.metafora.planningtoolmapcreator.client.serverlink.MapInit;
import de.kuei.metafora.planningtoolmapcreator.server.mysql.MysqlConnector;

public class MapInitImpl extends RemoteServiceServlet implements MapInit {

	@Override
	public void sendCreateMapCommand(String token, Vector<String> users,
			String md5Password, String groupId, String challengeId,
			String challengeName, String ptNodeId, String ptMap, String mapname) {

		NodeManager.getInstance().setNodeToMap(mapname, ptNodeId);

		PlanningToolMapManager manager = PlanningToolMapManager
				.getInstance(mapname);
		manager.sendUpdateCommand(token, users, ptNodeId, ptMap, mapname,
				groupId, challengeId, challengeName);
	}

	@Override
	public boolean showDialog(String ptNodeId) {
		boolean contains = false;
		if (!NodeManager.getInstance().containsNode(ptNodeId)) {
			contains = true;
		}
		
		return contains;
	}

	@Override
	public String getMap(String ptNodeId) {

		String map = NodeManager.getInstance().getMapName(ptNodeId);

		return map;
	}

	@Override
	public void removeNodeId(String ptNodeId) {
		NodeManager.getInstance().removeNodeId(ptNodeId);
	}

	@Override
	public Vector<String> getMapNames() {
		return MysqlConnector.getInstance().loadMapNamesFromDatabase();
	}

	@Override
	public String getTomcatServer() {
		return StartupServlet.tomcatserver;
	}
}
