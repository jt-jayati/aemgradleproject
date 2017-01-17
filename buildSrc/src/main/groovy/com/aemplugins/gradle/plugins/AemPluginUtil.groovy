package com.aemplugins.gradle.plugins;

import org.gradle.api.Project

/**
 * Created by jayati on 11/30/2016.
 */
public class AemPluginUtil {

    static void sendCurlRequest(Project project, String path,String bundleSymbolicName, String action){
        ServerConfig.setServerConfig(project)
        final curlRequestStr = "curl -u ${ServerConfig.username}:${ServerConfig.password} -F action=${action} http://${ServerConfig.server}:${ServerConfig.port}${path} ${bundleSymbolicName}"
        curlRequestStr.execute()
    }

    static void installPackageViaCurl(Project project, String packagePath){
        ServerConfig.setServerConfig(project)
        final installRequestStr = "curl -u ${ServerConfig.username}:${ServerConfig.password} -X POST -F file=@${packagePath} http://${ServerConfig.server}:${ServerConfig.port}/crx/packmgr/service.jsp -F install=true"
        installRequestStr.execute()
    }

}
