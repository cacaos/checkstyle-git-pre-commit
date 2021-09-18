package com.puppycrawl.tools.checkstyle;

import java.io.File;

/**
 * @author Ming
 */
public class PathPar {

    /**
     * git hooks 文件路径
     */
    protected static final String GIT_HOOKS_PATH = ".git" + File.separator + "hooks";

    /**
     * pre_commit 名称
     */
    public static final String GIT_HOOKS_PRE_COMMIT = "pre-commit";


    /**
     * git config 配置文件
     */
    protected static final String GIT_CONFIG_LOCAL_PATH = ".git" + File.separator + "config";
    protected static final String GIT_CONFIG_CHECKSTYLE_TITLE = "[checkstyle]";
    protected static final String GIT_CONFIG_CHECKSTYLE_JAR_PRE = "    checksJar = ";
    protected static final String GIT_CONFIG_CHECKSTYLE_CHECKS_PRE = "    checksFile = ";


    /**
     * 默认配置
     */
    protected static final String CHECKS_FILENAME = "sun_checks.xml";


}
