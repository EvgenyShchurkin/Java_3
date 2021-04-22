package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> users;

    public Server() {
        users =new Vector<>();
        ServerSocket serverSocket =null;
        Socket client = null;
        try {
            AuthService.connect();
            serverSocket = new ServerSocket(8089);
            System.out.println("Server has started");
             while (true){
                 client = serverSocket.accept();
                 System.out.printf("Client [%s] has just connected to server\n", client.getInetAddress());
                 new ClientHandler(this,client);
             }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            this.disconnect(serverSocket,client);
            }
        }

    private void disconnect(ServerSocket serverSocket, Socket client){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void broadcastMsg(String msg){
        for(ClientHandler client: users){
           client.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler client){
        users.add(client);
    }
    public void unsubscribe(ClientHandler client){
        users.remove(client);
    }

    public boolean isNickBusy(String nick) {
        for(ClientHandler clients: users) {
            if(clients.nickname.equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void sendPrivateMsg(ClientHandler fromUser, String toUser, String message) {
        if(fromUser.getNickname().equals(toUser)){
            fromUser.sendMsg("Warning: You're trying to send message to yourself");
            return;
        }
        for (ClientHandler clients : users) {
            if (clients.getNickname().equals(toUser)) {
                clients.sendMsg(fromUser.getNickname() + " sent private message for you: " + message);
                fromUser.sendMsg("You sent private message for " + toUser + ": " + message);
                return;
            }
        }
        fromUser.sendMsg("Warning: You're trying to send message to the user who doesn't exist");
    }
}
