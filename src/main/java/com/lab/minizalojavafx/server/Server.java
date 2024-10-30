package com.lab.minizalojavafx.server;

import com.lab.minizalojavafx.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private static Server server;
    private List<ClientHandler> clients = new ArrayList<>();

    private Server() throws IOException {
        serverSocket = new ServerSocket(3001);
    }

    public static Server getInstance() throws IOException {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public void makeSocket() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                System.out.println("Client socket accepted: " + socket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}