#This script should be placed in the GAE Python SDK directory.
#The path of the SDK will look like C:\Program Files (x86)\Google\google_appengine
#The script is to be used in conjunction with the generated_bulkloader.yaml file

#The script will download all types of entities from the GAE datastore except the StudentProfile entity type.
#The backup files will be stored on the Desktop with a timestamp of when the the backup is performed.


import os
import datetime

#Creates a folder on desktop with a timestamp of the backup
desktopFile = os.path.expanduser("~/Desktop/TM_Backup/")
mydir = os.path.join(desktopFile, datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S'))
os.makedirs(mydir)

#Runs a set of commands to download all the different types of entities from the datastore
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Account --filename %s/accounts.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Comment --filename %s/comment.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Course --filename %s/course.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Evaluation --filename %s/evaluation.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackQuestion --filename %s/feedbackQuestion.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackResponse --filename %s/feedbackResponse.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackResponseComment --filename %s/feedbackResponseComment.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackSession --filename %s/feedbackSession.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Instructor --filename %s/instructor.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Student --filename %s/student.csv" %mydir)
os.system("bulkloader.py --download --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Submission --filename %s/submission.csv" %mydir)