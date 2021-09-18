package com.puppycrawl.tools.checkstyle.utils;


import com.puppycrawl.tools.checkstyle.PathPar;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ming
 */
public class FileUtils {

    public static void write(File gitLocalConfig, List<String> collect) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(gitLocalConfig));
            bufferedWriter.write(collect.get(0));
            bufferedWriter.flush();
            bufferedWriter.close();
            bufferedWriter = new BufferedWriter(new FileWriter(gitLocalConfig, true));
            for (int i = 1; i < collect.size(); i++) {
                String wStr = collect.get(i);
                bufferedWriter.newLine();
                bufferedWriter.append(wStr);
                bufferedWriter.flush();
            }
        } finally {
            if (bufferedWriter != null) {
                close(bufferedWriter);
            }
        }
    }

    public static void copy(File in, File out) throws IOException {
        notNull(in, "No InputStream specified");
        notNull(out, "No OutputStream specified");
        InputStream inputStream = Files.newInputStream(in.toPath());
        OutputStream outputStream = Files.newOutputStream(out.toPath());
        int bufferSize = 4096;
        try {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } finally {
            close(inputStream);
            close(outputStream);
        }
    }


    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ex) {
            // ignore
        }
    }

    /**
     * 获取配置文件
     *
     * @return file
     */
    public static List<String> getRpeCommit() throws IOException {
        ClassLoader classLoader = PathPar.class.getProtectionDomain().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(PathPar.GIT_HOOKS_PRE_COMMIT);
        assert resourceAsStream != null;
        BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream));
        List<String> stringList = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            stringList.add(line);
        }
        return stringList;
    }

}
