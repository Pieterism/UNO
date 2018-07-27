package main;

import Interfaces.serverInterfaceImpl;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    private void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(1200);
            registry.rebind("UNO", (Remote) new serverInterfaceImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server is ready!");

//        try {
//        	Registry registry = LocateRegistry.getRegistry("localhost", 1100);
//            db = (dbInterface) registry.lookup("UNO");
//            
//            serverInterfaceImpl server = new serverInterfaceImpl();
//            
//            
////            System.out.println("get db");
////            System.out.println(db.getAll());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        System.out.println("Server connected to database");
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.startServer();
    }

}