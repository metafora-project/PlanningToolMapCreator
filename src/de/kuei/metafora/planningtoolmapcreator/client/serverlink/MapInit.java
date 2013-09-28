package de.kuei.metafora.planningtoolmapcreator.client.serverlink;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("mapinit")
public interface MapInit extends RemoteService {

	public void sendCreateMapCommand(String token, Vector<String> users,
			String md5Password, String groupId, String challengeId,
			String challengeName, String ptNodeId, String ptMap, String mapname);

	public String getMap(String ptNodeId);

	public boolean showDialog(String ptNodeId);

	public void removeNodeId(String ptNodeId);

	public Vector<String> getMapNames();

	public String getTomcatServer();

}
