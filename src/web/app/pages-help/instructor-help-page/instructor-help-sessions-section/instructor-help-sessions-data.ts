import { TemplateSession } from '../../../../services/feedback-sessions.service';
import {
  CommentVisibilityType,
  Course, FeedbackContributionResponseDetails, FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType, FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackVisibilityType,
  Instructor,
  InstructorPermissionRole,
  JoinState,
  NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../../types/api-output';
import {
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS, DEFAULT_MCQ_QUESTION_DETAILS,
  DEFAULT_MCQ_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { CommentEditFormModel } from '../../../components/comment-box/comment-edit-form/comment-edit-form.component';
import { CommentTableModel } from '../../../components/comment-box/comment-table/comment-table.component';
import {
  SessionEditFormModel,
} from '../../../components/session-edit-form/session-edit-form-model';
import {
  RecycleBinFeedbackSessionRowModel,
} from '../../../components/sessions-recycle-bin-table/sessions-recycle-bin-table.component';
import {
  SectionTabModel,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import { FeedbackQuestionModel } from '../../../pages-session/session-result-page/session-result-page.component';

/**
 * Structure of example session edit form model
 */
export const EXAMPLE_SESSION_EDIT_FORM_MODEL: SessionEditFormModel = {
  courseId: 'CS2103T',
  timeZone: 'UTC',
  courseName: 'Software Engineering',
  feedbackSessionName: 'Feedback for Project',
  instructions: 'This is where you type the instructions for the session',

  submissionStartTime: { hour: 10, minute: 0 },
  submissionStartDate: { year: 2020, month: 3, day: 13 },
  submissionEndTime: { hour: 12, minute: 0 },
  submissionEndDate: { year: 2020, month: 3, day: 13 },
  gracePeriod: 0,

  sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
  customSessionVisibleTime: { hour: 9, minute: 0 },
  customSessionVisibleDate: { year: 2020, month: 3, day: 13 },

  responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
  customResponseVisibleTime: { hour: 13, minute: 0 },
  customResponseVisibleDate: { year: 2020, month: 3, day: 13 },

  submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
  publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

  isClosingEmailEnabled: true,
  isPublishedEmailEnabled: true,

  templateSessionName: 'Example session',

  isSaving: false,
  isEditable: false,
  isDeleting: false,
  isCopying: false,
  hasVisibleSettingsPanelExpanded: true,
  hasEmailSettingsPanelExpanded: true,
};
/**
 * Structure of example of comment edit form model
 */
export const EXAMPLE_COMMENT_EDIT_FORM_MODEL: CommentEditFormModel = {
  commentText: '',

  isUsingCustomVisibilities: false,
  showCommentTo: [],
  showGiverNameTo: [],
};
/**
 * Structure of example response
 */
export const EXAMPLE_RESPONSE: ResponseOutput = {
  isMissingResponse: false,
  responseId: '',
  giver: 'Alice',
  giverTeam: 'Team A',
  giverSection: 'Section A',
  recipient: 'Bob',
  recipientTeam: 'Team B',
  recipientSection: 'Section B',
  responseDetails: DEFAULT_CONTRIBUTION_RESPONSE_DETAILS(),
  instructorComments: [],
};
/**
 * Structure of example response with comment
 */
export const EXAMPLE_RESPONSE_WITH_COMMENT: ResponseOutput = {
  isMissingResponse: false,
  responseId: '1',
  giver: 'Alice',
  giverTeam: 'Team A',
  giverSection: 'Section A',
  recipient: 'Bob',
  recipientTeam: 'Team B',
  recipientSection: 'Section B',
  responseDetails: {
    answer: 110,
    questionType: FeedbackQuestionType.CONTRIB,
  } as FeedbackContributionResponseDetails,
  instructorComments: [{
    commentGiver: 'Instructor',
    lastEditorEmail: '',
    feedbackResponseCommentId: 1,
    commentText: 'Good to know!',
    createdAt: 1,
    lastEditedAt: 1,
    isVisibilityFollowingFeedbackQuestion: true,
    showGiverNameTo: [CommentVisibilityType.GIVER],
    showCommentTo: [CommentVisibilityType.GIVER],
  }],
};
/**
 * Structure of example course candidates
 */
export const EXAMPLE_COURSE_CANDIDATES: Course[] = [
  {
    courseId: 'CS2103T',
    courseName: 'Software Engineering',
    institute: 'TEAMMATES Test Institute 1',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  },
];
/**
 * Structure of example template sessions
 */
export const EXAMPLE_TEMPLATE_SESSIONS: TemplateSession[] = [
  {
    name: 'Example session',
    questions: [],
  },
];
/**
 * Structure of example students
 */
export const EXAMPLE_STUDENTS: Student[] = [
  {
    email: 'alice@email.com',
    courseId: 'test.exa-demo',
    name: 'Alice Betsy',
    comments: 'Alice is a transfer student.',
    teamName: 'Team A',
    sectionName: 'Section A',
    joinState: JoinState.JOINED,
  },
];
/**
 * Structure of example instructors
 */
export const EXAMPLE_INSTRUCTORS: Instructor[] = [
  {
    googleId: 'bob@email.com',
    courseId: 'test.exa-demo',
    email: 'bob@email.com',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: 'Bob Ruth',
    key: 'impicklerick',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  },
];

/**
 * Structure of example feedback session
 */
export const EXAMPLE_FEEDBACK_SESSION: FeedbackSession = {
  courseId: 'CS2103T',
  timeZone: 'UTC',
  feedbackSessionName: 'Project Feedback 1',
  instructions: 'Enter your feedback for projects',
  submissionStartTimestamp: 0,
  submissionEndTimestamp: 0,
  gracePeriod: 0,
  sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
  responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
  submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
  publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
  isClosingEmailEnabled: true,
  isPublishedEmailEnabled: true,
  createdAtTimestamp: 0,
  studentDeadlines: {},
  instructorDeadlines: {},
};

/**
 * Structure of example recycle bin feedback sessions
 */
export const EXAMPLE_RECYCLE_BIN_FEEDBACK_SESSIONS: RecycleBinFeedbackSessionRowModel[] = [
  {
    feedbackSession: EXAMPLE_FEEDBACK_SESSION,
  },
];

/**
 * Structure of example instructor comment table model
 */
export const EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL: Record<string, CommentTableModel> = {
  'feedbackQuestionId%bob@example.com%bob@example.com': {
    isReadOnly: true,
    commentRows: [],
    newCommentRow: {
      commentEditFormModel: {
        commentText: '',
        isUsingCustomVisibilities: true,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: false,
    },
    isAddingNewComment: true,
  },
  'feedbackQuestionId%bob@example.com%danny@example.com': {
    isReadOnly: true,
    commentRows: [],
    newCommentRow: {
      commentEditFormModel: {
        commentText: '',
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: false,
    },
    isAddingNewComment: false,
  },
};

/**
 * Structure of example GRQ responses
 */
export const EXAMPLE_GRQ_RESPONSES: Record<string, SectionTabModel> = {
  'Section A': {
    questions: [
      {
        feedbackQuestion: {
          feedbackQuestionId: 'feedbackQuestionId',
          questionNumber: 1,
          questionBrief: 'How well did team member perform?',
          questionDescription: '',
          questionDetails: {
            hasAssignedWeights: false,
            mcqWeights: [],
            mcqOtherWeight: 0,
            mcqChoices: [
              '<p>Good</p>',
              '<p>Normal</p>',
              '<p>Bad</p>',
            ],
            otherEnabled: false,
            questionDropdownEnabled: false,
            generateOptionsFor: 'NONE',
            questionType: FeedbackQuestionType.MCQ,
            questionText: 'How well did team member perform?',
          } as FeedbackMcqQuestionDetails,
          questionType: FeedbackQuestionType.MCQ,
          giverType: FeedbackParticipantType.STUDENTS,
          recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
          numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
          showResponsesTo: [],
          showGiverNameTo: [],
          showRecipientNameTo: [],
          customNumberOfEntitiesToGiveFeedbackTo: 0,
        },
        questionStatistics: '',
        allResponses: [
          {
            responseId: 'feedbackQuestionId%bob@example.com%bob@example.com',
            giver: 'Bob Ruth',
            giverTeam: 'Team A',
            giverEmail: 'bob@example.com',
            giverSection: 'Section A',
            recipient: 'Bob Ruth',
            recipientTeam: 'Team A',
            recipientEmail: 'bob@example.com',
            recipientSection: 'Section A',
            responseDetails: {
              answer: '<p>Good</p>',
              isOther: false,
              otherFieldContent: '',
              questionType: FeedbackQuestionType.MCQ,
            } as FeedbackMcqResponseDetails,
            instructorComments: [],
            isMissingResponse: false,
          },
          {
            responseId: 'feedbackQuestionId%bob@example.com%danny@example.com',
            giver: 'Bob Ruth',
            giverTeam: 'Team A',
            giverEmail: 'bob@example.com',
            giverSection: 'Section A',
            recipient: 'Danny Engrid',
            recipientTeam: 'Team B',
            recipientEmail: 'danny@example.com',
            recipientSection: 'Section B',
            responseDetails: {
              answer: '<p>Bad</p>',
              isOther: false,
              otherFieldContent: '',
              questionType: FeedbackQuestionType.MCQ,
            } as FeedbackMcqResponseDetails,
            instructorComments: [],
            isMissingResponse: false,
          },
        ],
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
        responsesToSelf: [],
        responsesFromSelf: [],
        otherResponses: [],
      },
    ],
    hasPopulated: true,
    isTabExpanded: true,
  },
};

/**
 * Structure of example questions with responses
 */
export const EXAMPLE_QUESTIONS_WITH_RESPONSES: FeedbackQuestionModel[] = [{
  feedbackQuestion: {
    feedbackQuestionId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw',
    questionNumber: 1,
    questionBrief: 'How well did this team member perform?',
    questionDescription: '',
    questionDetails: {
      ...DEFAULT_MCQ_QUESTION_DETAILS(),
      questionText: 'How well did this team member perform?',
      mcqChoices: [
        '<p>Good</p>',
        '<p>Normal</p>',
        '<p>Bad</p>',
      ],
    } as FeedbackMcqQuestionDetails,
    questionType: FeedbackQuestionType.MCQ,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  },
  questionStatistics: '',
  allResponses: [EXAMPLE_RESPONSE_WITH_COMMENT],
  responsesToSelf: [
    {
      responseId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw'
          + '%benny.c.tmms@gmail.tmt%alice.b.tmms@gmail.tmt',
      giver: 'Benny Charles',
      giverTeam: 'Team A',
      giverSection: 'Section A',
      recipient: 'You',
      recipientTeam: 'Team A',
      recipientSection: 'Section A',
      responseDetails: {
        ...DEFAULT_MCQ_RESPONSE_DETAILS(),
        answer: '<p>Good</p>',
      } as FeedbackMcqResponseDetails,
      instructorComments: [],
      isMissingResponse: false,
    },
  ],
  responsesFromSelf: [
    {
      responseId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw'
          + '%alice.b.tmms@gmail.tmt%alice.b.tmms@gmail.tmt',
      giver: 'You',
      giverTeam: 'Team A',
      giverSection: 'Section A',
      recipient: 'You',
      recipientSection: 'Section A',
      recipientTeam: 'Team A',
      responseDetails: {
        ...DEFAULT_MCQ_RESPONSE_DETAILS(),
        answer: '<p>Good</p>',
      } as FeedbackMcqResponseDetails,
      instructorComments: [],
      isMissingResponse: false,
    },
    {
      responseId: 'ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIDICQw'
          + '     %alice.b.tmms@gmail.tmt%benny.c.tmms@gmail.tmt',
      giver: 'You',
      giverTeam: 'Team A',
      giverSection: 'Section A',
      recipient: 'Benny Charles',
      recipientTeam: 'Team A',
      recipientSection: 'Section A',
      responseDetails: {
        ...DEFAULT_MCQ_RESPONSE_DETAILS(),
        answer: '<p>Bad</p>',
      } as FeedbackMcqResponseDetails,
      instructorComments: [],
      isMissingResponse: false,
    },
  ],
  otherResponses: [],
  isLoaded: true,
  isLoading: false,
  hasResponse: true,
  hasResponseButNotVisibleForPreview: false,
  hasCommentNotVisibleForPreview: false,
}];
