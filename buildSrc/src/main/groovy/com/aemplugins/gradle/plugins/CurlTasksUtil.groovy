package com.aemplugins.gradle.plugins;

import org.gradle.api.Project
import org.json.JSONObject

/**
 * Created by jayati on 11/30/2016.
 */
public class CurlTasksUtil {

    static void sendBundleRequest(Project project, String path, String action){
        ServerConfig.setServerConfig(project)
        final curlRequestStr = "curl -u ${ServerConfig.username}:${ServerConfig.password} http://${ServerConfig.server}:${ServerConfig.port}${path}/${project.jar.manifest.symbolicName} -Faction=${action}"
        processRequest(curlRequestStr)
    }

    static void installPackage(Project project){
        String packagePath = project.tasks.findByName("createPackage").archivePath.toString()
        ServerConfig.setServerConfig(project)
        final installRequestStr = "curl -u ${ServerConfig.username}:${ServerConfig.password} -X POST -F file=@${packagePath} http://${ServerConfig.server}:${ServerConfig.port}${AemPluginConstants.PACKAGE_MANAGER_PATH}.jsp -F install=true"
        processRequest(installRequestStr);
    }

    static void checkActiveBundles(project,path){
        ServerConfig.setServerConfig(project)
        def curlStr = "curl -u ${ServerConfig.username}:${ServerConfig.password} http://${ServerConfig.server}:${ServerConfig.port}${path}/${project.jar.manifest.symbolicName}.json"
        processRequest(curlStr)

    }

    static void installBundleRequest(project, path){
        ServerConfig.setServerConfig(project)
        def curlStr = "curl -u ${ServerConfig.username}:${ServerConfig.password} -F action=install -F bundlestart=start -F bundlestartlevel=20 -F bundlefile=@${project.libsDir}\\${project.tasks.jar.archiveName} http://${ServerConfig.server}:${ServerConfig.port}${path}"
        processRequest(curlStr)
    }

    static void processRequest(final String curlRequestStr){

        Process process = curlRequestStr.execute()
        def response= process.text
        if(response.startsWith("{")){
            JSONObject jsonResponse = new JSONObject(response)
            println jsonResponse.has("status") ? jsonResponse.status:""
        }
       else if(response.startsWith("<")){
            def xmlResponse = new XmlSlurper().parseText(response)
            println xmlResponse.body
        }
    }
}
