/*
package com.api;

import com.cards.JsonReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class CSVCreater {

    public String createFile(String filename) throws URISyntaxException, IOException {
        CodeSource codeSource = JsonReader.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();
        File csvFile = new File(jarDir + "/" + filename + ".csv");
        FileWriter writer = new FileWriter(csvFile);
        writer.write("Test data");
        writer.close();
        return csvFile.getAbsolutePath();
    }

}




*/
