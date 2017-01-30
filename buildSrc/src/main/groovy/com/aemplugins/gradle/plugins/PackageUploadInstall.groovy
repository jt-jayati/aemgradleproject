package com.aemplugins.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by jayati on 11/30/2016.
 */
class PackageUploadInstall implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.tasks.create('uploadPackageTask').with {
            group = "AEM Plugins"
            description = "Task to upload and install the AEM package server"
            doLast{
                CurlTasksUtil.installPackage(project)
            }
        }

    }
}
