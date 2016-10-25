:: Sets the file path. Change the file path to the root directory of TEAMMATES if this variable does not work.
set filepath=%~dp0
set backupBat=runbackup.bat

:: Creates a scheduled task using the specified time intervals
%SystemRoot%\system32\schtasks.exe /create /sc daily /tn "Backup" /tr "%filepath%%backupBat%"