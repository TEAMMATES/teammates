import os
import datetime

#Creates a folder on desktop with a timestamp of the backup
date_time = raw_input("Enter the date and time of the backup files to be uploaded. Format is YYYY-MM-DD_HH-MM-SS: ")
desktopPath = os.path.expanduser("~/Desktop/TM_Backup/")
backupFileDirectory = os.path.join(desktopPath, date_time)

#Downloads all the types of entities
#Student profiles will be handled separately
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Account --filename %s/accounts.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Comment --filename %s/comment.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Course --filename %s/course.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Evaluation --filename %s/evaluation.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackQuestion --filename %s/feedbackQuestion.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackResponse --filename %s/feedbackResponse.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackResponseComment --filename %s/feedbackResponseComment.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind FeedbackSession --filename %s/feedbackSession.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Instructor --filename %s/instructor.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Student --filename %s/student.csv" %backupFileDirectory)
os.system("appcfg.py upload_data --url https://teammatesv4.appspot.com/remote_api  --config_file generated_bulkloader.yaml --kind Submission --filename %s/submission.csv" %backupFileDirectory)