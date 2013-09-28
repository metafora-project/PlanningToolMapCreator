package de.kuei.metafora.planningtoolmapcreator.client.serverlink;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MapInitAsync {
	public void sendCreateMapCommand(String token, Vector<String> users,
			String md5Password, String groupId, String challengeId,
			String challengeName, String ptNodeId, String ptMap,
			String mapname, AsyncCallback<Void> callback);

	public void showDialog(String ptNodeId, AsyncCallback<Boolean> callback);

	public void getMap(String ptNodeId, AsyncCallback<String> callback);

	public void removeNodeId(String ptNodeId, AsyncCallback<Void> callback);

	void getMapNames(AsyncCallback<Vector<String>> callback);

	void getTomcatServer(AsyncCallback<String> callback);

}
