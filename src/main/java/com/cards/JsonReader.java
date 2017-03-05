package com.cards;

//import com.api.CSVCreater;
import com.api.CrumziApi;
import com.api.CrumziApiImpl;
import com.api.SendEmail;
import com.csvreader.CsvWriter;
import java.io.*;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;

public class JsonReader {

    private static Properties props;

    public static void main(String[] args) throws IOException, URISyntaxException {

        props = loadProperties();
        JsonReader J = new JsonReader();

        LocalDateTime dateTime = LocalDateTime.now();
        Long beggining = dateTime.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        final String currentDateTime = Long.toString(System.currentTimeMillis());

        CodeSource codeSource = JsonReader.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();
        String file = jarDir + "/" + currentDateTime + ".csv";
        //FileWriter writer = new FileWriter(file);
       // writer.write(" ");
      //  writer.close();
        System.out.println(file);
        J.process(props.getProperty("TOKEN"), beggining,file, currentDateTime);
        SendEmail s = new SendEmail();
        s.send(props.getProperty("smtp_user"), props.getProperty("smtp_password"),
                props.getProperty("smtp_host"), props.getProperty("smtp_port"),
                props.getProperty("emailto"),file , currentDateTime + ".csv");


    }


    private void process(String TOKEN, long date_from,String file, String filename) throws IOException {
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

               // String outputFile ="D:\\\\"+filename+".csv";


                boolean alreadyExists = new File(file).exists();

                try {

                    // use FileWriter constructor that specifies open for appending
                    CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true), ',');

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
