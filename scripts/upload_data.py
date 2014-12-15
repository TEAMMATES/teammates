#This script should be placed in the GAE Python SDK directory.
#The path of the SDK will look like C:\Program Files (x86)\Google\google_appengine
#The script is to be used in conjunction with the generated_bulkloader.yaml file

#The script will upload all types of entities from the backup files to the GAE datastore.
#The only entity type that is not handled here is the StudentProfile entity type.
#As many backups would have been performed, the timestamp of which backup files to be used for uploading must be specified.
#The format of the timestamp is YYYY-MM-DD_HH-MM-SS.

import os
import datetime

#URL of the server (live or dev) where the data is uploaded to
#Change this to connect to a different dev server
#Remember to add /remote_api at the end of the URL
serverUrl = "https://teammatesv4.appspot.com/remote_api"

#Obtain the timestamp from the user and forms the upload file path
date_time = raw_input("Enter the date and time of the backup files to be uploaded. Format is YYYY-MM-DD_HH-MM-SS: ")
desktopPath = os.path.expanduser("~/Desktop/TM_Backup/")
backupFileDirectory = os.path.join(desktopPath, date_time)

#Runs a set of commands to upload all the data to the GAE datastore
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Account --filename %s/accounts.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Comment --filename %s/comment.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Course --filename %s/course.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Evaluation --filename %s/evaluation.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind FeedbackQuestion --filename %s/feedbackQuestion.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind FeedbackResponse --filename %s/feedbackResponse.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind FeedbackResponseComment --filename %s/feedbackResponseComment.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind FeedbackSession --filename %s/feedbackSession.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Instructor --filename %s/instructor.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Student --filename %s/student.csv" %(serverUrl,backupFileDirectory))
os.system("appcfg.py upload_data --url %s  --config_file generated_bulkloader.yaml --kind Submission --filename %s/submission.csv" %(serverUrl,backupFileDirectory))