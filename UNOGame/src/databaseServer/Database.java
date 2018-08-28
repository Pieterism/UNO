package databaseServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import security.PasswordHashing;

public class Database {

	private Connection connection;
	private Statement statement;
	String uri;
	String filepath = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\keystore.jks";
	//String filepath = "C:\\Users\\wouter\\Documents\\School\\geavanceerde\\keystore.jks";
	Signature signature;
	PrivateKey privateKey;

	// Database aanmaken indien ze nog niet bestaat
	public Database(String uri) throws SQLException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException {
		this.uri = uri;
		File dbName = new File(uri);
		if (dbName.exists()) {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			System.out.println("UNO database opened successfully! " +" [ "+ this.uri + " ]");
		} else {
			try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + uri)) {
				if (conn != null) {

					System.out.println("UNO database has been created." + "[ "+ this.uri + " ]");
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		KeyStore keystore = KeyStore.getInstance("JKS");
		FileInputStream fileInputStream = new FileInputStream(filepath);
		keystore.load(fileInputStream, "uno".toCharArray());
		privateKey = (PrivateKey) keystore.getKey("uno", "uno".toCharArray());
		signature = Signature.getInstance("SHA256withRSA");

	}

	// aanmaken van een table voor Users
	public void createUserTable() throws SQLException {

		try {

			String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
					+ "	user_id     INTEGER     PRIMARY KEY         AUTOINCREMENT,\n"
					+ "	username    VARCHAR     NOT NULL,\n" + "	password    VARCHAR     NOT NULL, \n"
					+ " token       BLOB,\n" + " timestamp	TIMESTAMP\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("User Table created successfully");
	}

	// aanmaken van table voor Players hun kaarten
	public void createPlayerHandTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS PlayerHand (\n"
					+ "	id        INTEGER     PRIMARY KEY   AUTOINCREMENT,\n" + "	user_id   INTEGER     NOT NULL,\n"
					+ "	card_id   INTEGER     NOT NULL, \n" + " FOREIGN KEY(user_id) REFERENCES Users(user_id),"
					+ " FOREIGN KEY (card_id) REFERENCES Images(card_id)\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Players hand table created successfully");
	}

	// aanmaken van table voor elk spel bij te houden
	public void createGameTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS Game (\n"
					+ "	game_id     INTEGER     PRIMARY KEY     AUTOINCREMENT,\n"
					+ "	user1       INTEGER     NOT NULL,\n" + "	user2       INTEGER     NOT NULL, \n"
					+ " user3       INTEGER     NOT NULL, \n" + " user4       INTEGER     NOT NULL, \n"
					+ " active      BOOLEAN     , \n" + "FOREIGN KEY(user1) REFERENCES Users(user_id),\n"
					+ "FOREIGN KEY(user2) REFERENCES Users(user_id),\n"
					+ "FOREIGN KEY(user3) REFERENCES Users(user_id),\n"
					+ "FOREIGN KEY(user4) REFERENCES Users(user_id)\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Game table created successfully");
	}

	// aanmaken table voor kaartvoorstellingen
	public void createImagesTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS Images (\n"
					+ "	card_id         INTEGER     PRIMARY KEY     AUTOINCREMENT,\n"
					+ "	card_color      INTEGER     NOT NULL,\n" + "	card_value      INTEGER     NOT NULL,\n"
					+ " card_image      BLOB        NOT NULL\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Images table created successfully");
	}

	// aanmaken table voor beurtenverloop van een spel
	public void createGameTurnTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS GameTurn (\n"
					+ "	turn_id         INTEGER        PRIMARY KEY     AUTOINCREMENT,\n"
					+ "	game_id         INTEGER        NOT NULL,\n" + "	user_id         INTEGER        NOT NULL,\n"
					+ " card_id         INTEGER        NOT NULL,\n" + " next_player     INTEGER        NOT NULL,\n"
					+ "FOREIGN KEY(game_id) REFERENCES Game(game_id),\n"
					+ "FOREIGN KEY(user_id) REFERENCES Users(user_id),\n"
					+ "FOREIGN KEY(card_id) REFERENCES Images(card_id)\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("GameTurn table created successfully");
	}

	// Controle of de username reeds aanwezig is in de databank
	public boolean checkUsername(String name) {

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "SELECT username FROM USERS WHERE USERNAME = ?";
		try {

			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				System.out.println("USERNAME IS REEDS IN GEBRUIK");
				return false;
			} else {
				return true;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return false;

	}

	// toevoegen van een user in databank
	public void insertUser(String username, String password) throws InvalidKeyException, SignatureException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String token = createToken(username, password, timestamp);
		
		System.out.println("Timestamp: " + timestamp);
		System.out.println("Token: " + token);
		
		if (!checkUsername(username)) {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		String sql = "INSERT INTO Users(username,password,token,timestamp) VALUES(?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setString(2, PasswordHashing.hashPassword(password));
			pstmt.setString(3, token);
			pstmt.setTimestamp(4, timestamp);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("insert user completed!");

	}

	private String createToken(String username, String password, Timestamp timestamp)
			throws InvalidKeyException, SignatureException {

		String token = (username + timestamp);
		signature.initSign(privateKey);
		signature.update(token.getBytes());
		byte[] signedToken = signature.sign();

		saveToken(username, new String(signedToken), timestamp);

		return new String(signedToken);

	}

	private void saveToken(String username, String signedToken, Timestamp timestamp) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "UPDATE Users SET token = ?, timestamp =? WHERE username = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, signedToken);
			pstmt.setTimestamp(2, timestamp);
			pstmt.setString(3, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("insert user completed!");

	}

	// inhoud van databank teruggeven
	public String getAllUsers() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT user_id, username, password FROM USERS";
		StringBuffer sb = new StringBuffer();
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				sb.append(rs.getString("USER_ID") + "\t");
				sb.append(rs.getString("USERNAME") + "\t");
				sb.append(rs.getString("PASSWORD") + "\n");

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return sb.toString();
	}

	// nakijken of de user met overeenkomstig passwoord zich al in
	// de databank bevindt
	public boolean loginUser(String username, String password) throws InvalidKeyException, SignatureException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String token = createToken(username, password, timestamp);
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "SELECT USERNAME FROM USERS WHERE USERNAME = ? AND PASSWORD = ?;";
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				System.out.println("LOGGED IN");
				return true;
			} else {
				return false;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return false;

	}

	// voegt kaart toe aan table van de hand van een speler
	public void insertCard(int user_id, int card_id) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO PlayerHand(user_id,card_id) VALUES(?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, card_id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("insert card completed!");
	}

	// verwijdert kaart uit de hand van een speler
	public void removeCard(int user_id, int card_id) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "DELETE FROM PlayerHand WHERE card_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, card_id);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Removed Card " + card_id);
	}

	// geeft alle kaarten in de hand van een speler weer
	public String getPlayerHand(int user_id) throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT card_id FROM PlayerHand WHERE user_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, user_id);
		ResultSet rs = pstmt.executeQuery();

		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			sb.append("(");
			sb.append(rs.getString("card_id"));
			sb.append(")");
		}
		return sb.toString();
	}

	// voeg nieuw spel toe aan databank
	public void addGame(int user1, int user2, int user3, int user4) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO Game(user1, user2, user3, user4, active) VALUES(?,?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, user1);
			pstmt.setInt(2, user2);
			pstmt.setInt(3, user3);
			pstmt.setInt(4, user4);
			pstmt.setBoolean(5, true);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("New game added!");
	}

	// verwijdert spel uit databank
	public void removeGame(int game_id) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "DELETE FROM Game WHERE game_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, game_id);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Removed Game " + game_id);
	}

	// Geeft weer welke spellen actief zijn
	public String getActiveGames() throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT game_id FROM Game WHERE active= 1";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			sb.append("(");
			sb.append(rs.getString("game_id"));
			sb.append(")");
		}
		return sb.toString();
	}

	// make game inactive
	public void StopGame(int game_id) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "UPDATE Game SET active = 0 WHERE game_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, game_id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Stopped Game");

	}

	// gespeelde beurt toevoegen aan databank van spelverloop
	public void playTurn(int game_id, int user_id, int card_id, int next_player) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO GameTurn(game_id, user_id, card_id, next_player) VALUES(?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, game_id);
			pstmt.setInt(2, user_id);
			pstmt.setInt(3, card_id);
			pstmt.setInt(4, next_player);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("insert turn completed!");
	}

	// geeft info over een gespeelde beurt
	public String getAllTurns(int game_id) throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT * FROM GameTurn WHERE game_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, game_id);
		ResultSet rs = pstmt.executeQuery();

		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			sb.append("(");
			sb.append(rs.getInt("turn_id") + "\t");
			sb.append(rs.getInt("game_id") + "\t");
			sb.append(rs.getInt("user_id") + "\t");
			sb.append(rs.getInt("card_id") + "\t");
			sb.append(rs.getInt("next_player") + "\t");
			sb.append(")");
		}
		return sb.toString();
	}

	// geeft alle beurten van een gespeeld spel weer
	public String getTurn(int turn_id, int game_id) throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "SELECT * FROM GameTurn WHERE game_id = ? AND turn_id = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, game_id);
		pstmt.setInt(2, turn_id);
		ResultSet rs = pstmt.executeQuery();

		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			sb.append("(");
			sb.append(rs.getInt("turn_id") + "\t");
			sb.append(rs.getInt("game_id") + "\t");
			sb.append(rs.getInt("use_id") + "\t");
			sb.append(rs.getInt("card_id") + "\t");
			sb.append(rs.getInt("next_player") + "\t");
			sb.append(")\n");
		}
		return sb.toString();

	}

	// geeft voorstelling van kaart terug
	public String getCardImage(int color, int value) throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT card_image FROM Images WHERE card_color = ? AND card_value = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, color);
		pstmt.setInt(2, value);
		ResultSet rs = pstmt.executeQuery();

		return rs.toString();

	}

	// afbeelding unokaart toevoegen
	public void insertImage(int card_color, int card_value, Blob image) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO Images(card_color,card_value, image) VALUES(?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, card_color);
			pstmt.setInt(2, card_value);
			pstmt.setBlob(3, image);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Image insert card completed!");
	}

	public String getToken(String username) throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT token FROM Users WHERE username = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();

		return rs.toString();
	}

	public boolean validateToken(String username, String token) {
		return false;
	}

	public void persistQuerty(String sql) {
		
	}
	
}
