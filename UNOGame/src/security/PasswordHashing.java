package security;

public class PasswordHashing {
	private static int workload = 12;

	/**
	 * @param password_plaintext
	 * @return
	 */
	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return hashed_password;
	}

	/**
	 * @param password_plaintext
	 * @param stored_hash
	 * @return
	 */
	public static boolean checkPassword(String password_plaintext, String stored_hash) {
		boolean result = false;

		result = BCrypt.checkpw(password_plaintext, stored_hash);

		return result;

	}
}
