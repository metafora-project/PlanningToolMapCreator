package de.kuei.metafora.planningtoolmapcreator.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.kuei.metafora.planningtoolmapcreator.server.xml.Classification;
import de.kuei.metafora.planningtoolmapcreator.server.xml.CommonFormatCreator;
import de.kuei.metafora.planningtoolmapcreator.server.xml.Role;
import de.kuei.metafora.planningtoolmapcreator.server.xml.XMLException;

public class PlanningToolMapManager {

	private static Map<String, PlanningToolMapManager> maps = Collections
			.synchronizedMap(new HashMap<String, PlanningToolMapManager>());

	public static boolean istKnownMap(String mapName) {
		if ((maps != null) && (maps.containsKey(mapName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the only existing instance of Manager.
	 * 
	 * @param aLasadMapName
	 *            name of the LASAD map
	 * @return Manager
	 */
	public static PlanningToolMapManager getInstance(String mapName) {
		if (maps.get(mapName) == null) {
			maps.put(mapName, new PlanningToolMapManager());
		}
		return maps.get(mapName);
	}

	public PlanningToolMapManager() {

	}

	/**
	 * Sends update command to Planning Tool with resource card URL.
	 */
	public void sendUpdateCommand(String token, Vector<String> users,
			String ptNodeId, String ptMap, String mapname, String groupId,
			String challengeId, String challengeName) {

		CommonFormatCreator creator;

		try {
			creator = new CommonFormatCreator(System.currentTimeMillis(),
					Classification.modify, "MODIFY_NODE_URL",
					StartupServlet.logged);

			creator.setObject(ptNodeId, "PLANNING_TOOL_NODE");

			try {
				creator.addProperty("RESOURCE_URL",
						StartupServlet.tomcatserver
								+ "/planningtoolsolo/?planningCard="
								+ URLEncoder.encode(mapname, "UTF8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			creator.addProperty("PLANNING_TOOL_MAP", ptMap);
			creator.addProperty("CREATED_PLANNING_TOOL_MAP", mapname);

			creator.addContentProperty("RECEIVING_TOOL",
					StartupServlet.receiving_tool);

			creator.addContentProperty("SENDING_TOOL",
					StartupServlet.sending_tool);

			for (String u : users) {
				creator.addUser(u, token, Role.originator);
			}

			creator.addContentProperty("GROUP_ID", groupId);
			creator.addContentProperty("CHALLENGE_ID", challengeId);
			creator.addContentProperty("CHALLENGE_NAME", challengeName);

			if (StartupServlet.command != null)
				StartupServlet.command.sendMessage(creator.getDocument());

		} catch (XMLException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		maps.remove(mapname);
		NodeManager.getInstance().removeNodeId(ptNodeId);
	}
}
