package com.lab.minizalojavafx.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Server {
    private static Server instance;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets = new ArrayList<>();
    private Consumer<String> messageReceivedCallback;
    private Map<Socket, String> clientMap = new HashMap<>();

    private Server() {
    }

    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void makeSocket() throws IOException {
        serverSocket = new ServerSocket(3001);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            clientSockets.add(clientSocket);
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
            String username = in.readUTF();
            clientMap.put(clientSocket, username);
            broadcastUserList();
            while (true) {
                String message = in.readUTF();
                if (message.startsWith("ADD_USER-")) {
                    String userName = message.split("-")[1];
                    clientMap.put(clientSocket, userName);
                    broadcastUserList();
                } else if (message.startsWith("REMOVE_USER-")) {
                    clientMap.remove(clientSocket);
                    broadcastUserList();
                    break;
                } else {
                    messageReceivedCallback.accept(message);
                    broadcastMessage(message, clientSocket);
                }

                if (message.startsWith("IMAGE-")) {
                    broadcastMessage(message, clientSocket);
                } else {
                    messageReceivedCallback.accept(message);
                    broadcastMessage(message, clientSocket);
                }

            }
        } catch (IOException e) {
            clientSockets.remove(clientSocket);
            clientMap.remove(clientSocket);
            broadcastUserList();
        }
    }

    private void broadcastUserList() {
        String userListMessage = "USER_LIST-" + String.join(",", clientMap.values());
        for (Socket clientSocket : clientMap.keySet()) {
            sendToClient(clientSocket, userListMessage);
        }
    }

    private void sendToClient(Socket clientSocket, String message) {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            clientMap.remove(clientSocket);
        }
    }

    public void broadcastMessage(String message) {
        for (Socket clientSocket : clientSockets) {
            try {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF(message);
            } catch (IOException e) {
                clientSockets.remove(clientSocket);
            }
        }
    }

    public void broadcastMessage(String message, Socket senderSocket) {
        for (Socket clientSocket : clientSockets) {
            if (!clientSocket.equals(senderSocket)) {
                try {
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                    out.writeUTF(message);
                } catch (IOException e) {
                    clientSockets.remove(clientSocket);
                }
            }
        }
    }


    public void setOnMessageReceived(Consumer<String> callback) {
        this.messageReceivedCallback = callback;
    }
}
