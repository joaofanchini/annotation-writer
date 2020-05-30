package br.com.simple.logger;

import br.com.simple.logger.annotations.LogContent;
import br.com.simple.logger.baseWriter.BaseLogAbstract;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.StringJoiner;

public class LogWriter implements Closeable {
    private FileWriter fileWriter;

    public LogWriter(String path) throws IOException {
        File file = new File(path);
        if (file.isDirectory() && !file.isFile()) {
            boolean newFile = file.createNewFile();
            if (!newFile)
                throw new IOException("Could not create the new file");
        }
        this.fileWriter = new FileWriter(file, true);
    }

    public <T extends BaseLogAbstract> void write(T... objectsToWrite) {
        Arrays.stream(objectsToWrite).forEach(objectToWrite -> {
            String annotationContent = getAnnotationContent(objectToWrite);
            if (!annotationContent.isEmpty()) {
                try {
                    this.fileWriter.write(annotationContent);
                    this.fileWriter.flush();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Written file!");
            }
        });
    }

    private <T extends BaseLogAbstract> String getAnnotationContent(T objectToWrite) {
        StringJoiner joiner = new StringJoiner(" | ");

        Field[] declaredFields = objectToWrite.getClass().getDeclaredFields();

        Arrays.stream(declaredFields)
                .forEach(declaredField -> {
                    boolean isAnnotationPresent = declaredField.isAnnotationPresent(LogContent.class);
                    if (isAnnotationPresent) {
                        try {
                            StringBuilder stringBuilder = new StringBuilder();

                            declaredField.setAccessible(true);
                            Object value = declaredField.get(objectToWrite);
                            if (value != null)
                                stringBuilder.append(declaredField.getName().concat(" : ").concat(value.toString()));

                            joiner.add(stringBuilder.toString());

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });

        return LocalDate.now().toString().concat(" - ").concat(" [ ".concat(objectToWrite.getClass().getName()).concat(" ] ").concat(joiner.toString().concat(System.lineSeparator())));
    }

    @Override
    public void close() throws IOException {
        fileWriter.close();
    }
}
