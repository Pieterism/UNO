package databaseServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
import java.util.ArrayList;
import java.util.List;

import security.BCrypt;
import security.PasswordHashing;
import uno.Card;

public class Database {

	private Connection connection;
	private Statement statement;
	String uri;
	// String filepath = "D:\\Google Drive\\School\\2017-2018\\1e
	// Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\keystore.jks";
	String filepath = "..\\keystore.jks";
	PublicKey publicKey;
	PrivateKey privateKey;
	Signature signature;

	// Database aanmaken indien ze nog niet bestaat
	/**
	 * @param uri
	 * @throws SQLException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 */
	public Database(String uri) throws SQLException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException {
		this.uri = uri;
		File dbName = new File(uri);
		if (dbName.exists()) {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			System.out.println("UNO database opened successfully! " + " [ " + this.uri + " ]");
		} else {
			try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + uri)) {
				if (conn != null) {

					System.out.println("UNO database has been created." + "[ " + this.uri + " ]");
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
	/**
	 * @throws SQLException
	 */
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
	/**
	 * @param id
	 * @throws SQLException
	 */
	public void createPlayerHandTable(String id) throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS PlayerHand" + id + " (\n"
					+ "	id        INTEGER     PRIMARY KEY   AUTOINCREMENT,\n" + "	username   VARCHAR     NOT NULL,\n"
					+ "	cards   VARCHAR     NOT NULL \n" + ");";

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
	/**
	 * @throws SQLException
	 */
	public void createGameTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS Game (\n"
					+ "	game_id     	INTEGER     PRIMARY KEY   AUTOINCREMENT,\n"
					+ "	game_name       VARCHAR     NOT NULL,\n" + " players       	INTEGER     NOT NULL,\n"
					+ " active       	BOOLEAN     NOT NULL, \n" + " serverport		INTEGER		NOT NULL, \n"
					+ " game_theme		INTEGER, \n" + "FOREIGN KEY(game_id) REFERENCES Game(game_id)\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Game table created successfully");
	}

	// aanmaken van table voor elk spel bij te houden
	/**
	 * @throws SQLException
	 */
	public void createGameToUserTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS GameToUSer (\n" + "	game_id     INTEGER     NOT NULL,\n"
					+ "	user1       INTEGER     NOT NULL,\n" + "	user2       INTEGER     NOT NULL, \n"
					+ " user3       INTEGER     NOT NULL,\n" + " user4       INTEGER     NOT NULL, \n"
					+ " game_theme, \n" + "FOREIGN KEY(game_id) REFERENCES Game(game_id),\n"
					+ "FOREIGN KEY(user1) REFERENCES Users(username),\n"
					+ "FOREIGN KEY(user2) REFERENCES Users(username),\n"
					+ "FOREIGN KEY(user3) REFERENCES Users(username),\n"
					+ "FOREIGN KEY(user4) REFERENCES Users(username)\n" + ");";

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
	/**
	 * @throws SQLException
	 */
	public void createImagesTable() throws SQLException {
		try {

			String sql = "CREATE TABLE IF NOT EXISTS Images (\n"
					+ "	card_id         INTEGER     PRIMARY KEY     AUTOINCREMENT,\n"
					+ "	card_color      INTEGER     NOT NULL,\n" + "	card_value      INTEGER     NOT NULL,\n"
					+ " card_theme		INTEGER,\n" + " card_image      BLOB        NOT NULL\n" + ");";

			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
			statement = connection.createStatement();
			statement.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Images table created successfully");
	}

	// Controle of de username reeds aanwezig is in de databank
	/**
	 * @param name
	 * @return
	 */
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
	/**
	 * @param username
	 * @param password
	 * @param token
	 * @param timestamp
	 */
	public void addUser(String username, String password, String token, Timestamp timestamp) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO Users(username,password,token,timestamp) VALUES(?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
			pstmt.setString(3, token);
			pstmt.setTimestamp(4, timestamp);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("insert user completed!");
	}

	/**
	 * @param username
	 * @param timestamp
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public String createToken(String username, Timestamp timestamp) throws InvalidKeyException, SignatureException {

		String token = (username + timestamp);
		signature.initSign(privateKey);
		signature.update(token.getBytes());
		byte[] signedToken = signature.sign();
		saveToken(username, new String(signedToken), timestamp);
		return new String(signedToken);
	}

	/**
	 * @param username
	 * @param signedToken
	 * @param timestamp
	 */
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
	/**
	 * @return
	 */
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
	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws SQLException 
	 */
	public boolean loginUser(String username, String password) throws InvalidKeyException, SignatureException, SQLException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";

			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			System.out.println(rs.getString("password"));
			
			String hashed_pw = rs.getString("password");
			createToken(username, timestamp);
			return BCrypt.checkpw(password, hashed_pw);

	}

	// geeft alle kaarten in de hand van een speler weer
	/**
	 * @param user_id
	 * @return
	 * @throws SQLException
	 */
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
	/**
	 * @param game_id
	 * @param user1
	 * @param user2
	 * @param user3
	 * @param user4
	 */
	public void addUserToGame(int game_id, String user1, String user2, String user3, String user4) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO GameToUSer(game_id, user1, user2, user3, user4) VALUES(?,?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, game_id);
			pstmt.setString(2, user1);
			pstmt.setString(3, user2);
			pstmt.setString(4, user3);
			pstmt.setString(5, user4);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("New gameToUser added!");
	}

	/**
	 * @param dbID
	 * @param name
	 * @param aantalSpelers
	 * @param serverport
	 * @param theme
	 */
	public void addGame(String dbID, String name, int aantalSpelers, int serverport, int theme) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO Game(game_id, game_name, players, active ,serverport, game_theme) VALUES(?,?,?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, dbID);
			pstmt.setString(2, name);
			pstmt.setInt(3, aantalSpelers);
			pstmt.setBoolean(4, false);
			pstmt.setInt(5, serverport);
			pstmt.setInt(6, theme);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("New game added!");
	}

	/**
	 * @param name
	 * @return
	 */
	public int getGameId(String name) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "SELECT game_id  FROM Game WHERE game_name = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, name);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(0);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	// Geeft weer welke spellen actief zijn
	/**
	 * @return
	 * @throws SQLException
	 */
	public List<String> getActiveGames() throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT game_id, game_name, players, serverport, game_theme FROM Game WHERE active= 1";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		List<String> games = new ArrayList<>();
		while (rs.next()) {
			StringBuilder sb = new StringBuilder();
			sb.append(rs.getString("game_id"));
			sb.append("\t");
			sb.append(rs.getString("game_name"));
			sb.append("\t");
			sb.append(rs.getInt("players"));
			sb.append("\t");
			sb.append(rs.getInt("serverPort"));
			sb.append("\t");
			sb.append(rs.getInt("game_theme"));
			sb.append("\t");
			games.add(sb.toString());
		}
		return games;
	}

	// make game inactive
	/**
	 * @param game_id
	 */
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
	/**
	 * @param name
	 * @param cards
	 * @param gameId
	 */
	public void playTurn(String name, List<Card> cards, String gameId) {
		StringBuilder sb = new StringBuilder();
		for (Card card : cards) {
			sb.append(card.toString() + "\'");
		}
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO PlayerHand" + gameId + " (username, cards) VALUES(?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.setString(2, sb.toString());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("insert turn completed!");
	}

	// geeft info over een gespeelde beurt
	/**
	 * @param game_id
	 * @return
	 * @throws SQLException
	 */
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
	/**
	 * @param turn_id
	 * @param game_id
	 * @return
	 * @throws SQLException
	 */
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
	/**
	 * @param color
	 * @param value
	 * @param theme
	 * @return
	 * @throws SQLException
	 */
	public String getCardImage(int color, int value, int theme) throws SQLException {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "SELECT card_image FROM Images WHERE card_color = ? AND card_value = ? AND card_theme = ?";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, color);
		pstmt.setInt(2, value);
		pstmt.setInt(2, theme);

		ResultSet rs = pstmt.executeQuery();

		return rs.toString();

	}

	// afbeelding unokaart toevoegen
	/**
	 * @param card_color
	 * @param card_value
	 * @param theme
	 * @param image
	 */
	public void insertImage(int card_color, int card_value, int theme, Blob image) {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + uri);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "INSERT INTO Images(card_color,card_value, image) VALUES(?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, card_color);
			pstmt.setInt(2, card_value);
			pstmt.setInt(3, theme);
			pstmt.setBlob(4, image);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Image insert card completed!");
	}

	/**
	 * @param username
	 * @return
	 * @throws SQLException
	 */
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

	// voegt kaart toe aan table van de hand van een speler
	/**
	 * @param user_id
	 * @param card_id
	 */
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
	/**
	 * @param user_id
	 * @param card_id
	 */
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

}
