import MysqlVersion.MySqlVersion;
import org.junit.Test;

/**
 * Created by user on 16/11/13.
 */
public class TestMysql {
    static String startPlace = "苏州北";
    static String endPlace = "沧州西";
    static String date = "2016-11-11 00:00:00";
    static String name = "user1";
    static String personalid = "3232****2545";
    MySqlVersion mySqlVersion = new MySqlVersion();


    @Test
    public void teatConsultTickets(){
        mySqlVersion.handleTickets(date,startPlace,endPlace);
    }

    @Test
    public void testOrderTickets(){
        mySqlVersion.orderTictets("G126",0,1,startPlace,endPlace,date,name,personalid);
    }




}
