package main;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import dispatcherInterfaces.dispatcherInterfaceImpl;

public class Main {

    private void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("UNOdispatcher", new dispatcherInterfaceImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Dispatcher is ready!");
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.startServer();
    }

}
