package databaseServer;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;

import dispatcher.dispatcherInterfaceImpl;

public class MainTest {

	/**
	 * @param args
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws AlreadyBoundException
	 */
	public static void main(String[] args)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			SQLException, IOException, InvalidKeyException, SignatureException, AlreadyBoundException {
		String uri = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\uno";

		dispatcherInterfaceImpl dispatcherInterfaceImpl = new dispatcherInterfaceImpl();

		System.out.println("einde");
	}
}