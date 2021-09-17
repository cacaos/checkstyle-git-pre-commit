package cn.cacaos.ojer;


import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Ming
 */
public abstract class CheckstyleConfig {

    private static final String CHECKS_FILENAME = "sun_checks.xml";
    private static final String PRE_COMMIT_FILE = "pre-commit";

    /**
     * init
     */
    protected String checksFile() {
        return CHECKS_FILENAME;
    }

    /**
     * init
     */
    @PostConstruct
    protected abstract void initCheckSetting();

    /**
     * init 子类可直接调用
     */
    protected void initSetting() {
        String projectPath = projectPath();
        if (strIsNull(projectPath)) {
            return;
        }
        String jarWholePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            jarWholePath = java.net.URLDecoder.decode(jarWholePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (strIsNull(jarWholePath)) {
            return;
        }

        System.err.println("jarWholePath: " + jarWholePath);
        String thisParent = new File(jarWholePath).getParentFile().getAbsolutePath();
        thisParent = thisParent.replaceAll("\\\\", "\\\\\\\\");
        String end = "\\";
        while (projectPath.endsWith(end)) {
            projectPath = projectPath.substring(0, projectPath.length() - 1);
        }
        projectPath = projectPath.replaceAll("\\\\", "\\\\\\\\");


        System.err.println("thisParent: " + thisParent);
        System.err.println("projectPath:" + projectPath);
        String mainHooksPath = "pre-commit";
        String gitConfigPath = ".git/config";
        String gitHooksPath = ".git/hooks/pre-commit";

        String gitConfigTitle = "[checkstyle]";
        String checksFile = checksFile();
        if (!CHECKS_FILENAME.equals(checksFile)) {
            // String gitConfigChecksFile = "    checksFile = " + "\"\\\"" + projectPath + "\\\\" + CHECKS_FILENAME + "\\\"\"";
            checksFile = checksFile.replaceAll("\\\\", "\\\\\\\\");
            checksFile = "\"\\\"" + checksFile + "\\\"\"";
        }

        String jarFilePre = "    checksJar = ";
        String checksFilePre = "    checksFile = ";

        String jarPath = new File(jarWholePath).getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
        jarPath = "\"\\\"" + jarPath + "\\\"\"";

        String gitConfigJarFile = jarFilePre + jarPath;
        String gitConfigChecksFile = checksFilePre + checksFile;

        try {
            copy(getResourceFile("/" + PRE_COMMIT_FILE), new File(projectPath, gitHooksPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File gitLocalConfig = new File(projectPath, gitConfigPath);
        if (gitLocalConfig.exists()) {
            checkConfigVal(gitLocalConfig, gitConfigTitle, checksFilePre, gitConfigChecksFile, jarFilePre, gitConfigJarFile);
        } else {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(gitLocalConfig));
                bufferedWriter.write(gitConfigTitle);
                bufferedWriter.newLine();
                bufferedWriter.write(gitConfigJarFile);
                bufferedWriter.newLine();
                bufferedWriter.write(gitConfigChecksFile);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取项目路径
     *
     * @return path
     */
    protected String projectPath() {
        String path = getResourceFile("/").getPath();
        String[] targets = path.split("target");
        return targets[0];
    }

    /**
     * 检查 git config 参数
     */
    private void checkConfigVal(
            File gitLocalConfig,
            String gitConfigTitle,
            String checksFilePrefix, String gitConfigChecksFile,
            String jarFilePre, String gitConfigJarFile
    ) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(gitLocalConfig));
            List<String> collect = bufferedReader.lines().collect(Collectors.toList());
            int titleIndex = -1;
            int checksFileIndex = -1;
            int jarFileIndex = -1;
            boolean equalsCheckFile = false;
            boolean equalsJarFile = false;

            int size = collect.size();
            for (int i = 0; i < size; i++) {
                String s = collect.get(i);
                if (s.contains(gitConfigTitle)) {
                    titleIndex = i;
                } else if (s.contains(checksFilePrefix)) {
                    checksFileIndex = i;
                    equalsCheckFile = (s.equals(gitConfigChecksFile));
                } else if (s.contains(jarFilePre)) {
                    jarFileIndex = i;
                    equalsJarFile = (s.equals(gitConfigJarFile));
                }
            }

            if (titleIndex > -1) {
                if (checksFileIndex == -1) {
                    checksFileIndex = titleIndex + 1;
                    if (checksFileIndex >= size) {
                        collect.add(gitConfigChecksFile);
                    } else {
                        collect.add(checksFileIndex, gitConfigChecksFile);
                    }
                } else {
                    if (!equalsCheckFile) {
                        checksFileIndex = checksFileIndex + 1;
                        if (checksFileIndex >= size) {
                            collect.add(gitConfigChecksFile);
                        } else {
                            collect.add(checksFileIndex, gitConfigChecksFile);
                        }
                    }
                }

                if (jarFileIndex == -1) {
                    jarFileIndex = titleIndex + 1;
                    if (jarFileIndex >= size) {
                        collect.add(gitConfigJarFile);
                    } else {
                        collect.add(checksFileIndex, gitConfigJarFile);
                    }
                } else {
                    if (!equalsJarFile) {
                        jarFileIndex = jarFileIndex + 1;
                        if (jarFileIndex >= size) {
                            collect.add(gitConfigJarFile);
                        } else {
                            collect.add(jarFileIndex, gitConfigJarFile);
                        }
                    }
                }
            } else {
                collect.add(gitConfigTitle);
                collect.add(gitConfigChecksFile);
                collect.add(gitConfigJarFile);
            }
            if (collect.size() > size) {
                write(gitLocalConfig, collect);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(File gitLocalConfig, List<String> collect) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                close(bufferedWriter);
            }
        }
    }

    private void copy(File in, File out) throws IOException {
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

    private static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean strIsNull(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return "".equals(str);
    }


    /**
     * 获取resourceFile
     *
     * @return path
     */
    protected File getResourceFile(String resourceName) {
        return new File(Objects.requireNonNull(CheckstyleConfig.class.getResource(resourceName)).getPath());
    }

}
