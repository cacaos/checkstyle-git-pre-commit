package com.puppycrawl.tools.checkstyle.plugin.git;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具.
 *
 * @author Ming
 */
public class FileUtils {

    /**
     * 写入git config 配置文件.
     *
     * @param gitLocalConfig git config file
     * @param collect        写入的内容,每个元素都将换行
     * @throws IOException 写入失败抛出
     */
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

    /**
     * 文件copy.
     *
     * @param in  in file
     * @param out out file
     * @throws IOException 复制错误时抛出
     */
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


    /**
     * 检验参数,如果为空 抛出异常.
     *
     * @param object  参数对象
     * @param message 异常信息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 关闭流.
     *
     * @param closeable 流对象
     */
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
     * 获取配置文件.
     * 返回读取的内容,元素换行
     *
     * @return file
     */
    public static List<String> getRpeCommit() throws IOException {
        ClassLoader classLoader = FileUtils.class.getProtectionDomain().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(PackageConst.GIT_HOOKS_PRE_COMMIT);
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
