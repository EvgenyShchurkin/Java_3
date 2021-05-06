package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    Socket client;
    Server server;
    DataInputStream in;
    DataOutputStream out;
    String nickname;
    List<String> blackList;
    ExecutorService exService;



    public ClientHandler(Server server, Socket client){
        try {
            this.server=server;
            this.client = client;
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            exService=Executors.newSingleThreadExecutor();

            exService.execute(()->{
                try {
                    // auth login pass
                    while (true){
                        client.setSoTimeout(20*1000);
                        String str = in.readUTF();
                        if(str.startsWith("/auth")){
                            String [] tokens = str.split(" ");
                            String nick = AuthService.getNicknameByLoginAndPassword(tokens[1],tokens[2]);
                            if(nick!=null){
                                if(!server.isNickBusy(nick)) {
                                    client.setSoTimeout(0);
                                    sendMsg("/authOk " +nick);
                                    setNickname(nick);
                                    setBlacklist(nick);
                                    server.subscribe(this);
                                    System.out.printf("Client [%s] has just connected to server\n", client.getInetAddress());
                                    break;
                                }
                                else {
                                    sendMsg("This user ["+nick+"] has already logged in");
                                }
                            }
                            else{
                                System.out.printf("Client [%s] trying to connected to server\n", client.getInetAddress());
                                sendMsg("/Auth Wrong");
                            }
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if(str.startsWith("/")) {
                            if ("/end".equals(str)) {
                                out.writeUTF("/ServerClosed");
                                System.out.printf("Client [%s] disconnected\n", client.getInetAddress());
                                break;
                            }
                            if(str.startsWith("/black ")){
                                blackListOperations(str);
                            }
                            if(str.equals("/getHistory")){
                                List<String> historyList=AuthService.getHistoryListByNickname(nickname);
                                sendMsg("----History Loaded----");
                                for (int i = 0; i < historyList.size() ; i++) {
                                    sendMsg(historyList.get(i));
                                }
                                historyList.clear();
                            }
                            if (str.startsWith("/getBlack")){
                                sendMsg("Your blacklist: ");
                                for (int i = 0; i < blackList.size() ; i++) {
                                    sendMsg(blackList.get(i));
                                }
                            }
                        }

                        else if(str.startsWith("@")) {
                            String[] partsMsg = str.split(" ",2);
                            server.sendPrivateMsg(this, partsMsg[0].substring(1, partsMsg[0].length()),partsMsg[1]);
                        }

                        else {
                            System.out.println(str);
//                            AuthService.addRecordToDB(nickname,str);
                            server.broadcastMsg(nickname+": "+str, this);
                        }
                    }
                }catch (SocketTimeoutException e){
                    System.out.println("Timeout "+client.getInetAddress());
                }catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    closeHandler();
                    exService.shutdown();
                }

            });

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void blackListOperations(String str) {
        String[] partsMsg=str.split(" ");
        if(partsMsg.length!=2){
            sendMsg("Service message: If you want to add someone to your blacklist you need to specify " +
                    "a username correctly after command /blacklist {username}.");
        }
        else {
            partsMsg = str.split(" ", 2);
            if(nickname.equals(partsMsg[1])){
                sendMsg("Service message: You can't add yourself to blacklist.");
                return;
            }
            if (!AuthService.getBlackListByNickname(nickname).contains(partsMsg[1])) {
                if (AuthService.addUserToBlacklist(nickname, partsMsg[1]) == 1) {
                    blackList.add(partsMsg[1]);
                    sendMsg("You added user [" + partsMsg[1] + "] to blacklist");
                    server.getLogger().debug("User "+nickname+" added "+partsMsg[1]+" to his blacklist");
                } else {
                    sendMsg("Something went wrong during adding user [" + partsMsg[1] + "] to your blacklist");
                    server.getLogger().debug("Something went wrong during addition user [" + partsMsg[1] + "] to blacklist of "+nickname);
                }
            } else {
                if (AuthService.deleteUserFromBlacklist(nickname, partsMsg[1]) == 1) {
                    blackList.remove(partsMsg[1]);
                    sendMsg("User [" + partsMsg[1] + "] not in your blacklist anymore");
                    server.getLogger().debug("User "+nickname+" deleted "+partsMsg[1]+" from his blacklist");
                } else {
                    sendMsg("Something went wrong during deleting user [" + partsMsg[1] + "] from your blacklist");
                    server.getLogger().debug("Something went wrong during deletion user [" + partsMsg[1] + "] from blacklist of "+nickname);
                }
            }
        }
    }

    private void setBlacklist(String nick) {
        this.blackList = AuthService.getBlackListByNickname(nick);
    }


    private void setNickname(String nick) {
        this.nickname=nick;
    }

    public String getNickname() {
        return nickname;
    }
    public List<String> getBlackList(){
        return blackList;
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void closeHandler(){
        try{
            if(in!=null) {
                in.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            if(out!=null) {
                out.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            if(client!=null||!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.unsubscribe(this);
    }
    public InetAddress getClientIPAddress(){
        return client.getInetAddress();
    }


}
