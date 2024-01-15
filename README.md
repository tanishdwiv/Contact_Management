# Contact Management System

# Required Tools
1)Any Java IDE e.g. IntelliJ, Eclipse, NetBeans etcetera (IntelliJ recommended - any edition will do, community ediion is free.

2)MySql database (free)

3)Java should be installed on your system.

# Set up Guide
1)Some of the following instructions are specific to IntelliJ, as that was what we used. Project can run and work with other IDEs as well, but I cannot provide any specifics regarding them.

2)Once Java is installed, you will need to download javafx sdk 15. I have provided it in this repository. Folder can be placed anywhere, but preferably place it within your project directory.

3)Download and install MySql. The default installation options will work fine, but you can modify if you need to. Set username and password as you like, and create a database/schema (name can be anything.)

4)In the folder sql files in this repository, there are two txt files. 'create_tables.txt' has all the queries required to create the required tables and populate them with dummy data. If you do not want to use the dummy data, then only select and run the create queries (not the insertion queries.)

5)You can run queries by copy/pasting them into the MySql console, which can be opened in MySql once you have created your schema/database.

6)Create project in IntelliJ, default package name is 'sample'. If you alter this, then make sure to change the first line 'package sample' in every .java file and set it to 'package <your-package-name>'. The Java sdk used should be version 15.

7)All the external java libraries required have been added in this repository, in the 'required jar files' folder. To add them to your project, do the following in IntelliJ:

Open your project
Click File from the toolbar
Select Project Structure option
Select Modules in the left panel
Select Dependencies tab
Select + icon
Select 1 JARs or directories option
(Optional) Read this to see alternative ways of adding JAR files
To add the JavaFX files, do the following in IntelliJ:

Go to Run -> Edit Configuration -> VM options
Edit path to point to the javafx15sdk\lib folder (provided in this repository)
e.g my path is '--module-path "C:\Users\HP\Desktop\EMS\Java\javafx-sdk-15.0.1\lib" --add-modules javafx.controls,javafx.fxml'
Your path should be exactly same, except for the "C:\Users\HP\Desktop...". This part only depends on wherever you placed the javafx sdk folder
In the src file main.java, provide your database details (name, username, password) in the function setMySQL() and give your email address and password in the function setEmailDetails().

Run project and enjoy :)
