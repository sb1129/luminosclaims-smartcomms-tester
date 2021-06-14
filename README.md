## Getting Started

#### Building and Dependencies

To download all the dependencies in this project, you must make sure your Maven _settings.xml_ is correctly setup. 
You need to make sure you are getting the jars from the correct repositories. By default, Maven will mirror **ALL** repositories
which will prevent you from accessing the required local and fineos repositories. To get passed this issue, you can change your
local settings.xml to include the following:


    <mirror>
        <id>nexus-public</id>
        <mirrorOf>central</mirrorOf>
        <url>http://jenkins.gbapres.local:8081/nexus/content/groups/public/</url>
    </mirror>
    <mirror>
        <id>nexus-fineos</id>
        <mirrorOf>fineos-generic-*</mirrorOf>
        <url>http://jenkins.gbapres.local:8081/nexus/content/groups/fineos-generic/</url>
    </mirror>


After adding the above, you can perform a normal _mvn clean install_ to get all the required dependencies.

#### Running the application

When running the application, you need to set the following values as VM options to get it to work:

`-Dconfigfile=config-gen.properties -Djavax.net.ssl.trustStore="c:\temp\cacerts" -Djavax.net.ssl.trustStorePassword=changeit` 

Remember, the path to your _cacerts_ may not be the same, so you will need to modify the above. 

#### Changing the payload file

The intial XML file that has all the document details gets set inside the config*.properties file, by changing the **testfile** property.

