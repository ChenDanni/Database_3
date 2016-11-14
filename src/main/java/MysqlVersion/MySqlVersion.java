package MysqlVersion;

/**
 * Created by chendanni on 16/11/8.
 */
import Utility.HandleMysql;

import java.sql.*;
import java.util.ArrayList;
import net.sf.json.*;



public class MySqlVersion {
//    static String startPlace = "北京南";
//    static String endPlace = "上海虹桥";
//    static String date = "2016-11-11 00:00:00";
//    static String name = "user1";
//    static String personalid = "3232****2545";
    ArrayList<String> trainsFit = new ArrayList<String>();
    ArrayList<Integer> secStartFit = new ArrayList<Integer>();
    ArrayList<Integer> secEndFit = new ArrayList<Integer>();
    HandleMysql handleMysql = new HandleMysql();
    ArrayList<ResultSet> res = new ArrayList<ResultSet>();

    public boolean isSameSeat(int c1,int c2,int s1,int s2){
        if ((c1 == 0)&&(s1 == 0))
            return true;
        if ((c1 == c2)&&(s1 == s2))
            return true;
        return false;
    }
    public boolean isSeatAvail(ArrayList<Integer> se, int start, int end){
        int startL = 100;
        int endL = 100;

        if (se.size() == 0)
            return false;

        for (int i = 0;i < se.size();i++){
            if (start == se.get(i))
                startL = i;
            if (end == se.get(i)){
                endL = i;
                break;
            }
        }

        if (((endL - startL) == (end - start))&&(endL >= startL))
            return true;
        return false;
    }

    public JSONObject getType(int type){
        String message = "";
        String table = "";
        switch (type){
            case 0:
                message = "商务座";
                table = "business_seats";
                break;
            case 1:
                message = "一等座";
                table = "first_seats";
                break;
            case 2:
                message = "二等座";
                table = "second_seats";
                break;
            case 3:
                message = "无座";
                table = "none_seats";
                break;
        }
        JSONObject detail = new JSONObject();
        detail.put("message",message);
        detail.put("table",table);
        return detail;
    }

    public JSONArray ticketsAvail(String train_number,int start,int end,int type,String date){
        JSONObject detail = getType(type);
        String message = detail.getString("message");
        String table = detail.getString("table");
        System.out.print(table + "\n");
        String findBusinessSeatSql = "SELECT seat_number,carriage,section_number FROM " + table + " WHERE train_number='" + train_number +
                "' AND date='" + date + "' AND occupied='0' ORDER BY carriage ASC ,seat_number ASC, section_number ASC ";

        ResultSet conRs = handleMysql.searchMysql(findBusinessSeatSql);
        res.add(conRs);


        int availCount = 0;
        JSONArray avails_info = new JSONArray();
        ArrayList<Integer> sectionAvail = new ArrayList<Integer>();
        int c1 = 0, c2 = 0, s1 = 0, s2 = 0;
        try{
            while (conRs.next()){
                c2 = conRs.getInt("carriage");
                s2 = conRs.getInt("seat_number");
                if (isSameSeat(c1,c2,s1,s2)){
                    sectionAvail.add(conRs.getInt("section_number"));
                }
                else {
                    if (isSeatAvail(sectionAvail,start,end)){
                        availCount++;
                        JSONObject temp_avail = new JSONObject();
                        temp_avail.put("carriage",c1);
                        temp_avail.put("seat",s1);
                        avails_info.add(temp_avail);
                    }
                    sectionAvail = new ArrayList<Integer>();
                    sectionAvail.add(conRs.getInt("section_number"));
                }
                c1 = c2;
                s1 = s2;
            }
            if (isSeatAvail(sectionAvail,start,end)){
                availCount++;
                JSONObject temp = new JSONObject();
                temp.put("carriage",c1);
                temp.put("seat",s1);
                avails_info.add(temp);
            }
            System.out.print(date + " 列车: " + train_number + " ");
            System.out.print(message + "剩余" + availCount + " 张\n");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return avails_info;
    }

    //商务座0 一等座1 二等座2 无座3
    public void ticketsLeft(int type,String date){
        JSONObject detail = getType(type);
        String message = detail.getString("message");
        String table = detail.getString("table");

        for (int i = 0;i < trainsFit.size();i++){
            String temp_train_number = trainsFit.get(i);
            int temp_start = secStartFit.get(i);
            int temp_end = secEndFit.get(i);

            String findBusinessSeatSql = "SELECT seat_number,carriage,section_number FROM " + table + " WHERE train_number='" + temp_train_number +
                    "' AND date='" + date + "' AND occupied='0' ORDER BY carriage,seat_number, section_number ";

            ResultSet conRs = handleMysql.searchMysql(findBusinessSeatSql);

            res.add(conRs);
            int availCount = 0;
            ArrayList<Integer> carriages = new ArrayList<Integer>();
            ArrayList<Integer> seatsNum = new ArrayList<Integer>();

            ArrayList<Integer> sectionAvail = new ArrayList<Integer>();
            int c1 = 0, c2 = 0, s1 = 0, s2 = 0;
            try{
                while (conRs.next()){
                    c2 = conRs.getInt("carriage");
                    s2 = conRs.getInt("seat_number");
                    if (isSameSeat(c1,c2,s1,s2)){
                        sectionAvail.add(conRs.getInt("section_number"));
                    }
                    else {
                        if (isSeatAvail(sectionAvail,temp_start,temp_end)){
                            availCount++;
                            carriages.add(c1);
                            seatsNum.add(s1);
                        }
//                        System.out.println(sectionAvail.size());
                        sectionAvail = new ArrayList<Integer>();
                        sectionAvail.add(conRs.getInt("section_number"));
                    }
                    c1 = c2;
                    s1 = s2;
                }
                if (isSeatAvail(sectionAvail,temp_start,temp_end)){
                    availCount++;
                    carriages.add(c1);
                    seatsNum.add(s1);
                }
                System.out.print(date + " 列车: " + temp_train_number + " ");
                System.out.println("    " + message + "剩余" + availCount + " 张");
            }catch (SQLException e){
                e.printStackTrace();
            }

        }

    }

    public void orderTictets(String train_number, int type, int num,String startPlace,String endPlace,String date,String name,String personalid){
        int start = -1;
        int end = -1;
        JSONObject type_details = getType(type);
        JSONArray avails = new JSONArray();
        JSONArray ticks_info = new JSONArray();
        for (int i = 0;i < trainsFit.size();i++){
            if (train_number.equals(trainsFit.get(i))){
                start = secStartFit.get(i);
                end = secEndFit.get(i);
            }
        }
        avails = ticketsAvail(train_number,start,end,type,date);
        if (avails.size() < num){
            System.out.print("余票不足。。。。");
        }else {
            System.out.println("订票成功:");
            for (int i = 0;i < num;i++){
                JSONObject info = (JSONObject) avails.get(i);
                ticks_info.add(info);
                String updateSql = "INSERT INTO purchase_info (date, train_number, carriage, seat_number, name, personal_id, start, end) " +
                        "VALUES ('" + date + "','" + train_number + "','" + info.getString("carriage") + "','"
                        + info.getString("seat") + "','" + name + "','" + personalid + "','" + startPlace + "','" + endPlace +  "')";

                String updateOccupied = "UPDATE " + type_details.getString("table") + " set occupied='1' where " +
                        "train_number='" + train_number + "'AND " + "carriage='" + info.getString("carriage") + "' AND seat_number='"
                        + info.getString("seat") + "' AND date='" + date + "'";

                handleMysql.updateMysql(updateSql);
                handleMysql.updateMysql(updateOccupied);

                System.out.println("-----------------------------------");
                System.out.println(type_details.getString("message") + " 车厢号: " + info.getString("carriage") + " | 座位号: " + info.getString("seat"));
                System.out.println(startPlace + "    至    " + endPlace);
                System.out.println("发车时间" + date);
                System.out.println("-----------------------------------");
            }
        }


    }

    public void handleTickets(String date, String startPlace, String endPlace){

        try{
            String findStartFitSql;
            String findEndFitSql;

            findStartFitSql = "SELECT train_number,section_number FROM sections WHERE start=" + "'" + startPlace + "'";
            findEndFitSql = "SELECT train_number,section_number FROM sections WHERE end=" + "'" + endPlace + "'";


            ResultSet startRs = handleMysql.searchMysql(findStartFitSql);
            ResultSet endRs = handleMysql.searchMysql(findEndFitSql);
            res.add(startRs);
            res.add(endRs);
            ArrayList<String> startFit = new ArrayList<String>();
            ArrayList<String> endFit = new ArrayList<String>();

            ArrayList<Integer> secStart = new ArrayList<Integer>();
            ArrayList<Integer> secEnd = new ArrayList<Integer>();


            while(startRs.next()){
                startFit.add(startRs.getString("train_number"));
                secStart.add(startRs.getInt("section_number"));
            }
            while (endRs.next()){
                endFit.add(endRs.getString("train_number"));
                secEnd.add(endRs.getInt("section_number"));
            }
            for (int i = 0; i < startFit.size();i++){
                String startTNum = startFit.get(i);
                for (int j = 0;j < endFit.size();j++){
                    String endTNum = endFit.get(j);
                    if (startTNum.equals(endTNum)&&(secStart.get(i) <= secEnd.get(j))){
                        trainsFit.add(startTNum);
                        secStartFit.add(secStart.get(i));
                        secEndFit.add(secEnd.get(j));
                        break;
                    }
                }
            }
            ticketsLeft(0,date);
            ticketsLeft(1,date);
            ticketsLeft(2,date);
            ticketsLeft(3,date);

        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
        handleMysql.disconnectMysql(res);
    }

}
