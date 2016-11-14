/**
 * Created by chendanni on 16/11/12.
 */
package MongoDBVersion;

import Utility.HandleMongoDB;
import com.mongodb.client.MongoDatabase;

public class starter {
    static String startPlace = "苏州北";
    static String endPlace = "沧州西";
    static String date = "2016-11-11 00:00:00";
    static String name = "user1";
    static String personalid = "3232****2545";



    public static void main(String[] args){
//        HandleMongoDB handleMongoDB = new HandleMongoDB();
//        handleMongoDB.createTrainsInfo();
//        handleMongoDB.createSections();
//        handleMongoDB.createSeats(0);
//        handleMongoDB.createSeats(1);
//        handleMongoDB.createSeats(2);
//        handleMongoDB.createSeats(3);
//        handleMongoDB.createPurchaseInfo();
        MongoDBVersion mongoDBVersion = new MongoDBVersion();
        mongoDBVersion.consultTickets(date, startPlace, endPlace);

//        mongoDBVersion.buyTickets(name,personalid,"G146",startPlace,endPlace,date,0,3);
    }
}
