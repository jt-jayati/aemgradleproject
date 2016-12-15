package com.aemplugins.gradle.plugins;

/**
 * Created by jayati on 11/30/2016.
 */
public class AemPluginUtil {

    static String server = "localhost"
    static String port = "4502"
    static String username = "admin"
    static String password = "admin"

    static void sendCurlRequest(String path,String bundleSymbolicName, String action){
        final curlRequestStr = "curl -u ${username}:${password} -F action=${action} http://${server}:${port}${path} ${bundleSymbolicName}"
        curlRequestStr.execute()
    }

    static void installPackageViaCurl(String packagePath){
        final installRequestStr = "curl -u ${username}:${password} -X POST -F file=@${packagePath} http://${server}:${port}/crx/packmgr/service.jsp -F install=true"
        installRequestStr.execute()
    }

}
