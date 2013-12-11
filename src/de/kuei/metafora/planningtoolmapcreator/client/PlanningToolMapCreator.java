package de.kuei.metafora.planningtoolmapcreator.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

import de.kuei.metafora.planningtoolmapcreator.client.serverlink.MapInit;
import de.kuei.metafora.planningtoolmapcreator.client.serverlink.MapInitAsync;
import de.kuei.metafora.planningtoolmapcreator.client.util.EncodingUrlBuilder;
import de.kuei.metafora.planningtoolmapcreator.client.util.UrlDecoder;
import de.kuei.metafora.planningtoolmapcreator.shared.Base64;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PlanningToolMapCreator implements EntryPoint {

	// object needed to implement i18n through Languages interface
	final static Languages language = GWT.create(Languages.class);

	private final MapInitAsync mapInit = GWT.create(MapInit.class);

	public static String tomcat = "https://metafora-project.de";

	public static String token = null;
	public static Vector<String> users = null;
	public static String md5Password = null;
	public static String groupId = null;
	public static String challengeId = null;
	public static String challengeName = null;
	public static String ptNodeId = null;
	public static String ptMap = null;
	public static String lang = "en";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		mapInit.getTomcatServer(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				tomcat = result;
			}
		});

		// read info from URL
		users = new Vector<String>();

		PlanningToolMapCreator.token = UrlDecoder.getParameter("token");

		String user = UrlDecoder.getParameter("user");

		PlanningToolMapCreator.users.add(user);

		Map<String, List<String>> parameters = Window.Location
				.getParameterMap();

		Set<String> keySet = parameters.keySet();

		for (String key : keySet) {
			if (key.startsWith("otherUser")) {
				List<String> values = parameters.get(key);
				for (String u : values) {
					if (u != null)
						PlanningToolMapCreator.users.add(URL.decode(u));
				}
			}
		}

		PlanningToolMapCreator.md5Password = UrlDecoder.getParameter("pw");

		PlanningToolMapCreator.groupId = UrlDecoder.getParameter("groupId");

		PlanningToolMapCreator.challengeId = UrlDecoder
				.getParameter("challengeId");

		PlanningToolMapCreator.challengeName = UrlDecoder
				.getParameter("challengeName");

		PlanningToolMapCreator.ptNodeId = UrlDecoder.getParameter("ptNodeId");

		PlanningToolMapCreator.ptMap = UrlDecoder.getParameter("ptMap");

		PlanningToolMapCreator.lang = Window.Location.getParameter("locale");

		if (lang == null || lang.length() == 0)
			lang = "en";

		Connecting.getInstance().main();

		mapInit.showDialog(ptNodeId, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showDialog();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server connection error. Please close tab and retry.");
				Connecting.getInstance().label
						.setText("Server connection error. Please close tab and retry.");
			}
		});

		Timer t = new Timer() {

			@Override
			public void run() {
				mapInit.getMap(ptNodeId, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(String result) {
						if (result != null) {
							// create URL
							UrlBuilder planningToolUrlBuilder = new EncodingUrlBuilder();

							String protocol = null;
							String server = null;
							if (tomcat.startsWith("https")) {
								protocol = "https";
								server = tomcat.substring(8, tomcat.length());
							} else {
								protocol = "http";
								server = tomcat.substring(7, tomcat.length());
							}

							planningToolUrlBuilder.setProtocol(protocol);
							planningToolUrlBuilder.setHost(server);
							planningToolUrlBuilder.setPath("planningtoolsolo/");

							if (users.size() > 0)
								planningToolUrlBuilder.setParameter("user",
										PlanningToolMapCreator.users.get(0));

							planningToolUrlBuilder.setParameter("pw",
									PlanningToolMapCreator.md5Password);

							planningToolUrlBuilder.setParameter("pwEncrypted",
									"true");

							planningToolUrlBuilder.setParameter("groupId",
									PlanningToolMapCreator.groupId);

							planningToolUrlBuilder.setParameter("challengeId",
									PlanningToolMapCreator.challengeId);

							planningToolUrlBuilder.setParameter(
									"challengeName",
									PlanningToolMapCreator.challengeName);

							Base64 base = new Base64();
							planningToolUrlBuilder.setParameter("ptMap",
									base.encodeStringForUrl(result));

							planningToolUrlBuilder.setParameter("token",
									PlanningToolMapCreator.token);

							planningToolUrlBuilder.setParameter("locale",
									PlanningToolMapCreator.lang);

							for (int i = 1; i < users.size(); i++) {
								if (!users.get(i).isEmpty())
									planningToolUrlBuilder.setParameter(
											"otherUser" + i, users.get(i));
							}

							final String url = planningToolUrlBuilder
									.buildString();

							mapInit.removeNodeId(ptNodeId,
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											// show URL
											Window.Location.replace(url);
										}

										@Override
										public void onSuccess(Void result) {
											// show URL
											Window.Location.replace(url);
										}
									});
						}
					}

				});

			}
		};

		t.scheduleRepeating(2000);
	}

	public void showDialog() {
		mapInit.getMapNames(new AsyncCallback<Vector<String>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Vector<String> result) {

				MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

				for (String map : result) {
					if (map != null)
						oracle.add(map);
				}

				Connecting.getInstance().hideUi();

				Dialog.getInstance().main(oracle);
			}
		});
	}

}
