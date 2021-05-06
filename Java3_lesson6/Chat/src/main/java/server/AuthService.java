package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static Connection connection;
    private static Statement statement;

    public static void connect(){
        try {
          //  Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Chat/src/main/java/db/main.db");
            statement = connection.createStatement();
        } catch (/*ClassNotFoundException | */SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getNicknameByLoginAndPassword(String login, String password){
        String qwery = String.format("select nickname from users where login='%s' and password='%s'",login,password);
        try {
            ResultSet resultSet = statement.executeQuery(qwery);
            if(resultSet.next()){
                String nickname=resultSet.getString("nickname");
                resultSet.close();
                return nickname;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int addRecordToDB(String fromNick,String toNick, String message){
        int result=-1;
        String requestToAdd = String.format("INSERT INTO messages ('fromNick','toNick','message') VALUES ('%s','%s','%s');",fromNick,toNick,message);
        try {
            result = statement.executeUpdate(requestToAdd);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getHistoryListByNickname(String nickname){
        List<String> historyList =new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet =null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM messages WHERE toNick=?");
            preparedStatement.setString(1,nickname);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                historyList.add(resultSet.getString("message"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            statementClose(preparedStatement);
            resultSetClose(resultSet);
        }
        return historyList;
    }

    public static int addUserToBlacklist(String source, String blockedUser){
        int result =-1;
        try {
            String requestToAdd = String.format("INSERT INTO blocked ('nick_source','blocked_user') VALUES ('%s','%s');",source,blockedUser);
            result=statement.executeUpdate(requestToAdd);
            return result;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public static int deleteUserFromBlacklist(String source, String blockedUser){
        int result =-1;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement("DELETE FROM blocked WHERE nick_source=? AND blocked_user=?;");
            preparedStatement.setString(1, source );
            preparedStatement.setString(2,blockedUser);
            result=preparedStatement.executeUpdate();
            return result;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            statementClose(preparedStatement);
        }
        return 0;
    }
    public static List<String> getBlackListByNickname(String nickname){
        List<String> blackList =new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet =null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM blocked WHERE nick_source=?");
            preparedStatement.setString(1,nickname);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                blackList.add(resultSet.getString("blocked_user"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            statementClose(preparedStatement);
            resultSetClose(resultSet);
        }
        return blackList;
    }

    private static void resultSetClose(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void statementClose(PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
