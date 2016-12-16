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
        final path = AemPluginConstants.getBUNDLE_CONSOLE_PATH()

        Task updateBundleTask = project.task([group: "CQ Plugins", dependsOn: "uninstallBundleTask"],'uploadBundleTask'){
            AemPluginUtil.sendCurlRequest(path,project.jar.manifest.symbolicName,"update",);
        }
        Task startBundleTask = project.task([group: "CQ Plugins", dependsOn: "installBundleTask"],'startBundleTask'){
            AemPluginUtil.sendCurlRequest(path,project.jar.manifest.symbolicName,'start')
        }
        Task stopBundleTask = project.task([group: "CQ Plugins"],'stopBundleTask'){
            AemPluginUtil.sendCurlRequest(path,project.jar.manifest.symbolicName,'stop')
        }
        Task refreshBundlesTask = project.task([group: "CQ Plugins", dependsOn: "startBundleTask"],'refreshBundlesTask'){
            AemPluginUtil.sendCurlRequest(path,project.jar.manifest.symbolicName,'refresh')
        }
        Task installBundleTask = project.task([group: "CQ Plugins", dependsOn: "uploadBundleTask"],'installBundleTask'){
            AemPluginUtil.sendCurlRequest(path,project.jar.manifest.symbolicName,'install -F bundlestart=start -F bundlestartlevel=20 -F bundlefile=/path/to/jar/file.jar ')
        }
        Task uninstallBundleTask = project.task([group: "CQ Plugins", dependsOn: "stopBundleTask"],'uninstallBundleTask'){
            AemPluginUtil.sendCurlRequest(path,project.jar.manifest.symbolicName,'uninstall')
        }

        Task checkIfBundleActive = project.task([group: "CQ Plugins", dependsOn: "startBundleTask"],'checkIfBundleActive'){
            def curlStr = 'curl -u admin:admin http://localhost:4502${path}${project.jar.manifest.symbolicName}.json'
            curlStr.execute()
            //process.waitFor()
            //println process.err.text
        }

        Task verifyBundles = project.task([group: "CQ Plugins"],'verifyBundles'){
            def curlStr = 'curl -u admin:admin http://localhost:4502/system/console/bundles.json'
            curlStr.execute()
           // process.waitFor()
            //println process.err.text

        }

    }
}
