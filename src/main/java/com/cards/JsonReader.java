package com.cards;

//import com.api.CSVCreater;
import com.api.CrumziApi;
import com.api.CrumziApiImpl;
import com.api.SendEmail;
import com.csvreader.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;

public class JsonReader {
   private static Logger logger = LoggerFactory.getLogger(JsonReader.class);

    private static Properties props;

    public static void main(String[] args)// throws URISyntaxException
    {



        LocalDateTime dateTime = LocalDateTime.now();
        Long beggining = dateTime.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        final String currentDateTime = Long.toString(System.currentTimeMillis());
        // beggining = 1488527251245L;
        CodeSource codeSource = JsonReader.class.getProtectionDomain().getCodeSource();
        File jarFile = null;
        try {
            jarFile = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
                logger.error("!!!"+e.toString());
            e.printStackTrace();
        }
        String jarDir = jarFile.getParentFile().getPath();
        String file = jarDir + "/" + currentDateTime + ".csv";

        props = loadProperties(args[0], jarDir);
        JsonReader J = new JsonReader();

        logger.info(file);
       // System.out.println(file);

        try {
            J.process(props.getProperty("TOKEN"), beggining,file, currentDateTime);
        } catch (IOException e) {
            logger.error("!!!"+e.toString());
            e.printStackTrace();
        }
        SendEmail s = new SendEmail();
        s.send(props.getProperty("smtp_user"), props.getProperty("smtp_password"),
                props.getProperty("smtp_host"), props.getProperty("smtp_port"),
                props.getProperty("emailto"),file , currentDateTime + ".csv");


    }


    private void process(String TOKEN, long date_from,String file, String filename) throws IOException {


        CrumziApi api = new CrumziApiImpl();
        List<com.clients.List> cards = api.getBuyerCards(TOKEN,date_from);

        if (cards.isEmpty()) {
            File csvFile = new File(file);
            FileWriter writer = new FileWriter(csvFile);
            writer.write("Cards not found");
            writer.close();
        }
            for (com.clients.List card : cards) {
                Payload payload = api.getInfoBuyCard(card.getId(), TOKEN);

                StringBuilder sb = new StringBuilder();
                sb.append((payload.getPhone_number())==null ? ";": (payload.getPhone_number()+ ";")) ;
                sb.append((payload.getBarcode()) ==null ? ";" : (payload.getBarcode()+";"));
                sb.append((payload.getSeller_descr()) ==null ? ";" : (payload.getSeller_descr()+ ";"));
                sb.append(payload.isIsActivated()+";");
                sb.append(payload.getDate_created()+ ";");
                sb.append((payload.getBuyer_profile()) ==null ? ";" : (payload.getBuyer_profile().getName()+ ";"));

  //              System.out.println(sb.toString());


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
                    logger.error("!!!"+e);
                   //e.printStackTrace();
                }

            }

    }

    private static Properties loadProperties(String orgName, String jarDir){
       // Logger logger = LoggerFactory.getLogger(Properties.class);

        Properties prop = new Properties();
        InputStream input = null;
       // logger.info("load a properties file");
        try {
            input = new FileInputStream(jarDir + "/" + orgName + ".properties");

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
                logger.error("!!!"+ex.toString());
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("!!!"+e.toString());
                    e.printStackTrace();
                }
            }
        }

        return prop;
    }




}
