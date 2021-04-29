package sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class FileServices {
    private static BufferedReader br;
    private static BufferedWriter bw;

    //метод для записи истории в файл на клиенте
    public static void writeHistory(String msg, String nickname) {
        StringBuilder sb = new StringBuilder("src/sample/history/"+nickname).append("-history.txt");
        String filePath= sb.toString();
        sb=null;
        File file = new File(filePath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            String tmp=null;
            br=new BufferedReader(new FileReader(file));
            bw = new BufferedWriter(new FileWriter(file,true));
            if ((tmp = br.readLine()) != null) {
                bw.newLine();
            }
            bw.write(msg);
            bw.close();
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    //метод для отображения поледних {n} сообщений после аутентификации
    //работает только для английской раскладки
    public static String getHistoryFromFile(String nickname, int n){
        File file = new File("src/sample/history/"+nickname+"-history.txt");
        String result = null;
        if(!file.exists()){
            return result;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long length = file.length() - 1;
            int readLines = 0;
            StringBuilder sb = new StringBuilder();
            for (long i = length; i >= 0 ; i--) {
                raf.seek(i);
                char c = (char) raf.read();

                if (c == '\n'){
                    readLines++;
                    if (readLines == n){
                        break;
                    }
                }
                sb.append(c);
            }
            result=sb.reverse().toString();
            byte [] bytes = result.getBytes("ISO-8859-1");
            result = new String(bytes, "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    public static long getFilePointer(String nickname, int n){
        File file = new File("src/sample/history/"+nickname+"-history.txt");
        long result = 0;
        if(!file.exists()){
            return result;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long length = file.length()-1;
            int readLines = 0;
            for (long i = length; i >= 0 ; i--) {
                raf.seek(i);
                raf.read();

                if (raf.read() == '\n'){
                    readLines++;
                    result=i+1;
                    if (readLines == n){
                        break;
                    }
                }

            }
            if(readLines<n){
                result=0;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
    public static String getLastMessagesUsePointer(int numberLastMsg, long pointer, String nickname){
        File file = new File("src/sample/history/"+nickname+"-history.txt");
        StringBuilder sb = new StringBuilder();
        if(!file.exists()){
            return null;
        }
        else{
            try {
                br=new BufferedReader(new FileReader(file));
                br.skip(pointer);
                String str = null;
                while((str=br.readLine())!=null){
                    sb.append(str).append("\n");
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            return sb.toString();
        }

    }

    //подсчитывает количество строк в файле, делался в тестовых целях
    public static int getStringCount(String nickname){
        File file = new File("src/sample/history/"+nickname+"-history.txt");
        int result=0;
        if(!file.exists()){
            return result;
        }

        try {
            br = new BufferedReader(new FileReader(file));
            String str=null;
            while((str=br.readLine())!=null){
                result++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //метод для отображения поледних {n} сообщений после аутентификации
    //в начале нужно прочитать всю историю сообщений
    public static String getLastMessages(int numberLastMsg, String nickname){
        List<String> msgList = new LinkedList<>();
        File file = new File("src/sample/history/"+nickname+"-history.txt");
        if(!file.exists()){
            return null;
        }
        else{
            try {
                br=new BufferedReader(new FileReader(file));
                String str =null;
                int msgCount =0;
                while((str=br.readLine())!=null){
                    if ((++msgCount) > numberLastMsg) {
                        msgList.remove(0);
                    }
                    msgList.add(str);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            for(int i =0;i<msgList.size();i++){
                sb.append(msgList.get(i)).append("\n");
            }
            return sb.toString();
        }

    }

    public static void close(){
        try {
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
