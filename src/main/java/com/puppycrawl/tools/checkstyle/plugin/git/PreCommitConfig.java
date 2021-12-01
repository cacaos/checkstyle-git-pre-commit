package com.puppycrawl.tools.checkstyle.plugin.git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

/**
 * @author Ming
 */
public abstract class PreCommitConfig {

    /**
     * 用户继承该类,实现该方法调用初始化设置.
     * <pre>
     *      public void initCheckSetting(){
     *          supper.initSetting();
     *      }
     * </pre>
     */
    @PostConstruct
    protected abstract void initCheckSetting();


    /**
     * 获取项目根目录.
     *
     * @return path 项目根目录绝对路径
     */
    protected abstract String projectPath();

    /**
     * 配置checkstyle.xml 文件.
     *
     * @return 文件路径
     */
    protected String checksFile() {
        return PackageConst.CHECKS_FILENAME;
    }

    /**
     * init 子类可直接调用.
     */
    protected void initSetting() {
        String projectPath = this.projectPath();
        if (this.strIsnull(projectPath)) {
            PrintUtils.err("projectPath err: " + projectPath);
        }

        PrintUtils.info("projectPath: " + projectPath);
        PrintUtils.info("set git hooks");

        try {
            this.setGitHooks(projectPath);
        } catch (IOException var4) {
            PrintUtils.err("set git hooks");
            var4.printStackTrace();
        }

        PrintUtils.info("set git config");

        try {
            this.setGitConfig(projectPath);
        } catch (IOException var3) {
            PrintUtils.err("set git config");
            var3.printStackTrace();
        }

    }

    /**
     * 设置 pre-commit
     *
     * @param projectPath 项目目录
     * @throws IOException 写入失败异常
     */
    private void setGitHooks(String projectPath) throws IOException {
        List<String> preCommit = FileUtils.getRpeCommit();
        String child = PackageConst.GIT_HOOKS_PATH + File.separator + PackageConst.GIT_HOOKS_PRE_COMMIT;
        FileUtils.write(new File(projectPath, child), preCommit);
    }

    /**
     * 设置git config
     *
     * @param projectPath 项目目录
     * @throws IOException 写入失败异常
     */
    private void setGitConfig(String projectPath) throws IOException {
        String checksFile = this.checksFile();
        if (PackageConst.CHECKS_FILENAME.equals(checksFile)) {
            PrintUtils.info("checks: default sun_checks.xml");
        } else {
            if (!checksFile.contains(projectPath)) {
                PrintUtils.err("checks: CUSTOM " + checksFile);
                throw new IOException();
            }

            checksFile = checksFile.replace(projectPath, "");
            checksFile = checksFile.replaceAll("\\\\", "/");
            checksFile = "." + checksFile;
            PrintUtils.info("checks: CUSTOM " + checksFile);
        }

        ProtectionDomain protectionDomain = PreCommitConfig.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL location = codeSource.getLocation();
        String file = location.getFile();
        file = URLDecoder.decode(file, "UTF-8");
        String jarPath = (new File(file)).getAbsolutePath().replace(projectPath, "");
        jarPath = jarPath.replaceAll("\\\\", "/");
        jarPath = "." + jarPath;
        String gitConfigJarFile = PackageConst.GIT_CONFIG_CHECKSTYLE_JAR_PRE + jarPath;
        String gitConfigChecksFile = PackageConst.GIT_CONFIG_CHECKSTYLE_CHECKS_PRE + checksFile;
        File gitLocalConfig = new File(projectPath, PackageConst.GIT_CONFIG_LOCAL_PATH);
        if (gitLocalConfig.exists()) {
            this.checkConfigVal(gitLocalConfig, gitConfigChecksFile, gitConfigJarFile);
        } else {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(gitLocalConfig));
            bufferedWriter.write(PackageConst.GIT_CONFIG_CHECKSTYLE_TITLE);
            bufferedWriter.newLine();
            bufferedWriter.write(gitConfigJarFile);
            bufferedWriter.newLine();
            bufferedWriter.write(gitConfigChecksFile);
            bufferedWriter.flush();
            bufferedWriter.close();
        }

    }

    /**
     * 检查git config 文件配置
     *
     * @param gitLocalConfig      gitLocalConfigFile
     * @param gitConfigChecksFile 标准xml配置内容
     * @param gitConfigJarFile    标准jar配置内容
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

            for (int i = 0; i < size; ++i) {
                String s = collect.get(i);
                if (s.contains(PackageConst.GIT_CONFIG_CHECKSTYLE_TITLE)) {
                    titleIndex = i;
                } else if (s.contains(PackageConst.GIT_CONFIG_CHECKSTYLE_CHECKS_PRE)) {
                    checksFileIndex = i;
                    equalsCheckFile = s.equals(gitConfigChecksFile);
                } else if (s.contains(PackageConst.GIT_CONFIG_CHECKSTYLE_JAR_PRE)) {
                    jarFileIndex = i;
                    equalsJarFile = s.equals(gitConfigJarFile);
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
                } else if (!equalsCheckFile) {
                    ++checksFileIndex;
                    if (checksFileIndex >= size) {
                        collect.add(gitConfigChecksFile);
                    } else {
                        collect.add(checksFileIndex, gitConfigChecksFile);
                    }
                }

                if (jarFileIndex == -1) {
                    jarFileIndex = titleIndex + 1;
                    if (jarFileIndex >= size) {
                        collect.add(gitConfigJarFile);
                    } else {
                        collect.add(checksFileIndex, gitConfigJarFile);
                    }
                } else if (!equalsJarFile) {
                    ++jarFileIndex;
                    if (jarFileIndex >= size) {
                        collect.add(gitConfigJarFile);
                    } else {
                        collect.add(jarFileIndex, gitConfigJarFile);
                    }
                }
            } else {
                collect.add(PackageConst.GIT_CONFIG_CHECKSTYLE_TITLE);
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

    /**
     * 判断字符串为空
     *
     * @param str str
     * @return isnull? true : false
     */
    private boolean strIsnull(String str) {
        if (str == null) {
            return true;
        } else {
            str = str.trim();
            return "".equals(str);
        }
    }
}
