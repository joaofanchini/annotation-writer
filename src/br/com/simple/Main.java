package br.com.simple;

import br.com.simple.logger.LogWriter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String pathFileName = System.getProperty("path-file-name");
        LogWriter logWriter = new LogWriter(pathFileName);
        Example example = new Example();
        logWriter.write(example);
    }
}