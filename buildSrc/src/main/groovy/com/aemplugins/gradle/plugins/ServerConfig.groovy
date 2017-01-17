package com.aemplugins.gradle.plugins

import org.gradle.api.Project

/**
 * Created by jayati on 11/30/2016.
 */
class ServerConfig {

    private static String server = "localhost"
    private static String port = "4502"
    private static String username = "admin"
    private static String password = "admin"

    static void setServerConfig(Project project) {
        server = project.properties.containsKey('server')?project.properties.get('server'):server
        port = project.properties.containsKey('port')? project.properties.get('port'):port
        username = project.properties.containsKey('username')?project.properties.get('username'):username
        password = project.properties.containsKey('password')?project.properties.get('password'):password
    }

    static String getServer() {
        server
    }

    static String getPort() {
        port
    }

    static String getUsername() {
        username
    }

    static String getPassword() {
        password
    }

}
