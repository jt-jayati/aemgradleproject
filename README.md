My AEM Project in Gradle
========

This a content package project generated using the multimodule-content-package-archetype.

Building
--------

This project uses Gradle for building. Common commands:

./gradlew build
./gradlew uploadPackageTask


Using with VLT
--------------

To use vlt with this project, first build and install the package to your local CQ instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal ``vlt up`` and ``vlt ci`` commands.


Specifying CRX Host/Port -TO DO
------------------------

The CRX host and port can be specified on the command line with:


TO DO:

creating template projects and using template/archetype plugins available
using custom init.gradle in init.d 		https://docs.gradle.org/current/userguide/init_scripts.html

 - to check curl responses in groovy
 - try above 2 for providing functionality like archetype
 - create plugins neatly using  https://docs.gradle.org/current/userguide/javaGradle_plugin.html
 