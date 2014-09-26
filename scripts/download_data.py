#This script should be placed in the GAE Python SDK directory.
#The path of the SDK will look like C:\Program Files (x86)\Google\google_appengine
#The script is to be used in conjunction with the generated_bulkloader.yaml file

#The script will download all types of entities from the GAE datastore except the StudentProfile entity type.
#The backup files will be stored on the Desktop with a timestamp of when the the backup is performed.


import os
import datetime

#URL of live server to download the data from
#Change this to connect to a different live server
#Remember to add /remote_api at the end of the URL
liveServerUrl = "https://teammatesv4.appspot.com/remote_api"

#URL of local dev server where the downloaded data is uploaded to
#Change this to connect to a different dev server
#Remember to add /remote_api at the end of the URL
devServerUrl = "http://localhost:8888/remote_api"

#Creates a folder on desktop with a timestamp of the backup
desktopFile = os.path.expanduser("~/Desktop/TM_Backup/")
fileLocation = os.path.join(desktopFile, datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S'))
os.makedirs(fileLocation)

#Runs a set of commands to download all the different types of entities from the datastore
#User name and password will be your own google account
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Account --filename %s/accounts.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Comment --filename %s/comment.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Course --filename %s/course.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Evaluation --filename %s/evaluation.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind FeedbackQuestion --filename %s/feedbackQuestion.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind FeedbackResponse --filename %s/feedbackResponse.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind FeedbackResponseComment --filename %s/feedbackResponseComment.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind FeedbackSession --filename %s/feedbackSession.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Instructor --filename %s/instructor.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Student --filename %s/student.csv" %(liveServerUrl,fileLocation))
os.system("bulkloader.py --download --url %s --config_file generated_bulkloader.yaml --kind Submission --filename %s/submission.csv" %(liveServerUrl,fileLocation))


#Uploads the downloaded data to the local dev server
#User name and password is arbitrary, but you will not be prompted for it as the script will handle it
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Account --filename %s/accounts.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Comment --filename %s/comment.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Course --filename %s/course.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Evaluation --filename %s/evaluation.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind FeedbackQuestion --filename %s/feedbackQuestion.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind FeedbackResponse --filename %s/feedbackResponse.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind FeedbackResponseComment --filename %s/feedbackResponseComment.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind FeedbackSession --filename %s/feedbackSession.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Instructor --filename %s/instructor.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Student --filename %s/student.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
os.system("echo 'XX' | appcfg.py upload_data --url %s --config_file generated_bulkloader.yaml --kind Submission --filename %s/submission.csv --email=aaa@gmail.com --passin" %(devServerUrl,fileLocation))
