package com.lab.minizalojavafx.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private List<ClientHandler> clients;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        try {
            this.socket = socket;
            this.clients = clients;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String msg;
        try {
            while (socket.isConnected() && (msg = dataInputStream.readUTF()) != null) {
                synchronized (clients) {
                    for (ClientHandler clientHandler : clients) {
                        if (clientHandler.socket.getPort() != socket.getPort()) {
                            clientHandler.dataOutputStream.writeUTF(msg);
                            clientHandler.dataOutputStream.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket);
        } finally {
            try {
                socket.close();
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            removeClient();
        }
    }

    private void removeClient() {
        synchronized (clients) {
            clients.remove(this);
        }
    }
}