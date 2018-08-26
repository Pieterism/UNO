package databaseServer;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;

public class MainTest {

	public static void main(String[] args) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, SQLException, IOException, InvalidKeyException, SignatureException {
		String uri = "D:\\Google Drive\\School\\2017-2018\\1e Semester\\Gedistribueerde Systemen\\Opdracht UNO\\GIT_UNO\\uno.db";

		dbInterfaceImpl dbInterfaceImpl = new dbInterfaceImpl(uri);
		
		dbInterfaceImpl.addUser("Pieter", "pieter");
	}
}