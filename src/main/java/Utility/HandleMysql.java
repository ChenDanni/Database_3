package Utility;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by user on 16/11/9.
 */
public class HandleMysql {
    // JDBC 驱动名及数据库 URL
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://localhost:8889/ticket_system";

    // 数据库的用户名与密码，需要根据自己的设置
    String USER = "user";
    String PASS = "123321";
    Connection conn = null;
    ResultSet rs=null;

    static String start = "";
    static String end = "上海虹桥";
    static String date = "2016-11-11 00:00:00";

    public HandleMysql(){
        connectMsql();

    }

    public void connectMsql(){
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            if (!conn.isClosed()) {
                System.out.println("Succeeded connecting to the Database!");
            } else {
                System.out.println("Falled connecting to the Database!");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public ResultSet searchMysql(String sql){
        ResultSet rs = null;
        try{
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
        return rs;
    }

    public int updateMysql(String sql){
        int re = 0;
        try{
            Statement stmt = conn.createStatement();
            re = stmt.executeUpdate(sql);
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
        return re;
    }

    public void disconnectMysql(ArrayList<ResultSet> res){
        try{
            conn.close();
            for (int i = 0;i < res.size();i++)
                res.get(i).close();
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }

}
