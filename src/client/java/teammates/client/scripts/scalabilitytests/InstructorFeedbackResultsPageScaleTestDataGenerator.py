import names
import json

FILENAME = 'data/InstructorFeedbackResultsPageScaleTest'

accounts = {
    'CFResultsUiT.instr': {
        'googleId': 'CFResultsUiT.instr',
        'name': 'Teammates Test',
        'isInstructor': 'true',
        'email': 'CFResultsUiT.instr@gmail.tmt',
        'institute': 'TEAMMATES Test Institute 1'
    }
}

courses = {
    'CFResultsUiT.CS2104': {
        'id': 'CFResultsUiT.CS2104',
        'name': 'Programming Language Concepts',
        'timeZone': 'UTC'
    }
}
instructors = {
    'CFResultsUiT.instr': {
        'googleId': 'CFResultsUiT.instr',
        'courseId': 'CFResultsUiT.CS2104',
        'name': 'Teammates Test',
        'email': 'CFResultsUiT.instr@gmail.tmt',
        'role': 'Co-owner',
        'isDisplayedToStudents': 'false',
        'displayedName': 'Instructor',
        'privileges': {
            'courseLevel': {
                'canviewstudentinsection': 'true',
                'cangivecommentinsection': 'true',
                'cansubmitsessioninsection': 'true',
                'canmodifysessioncommentinsection': 'true',
                'canmodifycommentinsection': 'true',
                'canmodifycourse': 'true',
                'canviewsessioninsection': 'true',
                'canmodifysession': 'true',
                'canviewcommentinsection': 'true',
                'canmodifystudent': 'true',
                'canmodifyinstructor': 'true'
            },
            'sectionLevel': {},
            'sessionLevel': {}
        }
    }
}


def gen_students(num):
    students = {}
    for i in range(num):
        student = {}
        name = names.get_full_name()
        while name in students:
            name = names.get_full_name()
        student['name'] = name
        student['course'] = "CFResultsUiT.CS2104"
        student['googleId'] = 'CFResultsUiT.' + name.replace(' ', '.')
        student['email'] = student['googleId'] + '@gmail.tmt'
        student['comments'] = "This is student " + name
        student['team'] = 'Team 1'
        student['section'] = 'Section A'
        students[name] = student
    return students


def gen_questions(num):
    feedbackQuestions = {}
    for i in range(num):
        question = {}
        question['feedbackSessionName'] = 'Open Session'
        question['courseId'] = 'CFResultsUiT.CS2104'
        question['creatorEmail'] = 'CFResultsUiT.instr@gmail.tmt'
        question['questionMetaData'] = {'value': 'Rate  other students'}
        question['questionNumber'] = i + 1
        question['questionType'] = 'TEXT'
        question['giverType'] = 'STUDENTS'
        question['recipientType'] = 'STUDENTS'
        question['numberOfEntitiesToGiveFeedbackTo'] = 4
        question['showResponsesTo'] = ['INSTRUCTORS',
                                       'RECEIVER',
                                       'STUDENTS',
                                       'OWN_TEAM_MEMBERS']
        question['showGiverNameTo'] = ['INSTRUCTORS',
                                       'OWN_TEAM_MEMBERS']
        question['showRecipientNameTo'] = ['INSTRUCTORS',
                                           'RECEIVER']
        feedbackQuestions['question' + str(i)] = question
    return feedbackQuestions


def gen_responses(students, numQuestions):
    num = 0
    responses = {}
    for question in range(numQuestions):
        for k1, giver in students.items():
            for k2, receiver in students.items():
                if giver != receiver:
                    response = {}
                    response['feedbackSessionName'] = 'Open Session'
                    response['courseId'] = 'CFResultsUiT.CS2104'
                    response['feedbackQuestionId'] = str(question + 1)
                    response['feedbackQuestionType'] = 'TEXT'
                    response['giver'] = giver['email']
                    response['recipient'] = receiver['email']
                    response['responseMetaData'] = {'value': 'Response '}
                    response['giverSection'] = 'Section A'
                    response['recipientSection'] = 'Section A'
                    num += 1
                    responses['response' + str(num)] = response
    return responses


feedbackSessions = {
    'Open Session': {
        'feedbackSessionName': 'Open Session',
        'courseId': 'CFResultsUiT.CS2104',
        'creatorEmail': 'CFResultsUiT.instr@gmail.tmt',
        'instructions': {
            'value': 'Instructions for Open session'
        },
        'createdTime': '2012-04-01 11:59 PM UTC',
        'startTime': '2012-04-01 11:59 PM UTC',
        'endTime': '2026-04-30 11:59 PM UTC',
        'sessionVisibleFromTime': '2012-04-01 11:59 PM UTC',
        'resultsVisibleFromTime': '2012-05-01 11:59 PM UTC',
        'timeZone': 8.0,
        'gracePeriod': 10,
        'feedbackSessionType': 'STANDARD',
        'sentOpenEmail': 'true',
        'sentClosingEmail': 'false',
        'sentClosedEmail': 'false',
        'sentPublishedEmail': 'true',
        'isOpeningEmailEnabled': 'true',
        'isClosingEmailEnabled': 'true',
        'isPublishedEmailEnabled': 'true'
    },
    'Session with no sections': {
        'feedbackSessionName': 'NoSections Session',
        'courseId': 'CFResultsUiT.NoSections',
        'creatorEmail': 'CFResultsUiT.instr@gmail.tmt',
        'instructions': {
            'value': 'This session has errors'
        },
        'createdTime': '2012-04-01 11:59 PM UTC',
        'startTime': '2012-04-02 11:59 PM UTC',
        'endTime': '2026-04-30 11:59 PM UTC',
        'sessionVisibleFromTime': '2012-04-01 11:59 PM UTC',
        'resultsVisibleFromTime': '2012-05-01 11:59 PM UTC',
        'timeZone': 8.0,
        'gracePeriod': 20,
        'feedbackSessionType': 'STANDARD',
        'sentOpenEmail': 'true',
        'sentClosingEmail': 'false',
        'sentClosedEmail': 'false',
        'sentPublishedEmail': 'true',
        'isOpeningEmailEnabled': 'true',
        'isClosingEmailEnabled': 'true',
        'isPublishedEmailEnabled': 'true'
    },
}


studentNums = [10, 20]
questionNums = [1, 5, 10, 20, 50]

for numStudent in studentNums:
    for numQuestion in questionNums:
        students = gen_students(numStudent)
        data = {'accounts': accounts, 'courses': courses,
                'instructors': instructors,
                'students': students, 'feedbackSessions': feedbackSessions,
                'feedbackQuestions': gen_questions(numQuestion),
                'feedbackResponses': gen_responses(students, numQuestion),
                'feedbackResponseComments': {}, 'comments': {}, 'profiles': {}}

        with open(FILENAME + '-' + str(numStudent) + 'Students'
                  + str(numQuestion) + 'Questions.json', 'w') as file:
            file.write(json.dumps(data, indent=2, sort_keys=True))
