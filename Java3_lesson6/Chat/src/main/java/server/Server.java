package server;


import org.apache.log4j.Logger;

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
                 new ClientHandler(this,client);
             }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            AuthService.disconnect();
            this.disconnect(serverSocket,client);
            }
        }

    public void disconnect(ServerSocket serverSocket, Socket client){
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
    public void broadcastMsg(String msg, ClientHandler fromUser){
        for(ClientHandler client: users){
            if (!fromUser.getBlackList().contains(client.getNickname()) && !client.getBlackList().contains(fromUser.getNickname())){
                client.sendMsg(msg);
                AuthService.addRecordToDB(fromUser.getNickname(),client.getNickname(),msg);
            }
        }
    }

    public void subscribe(ClientHandler client){
        users.add(client);
        broadcastClientsList();
    }
    public void unsubscribe(ClientHandler client){
        users.remove(client);
        broadcastClientsList();
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
                if(!clients.getBlackList().contains(fromUser.getNickname()) && !fromUser.getBlackList().contains(clients.getNickname())) {
                    clients.sendMsg(fromUser.getNickname() + " sent private message for you: " + message);
                    AuthService.addRecordToDB(fromUser.getNickname(),clients.getNickname(),fromUser.getNickname() + " sent private message for you: " + message);
                    fromUser.sendMsg("You sent private message for " + toUser + ": " + message);
                    AuthService.addRecordToDB(fromUser.getNickname(),fromUser.getNickname(),"You sent private message for " + toUser + ": " + message);
                    return;
                }
                else {
                    fromUser.sendMsg("Message receiver ["+toUser+"] in your blacklist or he put you in his blacklist");
                    return;
                }
            }
        }
        fromUser.sendMsg("Warning: You're trying to send message to the user who doesn't exist");
    }

    public void broadcastClientsList(){
        StringBuilder clients = new StringBuilder("/clients_list ");
        for(ClientHandler c: users){
            clients.append(c.getNickname()).append(" ");
        }
        clients.setLength(clients.length()-1);
        String clientsList = clients.toString();
        for(ClientHandler client: users){
            client.sendMsg(clientsList);
        }
    }


}
