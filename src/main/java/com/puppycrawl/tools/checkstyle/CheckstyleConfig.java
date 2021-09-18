package com.puppycrawl.tools.checkstyle;


import com.puppycrawl.tools.checkstyle.utils.FileUtils;
import com.puppycrawl.tools.checkstyle.utils.PrintUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ming
 */
public abstract class CheckstyleConfig {


    /**
     * init
     */
    protected String checksFile() {
        return PathPar.CHECKS_FILENAME;
    }

    /**
     * init
     */
    @PostConstruct
    protected abstract void initCheckSetting();

    /**
     * 获取项目路径
     *
     * @return path
     */
    protected abstract String projectPath();

    /**
     * init 子类可直接调用
     */
    protected void initSetting() {
        String projectPath = projectPath();
        if (strIsNull(projectPath)) {
            PrintUtils.err("projectPath err: " + projectPath);
        }
        PrintUtils.info("projectPath: " + projectPath);
        //setGitHooks
        PrintUtils.info("set git hooks");
        try {
            setGitHooks(projectPath);
        } catch (IOException e) {
            PrintUtils.err("set git hooks");
            e.printStackTrace();
        }
        PrintUtils.info("set git config");
        try {
            setGitConfig(projectPath);
        } catch (IOException e) {
            PrintUtils.err("set git config");
            e.printStackTrace();
        }
    }

    private void setGitHooks(String projectPath) throws IOException {
        List<String> preCommit = FileUtils.getRpeCommit();
        FileUtils.write(new File(projectPath, PathPar.GIT_HOOKS_PATH + File.separator + PathPar.GIT_HOOKS_PRE_COMMIT), preCommit);
    }


    private void setGitConfig(String projectPath) throws IOException {
        String checksFile = checksFile();
        if (PathPar.CHECKS_FILENAME.equals(checksFile)) {
            PrintUtils.info("checks: default " + PathPar.CHECKS_FILENAME);
        } else {
            if (checksFile.contains(projectPath)) {
                checksFile = checksFile.replace(projectPath, "");
                checksFile = checksFile.replaceAll("\\\\", "/");
                PrintUtils.info("checks: CUSTOM " + checksFile);
            } else {
                PrintUtils.err("checks: CUSTOM " + checksFile);
                throw new IOException();
            }
        }
        ProtectionDomain protectionDomain = CheckstyleConfig.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL location = codeSource.getLocation();
        String file = location.getFile();
        file = URLDecoder.decode(file, "UTF-8");
        String jarPath = new File(file).getAbsolutePath().replace(projectPath, "");
        jarPath = jarPath.replaceAll("\\\\", "/");
        jarPath = "." + jarPath;
        String gitConfigJarFile = PathPar.GIT_CONFIG_CHECKSTYLE_JAR_PRE + jarPath;
        String gitConfigChecksFile = PathPar.GIT_CONFIG_CHECKSTYLE_CHECKS_PRE + checksFile;

        File gitLocalConfig = new File(projectPath, PathPar.GIT_CONFIG_LOCAL_PATH);
        if (gitLocalConfig.exists()) {
            checkConfigVal(gitLocalConfig, gitConfigChecksFile, gitConfigJarFile);
        } else {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(gitLocalConfig));
            bufferedWriter.write(PathPar.GIT_CONFIG_CHECKSTYLE_TITLE);
            bufferedWriter.newLine();
            bufferedWriter.write(gitConfigJarFile);
            bufferedWriter.newLine();
            bufferedWriter.write(gitConfigChecksFile);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }


    /**
     * 检查 git config 参数
     */
    private void checkConfigVal(File gitLocalConfig, String gitConfigChecksFile, String gitConfigJarFile) {
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
                if (s.contains(PathPar.GIT_CONFIG_CHECKSTYLE_TITLE)) {
                    titleIndex = i;
                } else if (s.contains(PathPar.GIT_CONFIG_CHECKSTYLE_CHECKS_PRE)) {
                    checksFileIndex = i;
                    equalsCheckFile = (s.equals(gitConfigChecksFile));
                } else if (s.contains(PathPar.GIT_CONFIG_CHECKSTYLE_JAR_PRE)) {
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
                collect.add(PathPar.GIT_CONFIG_CHECKSTYLE_TITLE);
                collect.add(gitConfigChecksFile);
                collect.add(gitConfigJarFile);
            }
            if (collect.size() > size) {
                FileUtils.write(gitLocalConfig, collect);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean strIsNull(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return "".equals(str);
    }


}
