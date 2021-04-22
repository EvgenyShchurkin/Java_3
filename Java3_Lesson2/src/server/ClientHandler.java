package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    Socket client;
    Server server;
    DataInputStream in;
    DataOutputStream out;
    String nickname;



    public ClientHandler(Server server, Socket client){
        try {
            this.server=server;
            this.client = client;
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            new Thread(()->{
                try {
                    // auth login pass
                    while (true){
                        String str = in.readUTF();
                        if(str.startsWith("/auth")){
                            String [] tokens = str.split(" ");
                            String nick = AuthService.getNicknameByLoginAndPassword(tokens[1],tokens[2]);
                            if(nick!=null){
                                if(!server.isNickBusy(nick)) {
                                    sendMsg("/authOk");
                                    setNickname(nick);
                                    server.subscribe(this);
                                    break;
                                }
                                else {
                                    sendMsg("This user ["+nick+"] has already logged in");
                                }
                            }
                            else{
                                sendMsg("/AuthWrong");
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
                        }
                        else if(str.startsWith("@")) {
                            String[] partsMsg = str.split(" ",2);
                            server.sendPrivateMsg(this, partsMsg[0].substring(1, partsMsg[0].length()),partsMsg[1]);
                        }
                        else {
                            System.out.println(str);
                            server.broadcastMsg(nickname+": "+str);
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                        try{
                            in.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        try{
                            out.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(this);
                    }

            }).start();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void setNickname(String nick) {
        this.nickname=nick;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
