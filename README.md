AEM Project using Gradle
========

This a content package project generated using the multimodule-content-package-archetype.

Building
--------

This project uses Gradle for building. Common commands:

<b>./gradlew build </b> to build the project bundle and content zip<br> 
<b>./gradlew uploadPackageTask</b> to upload and install AEM package zip on AEM server

AEM Plugins tasks
-----------------
<i>checkBundlesTask </i>- Task to check the bundle status in AEM Felix Console<br>
<i>createPackage </i>- Creates AEM package with content and bundle<br>
<i>installBundleTask </i>- Task to install the project bundle to AEM Felix Console<br>
<i>packageSCRAnnotations </i>- Injects SCR metafiles into package's OSGI-INF<br>
<i>processSCRAnnotations </i>- Processes SCR annoations from source<br>
<i>refreshBundlesTask </i>- Task to refresh all the bundles in AEM Felix Console<br>
<i>startBundleTask </i>- Task to start the project bundle in AEM Felix Console<br>
<i>stopBundleTask </i>- Task to stop the project bundle in AEM Felix Console<br>
<i>uninstallBundleTask </i>- Task to uninstall the project bundle from AEM Felix Console<br>
<i>uploadPackageTask </i>- Task to upload and install the AEM package server<br>


Developing with this Repo
--------

1. Clone this repository
2. Provide a suitable name for project in settings.gradle
3. Use gradle.properties in root project to provide AEM server configuration
4. Start with your project under bundle and content directory
5. In case other modules are needed add them under setting.xml and provide each module it's own 



Migrating existing aem project to gradle from Maven
--------

1. Clone this repository
2. Within existing project root dir run "gradle init"
3. Copy module directories into aemgradleproject
4. Use gradle.properties in root project to provide AEM server configuration
5. update dependiencies version in bundle and content build.gradle provided in this project from the generated gradle scripts for modules
6. Build fom root dir using above commands
