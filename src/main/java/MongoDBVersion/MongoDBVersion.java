package MongoDBVersion;

import Utility.HandleMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user on 16/11/13.
 */
public class MongoDBVersion {
    HandleMongoDB handleMongoDB = new HandleMongoDB();
    JSONArray availseats = new JSONArray();

    public String getDBName(int type){
        switch (type){
            case 0:
                return "business_seats";
            case 1:
                return "first_seats";
            case 2:
                return "second_seats";
            case 3:
                return "none_seats";
            default:
                return "err";
        }
    }

    public int consultSeats(int type,String date,String train_number,int startsec, int endesc){
        String DBName = getDBName(type);
        int availCount = 0;
        int s = 0;
        boolean fit = true;
        availseats = new JSONArray();

        MongoCursor<Document> seats = handleMongoDB.findSeatsByTrainAndDate(DBName,train_number,date);
        while (seats.hasNext()){
            Document seat = seats.next();
            List<Document> sections = (List<Document>) seat.get("sections");
            for (int j = 0;j < sections.size();j++){
                int section_number = sections.get(j).getInteger("section_number");
                if (section_number == startsec){
                    s = j;
                    break;
                }
            }
            for (int j = s;j < s + endesc - startsec;j++){
                int occupied = sections.get(j).getInteger("occupied");
                if (occupied == 1){
                    fit = false;
                }
            }
            if (fit == true){
                JSONObject obj = new JSONObject();
                obj.put("carriage",seat.getInteger("carriage"));
                obj.put("seat_number",seat.getInteger("seat_number"));
                availseats.add(obj);
                availCount++;
            }else {
                fit = true;
            }
        }

        return availCount;
    }

    public void consultTickets(String date, String start, String end){
        ArrayList<String> trainsFit = new ArrayList<String>();
        ArrayList<Integer> startFit = new ArrayList<Integer>();
        ArrayList<Integer> endFit = new ArrayList<Integer>();
        MongoCursor<Document> it = handleMongoDB.findSections();
        while(it.hasNext()){
            Document secItem = it.next();
            String trainNum = secItem.getString("train_number");
            List<Document> sections = (List<Document>) secItem.get("sections");
            int s = -1;
            int e = -1;
            for (int i = 0;i < sections.size();i++){
                Document item = sections.get(i);
                if (item.getString("start").equals(start)){
                    s = item.getInteger("section_number");
                }
                if (item.getString("end").equals(end)){
                    e = item.getInteger("section_number");
                }
            }
            if ((s != -1)&&(e != -1)&&(s <= e)){
                trainsFit.add(trainNum);
                startFit.add(s);
                endFit.add(e);
            }
        }

        ArrayList<Integer> bussinessNum = new ArrayList<Integer>();
        ArrayList<Integer> firstNum = new ArrayList<Integer>();
        ArrayList<Integer> secondNum = new ArrayList<Integer>();
        ArrayList<Integer> noneNum = new ArrayList<Integer>();

        for (int i = 0;i < trainsFit.size();i++){
            int bu = consultSeats(0,date,trainsFit.get(i),startFit.get(i),endFit.get(i));
            int f = consultSeats(1,date,trainsFit.get(i),startFit.get(i),endFit.get(i));
            int s = consultSeats(2,date,trainsFit.get(i),startFit.get(i),endFit.get(i));
            int n = consultSeats(3,date,trainsFit.get(i),startFit.get(i),endFit.get(i));
            bussinessNum.add(bu);
            firstNum.add(f);
            secondNum.add(s);
            noneNum.add(n);
        }


        for (int i = 0;i < trainsFit.size();i++){
            System.out.println(trainsFit.get(i));
            System.out.println("    商务座剩余: " + bussinessNum.get(i));
            System.out.println("    一等座剩余: " + firstNum.get(i));
            System.out.println("    二等座剩余: " + secondNum.get(i));
            System.out.println("    无座座剩余: " + noneNum.get(i));
        }
    }

    public String getTypeName(int type){
        switch (type){
            case 0:
                return "商务座";
            case 1:
                return "一等座";
            case 2:
                return "二等座";
            case 3:
                return "无座";
            default:
                return "";

        }
    }

    public boolean buyTickets(String username, String personalID, String train_number, String start, String end, String date,int type,int num){
        int startNum = -1;
        int endNum = -1;
        String typeName = getTypeName(type);
        Document sec = handleMongoDB.findSectionByTrainNum(train_number);
        List<Document> secs = (List<Document>) sec.get("sections");
        for (int i = 0;i < secs.size();i++){
            Document temp = secs.get(i);
            if (temp.getString("start").equals(start)){
                startNum = temp.getInteger("section_number");
            }else if (temp.getString("end").equals(end)){
                endNum = temp.getInteger("section_number");
            }
        }
        if ((startNum == -1)||(endNum == -1)){
            System.out.println("something err\n");
            return false;
        }

        int left = consultSeats(type,date,train_number,startNum,endNum);
        JSONArray seatsAvail = availseats;
        if ((left < num)||(left <= 0)){
            System.out.println("票已售完,或订票数量超过剩余数量\n");
            return false;
        } else {
            System.out.println("购票成功:");
            for (int i = 0;i < num;i++){
                JSONObject temp = (JSONObject) seatsAvail.get(i);
                boolean update = handleMongoDB.updatePurchaseInfo(username,personalID,train_number,temp.getInt("carriage"),temp.getInt("seat_number"),start,end,startNum,endNum,date,type);

                if (!update){
                    return false;
                }
                System.out.println("-----------------------------------");
                System.out.println(typeName + " 车厢号: " + temp.getString("carriage") + " | 座位号: " + temp.getString("seat_number"));
                System.out.println(start + "    至    " + end);
                System.out.println("发车时间" + date);
                System.out.println("-----------------------------------");
            }
        }

        return true;
    }
}
