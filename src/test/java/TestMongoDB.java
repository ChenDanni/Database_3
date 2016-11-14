import MongoDBVersion.MongoDBVersion;
import org.junit.Test;

/**
 * Created by user on 16/11/13.
 */
public class TestMongoDB {
    static String startPlace = "苏州北";
    static String endPlace = "沧州西";
    static String date = "2016-11-11 00:00:00";
    static String name = "user1";
    static String personalid = "3232****2545";
    MongoDBVersion mongoDBVersion = new MongoDBVersion();

    @Test
    public void testTicketConsult(){
        mongoDBVersion.consultTickets(date, startPlace, endPlace);
    }
    @Test
    public void testPurchaseTickets(){
        mongoDBVersion.buyTickets(name,personalid,"G112",startPlace,endPlace,date,0,3);
    }
}
