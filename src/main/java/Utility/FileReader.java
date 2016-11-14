package Utility;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by user on 16/11/12.
 */
public class FileReader {

    public ArrayList<String> readByLine(String path){
        ArrayList<String> res = new ArrayList<String>();

        try{
            File filename = new File(path);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);

            String line = "";
            while((line = br.readLine()) != null){
                res.add(line);
            }
            br.close();


        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
