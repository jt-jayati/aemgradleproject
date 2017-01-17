AEM Project using Gradle
========

This a content package project generated using the multimodule-content-package-archetype.

Building
--------

This project uses Gradle for building. Common commands:

./gradlew  build 
./gradlew uploadPackageTask



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
