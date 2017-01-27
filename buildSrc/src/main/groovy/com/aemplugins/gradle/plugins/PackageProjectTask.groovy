package com.aemplugins.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Zip

/**
 * Created by jayati on 11/30/2016.
 */
class PackageProjectTask implements Plugin<Project> {

    @Override
    void apply(Project project) {

        String defaultInstallPath = "jcr_root/apps/"+project.archivesBaseName-"-content"+"/install"
        def osgiModules = project.parent.childProjects.findAll {it.value.plugins.findPlugin('osgi')}

        Task createPackage =project.task([group: "AEM Plugins", type: Zip],"createPackage"){
            // copy task for jcr_root content
                from 'src/main/content'
            // copy task for bundles into install folder
                osgiModules.each { osgiModule ->
                  from( osgiModule.value.tasks.jar,{
                        into project.ext.has("installPath")? project.ext.get("installPath") : defaultInstallPath
                    })
                }
            }

        project.tasks.build.dependsOn += createPackage
    }
}
