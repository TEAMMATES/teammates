:: Please modify the following file path name to the folder root folder of TEAMMATES
set filepath=%~dp0
set jarName=backup.jar

:: Change directory to the TEAMMATES rootfolder so that files will be saved here
cd %filepath%..

:: Run the backup jar file
java.exe -jar %filepath%..\%jarName%