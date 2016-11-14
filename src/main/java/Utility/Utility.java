package Utility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by chendanni on 16/11/9.
 */
public class Utility {
    int businessNum = 40;
    int firstNum = 80;
    int secondNum = 100;
    int noneNum = 20;
    int[] busssec = {1};
    int[] firstsec = {2,3};
    int[] secondsec = {4,5,6,7,8};
    int[] nonesec = {4,5,6,7,7};
    String[] date = {"2016-11-11 00:00:00","2016-11-12 00:00:00","2016-11-13 00:00:00",
            "2016-11-14 00:00:00","2016-11-15 00:00:00","2016-11-16 00:00:00","2016-11-17 00:00:00"};

    public void handleTrainInfo(){
        String path = "src/main/doc/routes.txt";
        String outpath = "src/main/doc/train_info.txt";
        try{
            File filename = new File(path);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);

            File writeto = new File(outpath);
            BufferedWriter wr = new BufferedWriter(new FileWriter(writeto));


            String line = "";
//            line = br.readLine();
            while((line = br.readLine()) != null){
                System.out.print(line + "\n");
                String[] array = line.split(" ");
                wr.write(array[0] + "\n");
            }
            br.close();
            wr.flush();
            wr.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handleSections(){
        String path = "src/main/doc/routes.txt";
        String outpath = "src/main/doc/sections.txt";
        try{
            File filename = new File(path);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);

            File writeto = new File(outpath);
            BufferedWriter wr = new BufferedWriter(new FileWriter(writeto));

            String line = "";
            while((line = br.readLine()) != null){
                String[] array = line.split(" ");
                if (array.length < 2){
                    System.out.print("route error \n");
                    return;
                }
                String[] sections = array[1].split("-");

                for (int i = 0;i < sections.length - 1;i++){
                    wr.write(sections[i] + " " + sections[i + 1] + " "
                    + array[0] + " " + i + " " + 2 + "\n");
                }
            }
            br.close();
            wr.flush();
            wr.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handleCarriages(){
        String path = "src/main/doc/routes.txt";
        String outpath = "src/main/doc/carriages.txt";
        try{
            File filename = new File(path);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);

            File writeto = new File(outpath);
            BufferedWriter wr = new BufferedWriter(new FileWriter(writeto));

            String line = "";
            while((line = br.readLine()) != null){
                String[] array = line.split(" ");
                wr.write(array[0] + " " + 1 + " " + 40 + " " + 0 + "\n");
                wr.write(array[0] + " " + 2 + " " + 80 + " " + 1 + "\n");
                wr.write(array[0] + " " + 3 + " " + 80 + " " + 1 + "\n");
                wr.write(array[0] + " " + 4 + " " + 100 + " " + 2 + "\n");
                wr.write(array[0] + " " + 5 + " " + 100 + " " + 2 + "\n");
                wr.write(array[0] + " " + 6 + " " + 100 + " " + 2 + "\n");
                wr.write(array[0] + " " + 7 + " " + 100 + " " + 2 + "\n");
                wr.write(array[0] + " " + 8 + " " + 100 + " " + 2 + "\n");
                wr.write(array[0] + " " + 4 + " " + 20 + " " + 3 + "\n");
                wr.write(array[0] + " " + 5 + " " + 20 + " " + 3 + "\n");
                wr.write(array[0] + " " + 6 + " " + 20 + " " + 3 + "\n");
                wr.write(array[0] + " " + 7 + " " + 20 + " " + 3 + "\n");
                wr.write(array[0] + " " + 8 + " " + 20 + " " + 3 + "\n");
            }
            br.close();
            wr.flush();
            wr.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<JSONArray> getSections(){
        ArrayList<JSONArray> res = new ArrayList<JSONArray>();
        String path = "src/main/doc/sections.txt";
        try{
            File filename = new File(path);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);

            String line = "";
            JSONArray singleTrain = new JSONArray();
            boolean fistLineRead = false;
            while((line = br.readLine()) != null){

                String[] array = line.split(" ");
                if (array.length < 4){
                    System.out.print("section error \n");
                    return res;
                }
                String train_number = array[2];
                if(array[3].equals("")){
                    System.out.print("section number error  \n");
                    return res;
                }
                int sectionNum = Integer.parseInt(array[3]);

                JSONObject t = new JSONObject();
                t.put("train_number",train_number);
                t.put("section",sectionNum);
                if (!fistLineRead){
                    singleTrain.add(t);
                    fistLineRead = true;
                }else if (sectionNum == 0){
                    res.add(singleTrain);
                    singleTrain = new JSONArray();
                    singleTrain.add(t);
                }else{
                    singleTrain.add(t);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public int[] getCarriage(int type){
        int[] busssec = {1};
        int[] firstsec = {2,3};
        int[] secondsec = {4,5,6,7,8};
        int[] nonesec = {4,5,6,7,7};
        switch (type){
            case 0:
                return busssec;
            case 1:
                return firstsec;
            case 2:
                return secondsec;
            case 3:
                return nonesec;
            default:
                return null;
        }
    }
    public void handleSeats(int type){
        String kind = "";
        int[] carriage = getCarriage(type);
        int seats = 0;
        switch (type){
            case 0:
                kind = "business";
                seats = businessNum;
                break;
            case 1:
                kind = "first";
                seats = firstNum;
                break;
            case 2:
                kind = "second";
                seats = secondNum;
                break;
            case 3:
                kind = "none";
                seats = noneNum;
                break;
            default:
                carriage = null;
        }
        String writePath = "src/main/doc/"+ kind +"_seats_loader.txt";
        try{
            File writeto = new File(writePath);
            BufferedWriter wr = new BufferedWriter(new FileWriter(writeto));

            ArrayList<JSONArray> sections_info = getSections();
            for (int i = 0;i < sections_info.size();i++){
                JSONArray train = sections_info.get(i);
                String train_number = ((JSONObject)train.get(0)).getString("train_number");
                for (int j = 0;j < carriage.length;j++){
                    for (int k = 0;k < date.length;k++){
                        for (int m = 1;m <= seats;m++){
                            for (int n = 0;n < train.size();n++){

                                String secNum = ((JSONObject)train.get(n)).getString("section");
                                wr.write(train_number + "\t" + carriage[j] + "\t" + m + "\t"
                                + date[k] + "\t" + secNum + "\t" + "0" + "\n");
                            }
                        }
                    }
                }

            }

            wr.flush();
            wr.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.print("done " + kind + "\n");
    }

    public static void main(String[] args){
        Utility utility = new Utility();
//        utility.handleTrainInfo();
//        utility.handleSections();
//        utility.handleCarriages();
        utility.handleSeats(0);
        utility.handleSeats(1);
        utility.handleSeats(2);
        utility.handleSeats(3);
    }

}
