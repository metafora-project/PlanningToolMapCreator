package de.kuei.metafora.planningtoolmapcreator.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class MysqlConnector {

	public static String url = "jdbc:mysql://metaforaserver.ku.de/metafora?useUnicode=true&characterEncoding=UTF-8";
	public static String user = "user";
	public static String password = "password";

	private static MysqlConnector instance = null;

	public static synchronized MysqlConnector getInstance() {
		if (instance == null) {
			instance = new MysqlConnector();
		}
		return instance;
	}

	private Connection connection;

	private MysqlConnector() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			System.err.println("class loaded");

			try {
				connection = DriverManager.getConnection(MysqlConnector.url,
						MysqlConnector.user, MysqlConnector.password);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Vector<String> loadMapNamesFromDatabase() {
		String sql = "SELECT graphName FROM DirectedGraph";

		Vector<String> graphNames = new Vector<String>();

		Statement stmt;
		try {
			stmt = getConnection().createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			if (rs.first()) {
				do {
					String graphName = rs.getString("graphName");

					if (graphNames.contains(graphName)) {
						System.err
								.println("DATABASE ERROR: duplicate Graphname: "
										+ graphName);
					} else {
						if (graphName != null)
							graphNames.add(graphName);
					}
				} while (rs.next());
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return graphNames;
	}

	private Connection getConnection() {
		try {
			if (connection == null || !connection.isValid(5)) {
				connection = DriverManager.getConnection(MysqlConnector.url,
						MysqlConnector.user, MysqlConnector.password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

}
