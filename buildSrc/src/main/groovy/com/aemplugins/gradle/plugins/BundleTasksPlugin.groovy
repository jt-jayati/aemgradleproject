package com.aemplugins.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by jayati on 11/29/2016.
 */
class BundleTasksPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        final path = AemPluginConstants.BUNDLE_CONSOLE_PATH

        project.tasks.create('stopBundleTask').with {
            group = 'AEM Plugins'
            description = 'Task to stop the project bundle in AEM Felix Console'
            doLast{
                CurlTasksUtil.sendBundleRequest(project, path,'stop')
            }
        }

        project.tasks.create('uninstallBundleTask').with {
            group = "AEM Plugins"
            description = "Task to uninstall the project bundle from AEM Felix Console"
            dependsOn : "stopBundleTask"
            doLast{
                CurlTasksUtil.sendBundleRequest(project, path,'uninstall')
            }
        }

        project.tasks.create('installBundleTask').with {
            group = "AEM Plugins"
            description = "Task to install the project bundle to AEM Felix Console"
            doLast{
                CurlTasksUtil.installBundleRequest(project, path)
            }
        }

        project.tasks.create('startBundleTask').with {
            group = "AEM Plugins"
            description = "Task to start the project bundle in AEM Felix Console"
            doLast{
                CurlTasksUtil.sendBundleRequest(project, path,'start')
            }
        }

        project.tasks.create('refreshBundlesTask').with {
            group = "AEM Plugins"
            description = "Task to refresh all the bundles in AEM Felix Console"
            doLast{
                CurlTasksUtil.sendBundleRequest(project, path,'refresh')
            }
        }

        project.tasks.create('checkBundlesTask').with {
            group = "AEM Plugins"
            description = "Task to check the bundle status in AEM Felix Console"
            doLast{
                CurlTasksUtil.checkActiveBundles(project, path)
            }
        }


    }
}
