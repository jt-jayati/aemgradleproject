package com.aemplugins.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task


class ProcessSCR implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.ant.properties.classes = project.sourceSets.main.output.classesDir

        Task processSCRTask = project.task([description: "Processes SCR annoations from source", group: "CQ Plugins", dependsOn: "compileGroovy"], 'processSCRAnnotations')
        processSCRTask.doLast{
                if (!project.ant.properties.classes) {
                    println "No Java/Groovy classes found"
                    return
                }
                project.ant.taskdef(resource: "scrtask.properties", classpath : project.configurations.compile.asPath)
                project.ant.scr(srcdir: project.ant.properties.classes, destdir: project.ant.properties.classes, classpath: project.configurations.compile.asPath, scanClasses: true)
            }

        Task packageSCRTask = project.task([description: "Injects SCR metafiles into package's OSGI-INF", group: "CQ Plugins", dependsOn: "processSCRAnnotations"], 'packageSCRAnnotations') {
            def tree = project.fileTree(dir: new File(project.ant.properties.classes, '/OSGI-INF'), include: "**/*.xml", exclude: "**/metatype/**")
            def serviceComponents = ""
            if (tree.isEmpty()) {
                println "No SCR Annotations found"
                project.extensions.add("serviceComponents", serviceComponents)
                return
            }
            tree.each { File file ->
                def index = file.path.indexOf("OSGI-INF")
                serviceComponents += file.path.substring(index).replace("\\", "/") + ", "
            }
            serviceComponents = serviceComponents.substring(0, serviceComponents.length() - 2)//remove final ", "
            //println serviceComponents
            project.tasks.getByName('jar').manifest.instruction('Service-Component', serviceComponents)
            //project.extensions.add("serviceComponents", serviceComponents)
        }
        final jarTask = project.tasks.findByPath("jar").dependsOn packageSCRTask
    }
}
