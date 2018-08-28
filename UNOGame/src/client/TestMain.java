package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import interfaces.dispatcherInterface;
import interfaces.serverInterface;

public class TestMain {
	private void startServer() {
	    dispatcherInterface dispatcher;
	    serverInterface server;

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            dispatcher = (dispatcherInterface) registry.lookup("UNOdispatcher");
            
            int port = dispatcher.getPort();
            System.out.println(port);
            
        	registry = LocateRegistry.getRegistry("localhost", port);
        	server = (serverInterface) registry.lookup("UNOserver");
//
//        	if(server.ping()) {
//        		System.out.println("connection established!");
//        	}
			server.register("woutertje", "Password");
			server.startNewGame("myNewGame", "myNewDespription", 2);
			server.readyToStart(0, "My game");
		   	System.out.println(dispatcher.getPort());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestMain main = new TestMain();
        main.startServer();
    }
}
