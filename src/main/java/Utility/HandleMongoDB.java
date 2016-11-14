package Utility;

/**
 * Created by user on 16/11/12.
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import net.sf.json.JSONArray;
import org.bson.Document;
import org.bson.codecs.IntegerCodec;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HandleMongoDB {

    MongoDatabase mongoDatabase;
    FileReader fileReader = new FileReader();
    int[] seatsNum = {40,80,100,30};
    int[][] carriages = {{1},{2,3},{4,5,6,7,8},{4,5,6,7,8}};
    String[] date = {"2016-11-11 00:00:00","2016-11-12 00:00:00","2016-11-13 00:00:00",
            "2016-11-14 00:00:00","2016-11-15 00:00:00","2016-11-16 00:00:00","2016-11-17 00:00:00"};

    public HandleMongoDB(){
        connectMongoDB();
    }

    public void connectMongoDB(){
        try{
            // 连接到 mongodb 服务
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

            // 连接到数据库
            mongoDatabase = mongoClient.getDatabase("ticket_system");
            System.out.println("Connect to database successfully");

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void createTrainsInfo(){
        try{
            MongoCollection<Document> collection = mongoDatabase.getCollection("trains_info");
            collection.drop();
            mongoDatabase.createCollection("trains_info");
            collection = mongoDatabase.getCollection("trains_info");

            ArrayList<String> train_numbers = fileReader.readByLine("src/main/doc/train_info.txt");
            List<Document> documents = new ArrayList<Document>();

            for (int i = 0;i < train_numbers.size();i++){
                Document document = new Document("train_number",train_numbers.get(i));
                documents.add(document);
            }
            collection.insertMany(documents);


        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }
    }


    public void createSections(){
        String dbName = "sections";
        try{
            MongoCollection<Document> collection = mongoDatabase.getCollection(dbName);
            collection.drop();
            mongoDatabase.createCollection(dbName);
            collection = mongoDatabase.getCollection(dbName);

            ArrayList<String> sections = fileReader.readByLine("src/main/doc/sections.txt");
            List<Document> documents = new ArrayList<Document>();

            System.out.print(sections.size() + "\n");


            String train_number = "G5";
            List<Document> ss = new ArrayList<Document>();
            for (int i = 0;i < sections.size();i++){
                String[] secs = sections.get(i).split(" ");
                String start = secs[0];
                String end = secs[1];
                String trainNum = secs[2];
                int sectionNum = Integer.parseInt(secs[3]);
                Document se_info = new Document();
                se_info.append("start",start);
                se_info.append("end",end);
                se_info.append("section_number",sectionNum);


                if (trainNum.equals(train_number)){
                    ss.add(se_info);
                }else{
                    Document section_detail = new Document();
                    section_detail.append("train_number",train_number);
                    section_detail.append("sections",ss);
                    documents.add(section_detail);

                    train_number = trainNum;
                    ss = new ArrayList<Document>();
                    ss.add(se_info);
                }
            }
            Document section_detail = new Document();
            section_detail.append("train_number",train_number);
            section_detail.append("sections",ss);
            documents.add(section_detail);
            collection.insertMany(documents);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createPurchaseInfo(){
        try{
            MongoCollection<Document> collection = mongoDatabase.getCollection("purchase_info");
            collection.drop();
            mongoDatabase.createCollection("purchase_info");
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public String getName(int type){
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
                return "";
        }
    }

    public void createSeats(int type){
        String dbName = getName(type);
        int seats_number = seatsNum[type];
        int[] carrs = carriages[type];
        ArrayList<String> train_number = fileReader.readByLine("src/main/doc/train_info.txt");

        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(dbName);
            collection.drop();
            MongoCollection<Document> section_infos = mongoDatabase.getCollection("sections");
            mongoDatabase.createCollection(dbName);
            collection = mongoDatabase.getCollection(dbName);

            List<Document> documents = new ArrayList<Document>();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Calendar time = Calendar.getInstance();
            for (int i = 0; i < date.length;i++){
                time.setTime(format.parse(date[i]));
                for (int j = 0;j < train_number.size();j++){
                    String trainNum = train_number.get(j);
                    for (int c = 0; c < carrs.length;c++){
                        int carrNum = carrs[c];
                        BasicDBObject query = new BasicDBObject();
                        query.put("train_number",trainNum);
                        MongoCursor<Document> it = section_infos.find(query).iterator();
                        Document next = new Document();

                        List<Document> secs = new ArrayList<Document>();
                        if(it.hasNext()){
                            next = it.next();
                            List<Document> sec = new ArrayList<Document>();
                            sec = (List<Document>)next.get("sections");
                            for (int nu = 0;nu < sec.size();nu++){
                                Document temp = sec.get(nu);
                                Document sec_item = new Document();
                                sec_item.append("section_number",temp.getInteger("section_number"));
                                System.out.print(trainNum + " " + temp.getInteger("section_number") + "\n");
                                sec_item.append("occupied",0);
                                secs.add(sec_item);
                            }

                        }else {
                            System.out.print("something error \n");
                            return;
                        }
                        for (int m = 1;m <=seats_number;m++){
                            Document seat = new Document();
                            seat.append("train_number",trainNum);
                            seat.append("carriage",carrNum);
                            seat.append("seat_number",m);
                            seat.append("date",time.getTime());
                            seat.append("sections",secs);
                            documents.add(seat);
                        }
                    }
                }
            }

            collection.insertMany(documents);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MongoCursor<Document> findSections(){
        MongoCursor<Document> it = null;
        try{
            MongoCollection<Document> section_infos = mongoDatabase.getCollection("sections");

//            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//            Calendar time = Calendar.getInstance();
//            time.setTime(format.parse(date));
//            BasicDBObject query = new BasicDBObject();
//            query.put("date",time.getTime());
            it = section_infos.find().iterator();
        }catch(Exception e){
            e.printStackTrace();
        }
        return it;
    }

    public Document findSectionByTrainNum(String train_number){
        Document section = new Document();
        try{
            MongoCollection<Document> section_infos = mongoDatabase.getCollection("sections");

            BasicDBObject query = new BasicDBObject();
            query.put("train_number",train_number);
            MongoCursor<Document> it = section_infos.find(query).iterator();
            if (it.hasNext())
                section = it.next();
        }catch(Exception e){
            e.printStackTrace();
        }
        return section;
    }

    public MongoCursor<Document> findSeatsByTrainAndDate(String tableName, String train_number,String date){
        MongoCursor<Document> it = null;
        try{
            MongoCollection<Document> seats = mongoDatabase.getCollection(tableName);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Calendar time = Calendar.getInstance();
            time.setTime(format.parse(date));

            BasicDBObject query = new BasicDBObject();
            query.put("train_number",train_number);
            query.put("date",time.getTime());
            it = seats.find(query).iterator();

        }catch(Exception e){
            e.printStackTrace();
        }
        return it;
    }

    public String getTableName(int type){
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

    public boolean updatePurchaseInfo(String username, String personalID, String train_number,
                                      int carriage, int seat_number, String start, String end,
                                      int startNum, int endNum, String date,int type){

        String updateTable = getTableName(type);
        Document section = new Document();
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Calendar time = Calendar.getInstance();
            time.setTime(format.parse(date));

            MongoCollection<Document> purchase_info = mongoDatabase.getCollection("purchase_info");
            MongoCollection<Document> seats = mongoDatabase.getCollection(updateTable);
            Document purchase = new Document();
            purchase.append("username",username);
            purchase.append("personalID",personalID);
            purchase.append("date",time.getTime());
            purchase.append("train_number",train_number);
            purchase.append("carriage",carriage);
            purchase.append("seat_number",seat_number);
            purchase.append("start",start);
            purchase.append("end",end);
            purchase.append("seat_type",type);

            BasicDBObject query = new BasicDBObject();
            query.put("train_number",train_number);
            query.put("carriage",carriage);
            query.put("seat_number",seat_number);
            query.put("date",time.getTime());
            MongoCursor<Document> it = seats.find(query).iterator();
            Document seat = new Document();
            if (it.hasNext())
                seat = it.next();
            List<Document> beforeUp = (List<Document>) seat.get("sections");
            List<Document> afterUp = new ArrayList<Document>();
            for (int i = 0;i < beforeUp.size();i++){
                int section_number = beforeUp.get(i).getInteger("section_number");
                if ((section_number >= startNum)||(section_number <= endNum)){
                    Document temp = new Document();
                    temp.append("section_number",section_number);
                    temp.append("occupied",1);
                    afterUp.add(temp);
                }else{
                    afterUp.add(beforeUp.get(i));
                }
            }
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.append("$set", new BasicDBObject().append("sections", afterUp));
            seats.updateOne(query,newDocument);
            purchase_info.insertOne(purchase);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
