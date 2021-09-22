/**
 * git 提交风格验证扩展包
 */
package com.puppycrawl.tools.checkstyle.plugin.git;


import java.io.File;

class PackageConst {
    /**
     * gut hoos 目录 相对路径
     */
    protected static final String GIT_HOOKS_PATH = ".git" + File.separator + "hooks";
    /**
     * pre-commit 固定文件名
     */
    public static final String GIT_HOOKS_PRE_COMMIT = "pre-commit";
    /**
     * git config local 文件路径 相对路径
     */
    protected static final String GIT_CONFIG_LOCAL_PATH = ".git" + File.separator + "config";


    /**
     * git config 配置信息头
     */
    protected static final String GIT_CONFIG_CHECKSTYLE_TITLE = "[checkstyle]";
    /**
     * git config jar 路径配置信息
     */
    protected static final String GIT_CONFIG_CHECKSTYLE_JAR_PRE = "    checksJar = ";

    /**
     * git config checkstyle.xml 路径配置信息
     */
    protected static final String GIT_CONFIG_CHECKSTYLE_CHECKS_PRE = "    checksFile = ";

    /**
     * git config checkstyle.xml 默认配置文件
     */
    protected static final String CHECKS_FILENAME = "sun_checks.xml";

}