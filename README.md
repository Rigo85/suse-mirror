# suse-mirror
Application to download/update the OpenSUSE repositories for offline use.

After generating the file suse-mirror.jar and update the list of 
repositories in the file config.json, run the following:

  java -Xms512m -Xmx2g  -jar suse-mirror.jar config.json

The application depends on JVM 8.

