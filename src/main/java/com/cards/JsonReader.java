package com.cards;

import com.api.CrumziApi;
import com.api.CrumziApiImpl;
import com.api.SendEmail;
import com.csvreader.CsvWriter;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;

public class JsonReader {

    private static Properties props;

  //  private static String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJUT0tFTl9UWVBFIjoiU0VTU0lPTiIsIkFQUExJQ0FUSU9OX1RZUEUiOiJTRUxMRVIiLCJVU0VSX0lEIjoiMmM5ZjkxZjQ1OWFjZWYwZjAxNTliMTQzNmIzYjAwMDQiLCJpc3MiOiJDUlVNWkkiLCJpYXQiOjE0ODQ3Mzk0NTF9.sbYrTc2k54zCWhepAlNxeEgyIYYN3ko4Zg23RTdNhrART4fL_bgVCdN6rmR7tzsJILrjIyJZcJ3k5_RlnG7DvQ";
  //  private static long date_from =1485935234027L;
   // private static long date_to  =1485935607127L;

    public static void main(String[] args) throws IOException {
        props = loadProperties();
        JsonReader J = new JsonReader();

        LocalDateTime dateTime = LocalDateTime.now();
        Long beggining = dateTime.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        final String currentDateTime = Long.toString(System.currentTimeMillis());
        String file = "D:\\\\" + currentDateTime + ".csv";
        J.process(props.getProperty("TOKEN"), beggining, currentDateTime);
        SendEmail s = new SendEmail();
        s.send(props.getProperty("smtp_user"), props.getProperty("smtp_password"),
                props.getProperty("smtp_host"), props.getProperty("smtp_port"),
                props.getProperty("emailto"),file , currentDateTime + ".csv");


    }


    private void process(String TOKEN, long date_from, String filename) throws IOException {
        CrumziApi api = new CrumziApiImpl();
        List<com.clients.List> cards = api.getBuyerCards(TOKEN,date_from);
        //while (!cards.isEmpty()) {
            for (com.clients.List card : cards) {
                Payload payload = api.getInfoBuyCard(card.getId(), TOKEN);

                StringBuilder sb = new StringBuilder();
                sb.append((payload.getPhone_number())==null ? ";": (payload.getPhone_number()+ ";")) ;
                sb.append((payload.getBarcode()) ==null ? ";" : (payload.getBarcode()+";"));
                sb.append((payload.getSeller_descr()) ==null ? ";" : (payload.getSeller_descr()+ ";"));
                sb.append(payload.isIsActivated()+";");
                sb.append(payload.getDate_created()+ ";");
                sb.append((payload.getBuyer_profile()) ==null ? ";" : (payload.getBuyer_profile().getName()+ ";"));

                System.out.println(sb.toString());

                String outputFile ="D:\\\\"+filename+".csv";


                boolean alreadyExists = new File(outputFile).exists();

                try {
                    // use FileWriter constructor that specifies open for appending
                    CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');

                    // if the file didn't already exist then we need to write out the header line
                    if (!alreadyExists)
                    {
                        csvOutput.write("phone_number;barcode;seller_descr;is_activated;date_created;buyer_profile_name");
                        csvOutput.endRecord();
                    }

                    csvOutput.write(sb.toString());
                    csvOutput.endRecord();

                    csvOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

    }

    private static Properties loadProperties(){
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return prop;
    }




}
