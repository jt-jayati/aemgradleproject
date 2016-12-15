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

        Task uploadPackageTask = project.task([group: "CQ Plugins",dependsOn: "createPackage"],'uploadPackageTask'){
            AemPluginUtil.installPackageViaCurl(project.tasks.findByName("createPackage").archivePath.toString())
        }
    }
}
