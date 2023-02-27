import {
  ContributionStatistics,
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRankRecipientsResponseDetails,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import {
    DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS,
    DEFAULT_CONTRIBUTION_QUESTION_DETAILS,
    DEFAULT_MCQ_QUESTION_DETAILS,
    DEFAULT_NUMSCALE_QUESTION_DETAILS,
    DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
    DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS,
    DEFAULT_RUBRIC_QUESTION_DETAILS,
    DEFAULT_TEXT_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';
import { CommentTableModel } from '../../../components/comment-box/comment-table/comment-table.component';
import {
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import {
  QuestionSubmissionFormModel,
} from '../../../components/question-submission-form/question-submission-form-model';
import { Response } from '../../../components/question-types/question-statistics/question-statistics';
import {
    QuestionTabModel,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';

/**
 * Structure for example of essay question model
 */
export const EXAMPLE_ESSAY_QUESTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.TEXT,
  questionDetails: DEFAULT_TEXT_QUESTION_DETAILS(),

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of numerical scale question model
 */
export const EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.NUMSCALE,
  questionDetails: DEFAULT_NUMSCALE_QUESTION_DETAILS(),

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of numerical scale responses
 */
export const EXAMPLE_NUMERICAL_SCALE_RESPONSES: Response<FeedbackNumericalScaleResponseDetails>[] = [
  {
    giver: 'Alice',
    giverEmail: 'alice@gmail.com',
    giverTeam: 'Team 1',
    giverSection: '',
    recipient: 'Bob',
    recipientEmail: 'bob@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'Charles',
    giverEmail: 'charles@gmail.com',
    giverTeam: 'Team 1',
    giverSection: '',
    recipient: 'Bob',
    recipientEmail: 'bob@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'David',
    giverEmail: 'david@gmail.com',
    giverTeam: 'Team 1',
    giverSection: '',
    recipient: 'Bob',
    recipientEmail: 'bob@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 2,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'Bob',
    giverEmail: 'bob@gmail.com',
    giverTeam: 'Team 2',
    giverSection: '',
    recipient: 'Bob',
    recipientEmail: 'bob@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'Alice',
    giverEmail: 'alice@gmail.com',
    giverTeam: 'Team 1',
    giverSection: '',
    recipient: 'Emma',
    recipientEmail: 'emma@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 4,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'Charles',
    giverEmail: 'charles@gmail.com',
    giverTeam: 'Team 1',
    giverSection: '',
    recipient: 'Emma',
    recipientEmail: 'emma@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 3,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'David',
    giverEmail: 'david@gmail.com',
    giverTeam: 'Team 1',
    giverSection: '',
    recipient: 'Emma',
    recipientEmail: 'emma@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 4,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
  {
    giver: 'Emma',
    giverEmail: 'emma@gmail.com',
    giverTeam: 'Team 2',
    giverSection: '',
    recipient: 'Emma',
    recipientEmail: 'emma@gmail.com',
    recipientTeam: 'Team 2',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    },
  },
];
/**
 * Structure for example of numerical scale response output
 */
export const EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT: ResponseOutput[] = [
  {
    isMissingResponse: false,
    responseId: '1',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '2',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '3',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 2,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '4',
    giver: 'Bob',
    giverTeam: 'Team 2',
    giverEmail: 'bob@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '5',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 4,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '6',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 3,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '7',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 4,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '8',
    giver: 'Emma',
    giverTeam: 'Team 2',
    giverEmail: 'emma@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 5,
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleResponseDetails,
    instructorComments: [],
  },
];
/**
 * Structure for example of numerical scale question tab model
 */
export const EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL: QuestionTabModel = {
  question: EXAMPLE_NUMERICAL_SCALE_QUESTION_MODEL,
  responses: EXAMPLE_NUMERICAL_SCALE_RESPONSE_OUTPUT,
  statistics: '',
  hasPopulated: true,
  isTabExpanded: true,
};
/**
 * Structure for example of numerical scale questions
 */
export const EXAMPLE_NUMERICAL_SCALE_QUESTIONS: Record<string, QuestionTabModel> = {
  question: EXAMPLE_NUMERICAL_SCALE_QUESTION_TAB_MODEL,
};
/**
 * Structure for example of instructor comment tab model
 */
export const EXAMPLE_INSTRUCTOR_COMMENT_TABLE_MODEL: Record<string, CommentTableModel> = {
  1: {
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
    isReadOnly: true,
  },
  2: {
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
    isReadOnly: true,
  },
  3: {
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
    isReadOnly: true,
  },
  4: {
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
    isReadOnly: true,
  },
  5: {
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
    isReadOnly: true,
  },
  6: {
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
    isReadOnly: true,
  },
  7: {
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
    isReadOnly: true,
  },
  8: {
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
    isReadOnly: true,
  },
};
/**
 * Structure for example of feedback session
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
 * Structure for example of distrution point option question detail
 */
export const EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL: FeedbackConstantSumQuestionDetails = {
  constSumOptions: ['Option A', 'Option B'],
  distributeToRecipients: false,
  pointsPerOption: false,
  forceUnevenDistribution: false,
  distributePointsFor: FeedbackConstantSumDistributePointsType.NONE,
  points: 100,
  questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
  questionText: '',
  minPoint: 0,
  maxPoint: 100,
};
/**
 * Structure for example of distribution point option model
 */
export const EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
  questionDetails: EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_DETAIL,

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of distribute point option response output
 */
export const EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT: ResponseOutput[] = [
  {
    isMissingResponse: false,
    responseId: '1',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [2, 8],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '2',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [3, 7],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '3',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [5, 5],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '4',
    giver: 'Bob',
    giverTeam: 'Team 2',
    giverEmail: 'bob@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [5, 5],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '5',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [9, 1],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '6',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [6, 4],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '7',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [4, 6],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '8',
    giver: 'Emma',
    giverTeam: 'Team 2',
    giverEmail: 'emma@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answers: [7, 3],
      questionType: FeedbackQuestionType.CONSTSUM,
    } as FeedbackConstantSumResponseDetails,
    instructorComments: [],
  },
];
/**
 * Structure for example of distribute point option question tab model
 */
export const EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL: QuestionTabModel = {
  question: EXAMPLE_DISTRIBUTED_POINT_OPTION_MODEL,
  responses: EXAMPLE_DISTRIBUTE_POINT_OPTION_RESPONSE_OUTPUT,
  statistics: '',
  hasPopulated: true,
  isTabExpanded: true,
};
/**
 * Structure for example of distribute point option questions
 */
export const EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTIONS: Record<string, QuestionTabModel> = {
  question: EXAMPLE_DISTRIBUTE_POINT_OPTION_QUESTION_TAB_MODEL,
};
/**
 * Structure for example of distributed point recipient model
 */
export const EXAMPLE_DISTRIBUTED_POINT_RECIPIENT_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
  questionDetails: DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS(),

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of team contribution question model
 */
export const EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.CONTRIB,
  questionDetails: DEFAULT_CONTRIBUTION_QUESTION_DETAILS(),

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
    FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of team contribution response output
 */
export const EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT: ResponseOutput[] = [
  {
    isMissingResponse: false,
    responseId: '1',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 90,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '2',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 70,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '3',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 120,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '4',
    giver: 'Bob',
    giverTeam: 'Team 2',
    giverEmail: 'bob@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 100,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '5',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 110,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '6',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 110,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '7',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 100,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '8',
    giver: 'Emma',
    giverTeam: 'Team 2',
    giverEmail: 'emma@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 110,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    instructorComments: [],
  },
];
/**
 * Structure for example of contribution statistics
 */
export const EXAMPLE_CONTRIBUTION_STATISTICS: ContributionStatistics = {
  results: {
    'alice@gmail.com': {
      claimed: -999,
      perceived: -9999,
      claimedOthers: {
        'bob@gmail.com': 90,
        'emma@gmail.com': 110,
      },
      perceivedOthers: [],
    },
    'bob@gmail.com': {
      claimed: 100,
      perceived: 92,
      claimedOthers: {},
      perceivedOthers: [109, 90, 78, -9999],
    },
    'charles@gmail.com': {
      claimed: -999,
      perceived: -9999,
      claimedOthers: {
        'bob@gmail.com': 78,
        'emma@gmail.com': 122,
      },
      perceivedOthers: [],
    },
    'david@gmail.com': {
      claimed: -999,
      perceived: -9999,
      claimedOthers: {
        'bob@gmail.com': 109,
        'emma@gmail.com': 91,
      },
      perceivedOthers: [],
    },
    'emma@gmail.com': {
      claimed: 110,
      perceived: 108,
      claimedOthers: {},
      perceivedOthers: [122, 110, 91, -9999],
    },
  },
};
/**
 * Structure for example of team contribution question tab model
 */
export const EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL: QuestionTabModel = {
  question: EXAMPLE_TEAM_CONTRIBUTION_QUESTION_MODEL,
  responses: EXAMPLE_TEAM_CONTRIBUTION_RESPONSE_OUTPUT,
  statistics: JSON.stringify(EXAMPLE_CONTRIBUTION_STATISTICS),
  hasPopulated: true,
  isTabExpanded: true,
};
/**
 * Structure for example of team contribution questions
 */
export const EXAMPLE_TEAM_CONTRIBUTION_QUESTIONS: Record<string, QuestionTabModel> = {
  question: EXAMPLE_TEAM_CONTRIBUTION_QUESTION_TAB_MODEL,
};
/**
 * Structure for example of rubric question model
 */
export const EXAMPLE_RUBRIC_QUESTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.RUBRIC,
  questionDetails: {
    ...DEFAULT_RUBRIC_QUESTION_DETAILS(),
    rubricChoices: ['Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree'],
    rubricSubQuestions:
      ['This student participates well in online discussions.', 'This student completes assigned tasks on time.'],
    rubricDescriptions: [
      ['Rarely or never responds.', 'Occasionally responds, but never initiates discussions.',
        'Takes part in discussions and sometimes initiates discussions.',
        'Initiates discussions frequently, and engages the team.'],
      ['Rarely or never completes tasks.', 'Often misses deadlines.', 'Occasionally misses deadlines.',
        'Tasks are always completed before the deadline.']],
  } as FeedbackRubricQuestionDetails,

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of rubric question response output
 */
export const EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT: ResponseOutput[] = [
  {
    isMissingResponse: false,
    responseId: '1',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [0, 0],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '2',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [2, 2],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '3',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [1, 1],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '4',
    giver: 'Bob',
    giverTeam: 'Team 2',
    giverEmail: 'bob@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [1, 2],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '5',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [0, 1],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '6',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [2, 3],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '7',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [1, 3],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '8',
    giver: 'Emma',
    giverTeam: 'Team 2',
    giverEmail: 'emma@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: [0, 1],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    instructorComments: [],
  },
];
/**
 * Structure for example of rubric question questions tab model
 */
export const EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL: QuestionTabModel = {
  question: EXAMPLE_RUBRIC_QUESTION_MODEL,
  responses: EXAMPLE_RUBRIC_QUESTION_RESPONSE_OUTPUT,
  statistics: '',
  hasPopulated: true,
  isTabExpanded: true,
};
/**
 * Structure for example of rubric question questions
 */
export const EXAMPLE_RUBRIC_QUESTION_QUESTIONS: Record<string, QuestionTabModel> = {
  question: EXAMPLE_RUBRIC_QUESTION_QUESTION_TAB_MODEL,
};
/**
 * Structure for example of rank recipient question model
 */
export const EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.RANK_RECIPIENTS,
  questionDetails: DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS(),

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of rank recipient response output
 */
export const EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT: ResponseOutput[] = [
  {
    isMissingResponse: false,
    responseId: '1',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'Alice',
    recipientTeam: 'Team 1',
    recipientEmail: 'alice@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 2,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '2',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Alice',
    recipientTeam: 'Team 1',
    recipientEmail: 'alice@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 1,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '3',
    giver: 'Emma',
    giverTeam: 'Team 2',
    giverEmail: 'emma@gmail.com',
    giverSection: '',
    recipient: 'Bob',
    recipientTeam: 'Team 2',
    recipientEmail: 'bob@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 2,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '4',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'Charles',
    recipientTeam: 'Team 1',
    recipientEmail: 'charles@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 1,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '5',
    giver: 'David',
    giverTeam: 'Team 1',
    giverEmail: 'david@gmail.com',
    giverSection: '',
    recipient: 'Charles',
    recipientTeam: 'Team 1',
    recipientEmail: 'charles@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 3,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '6',
    giver: 'Alice',
    giverTeam: 'Team 1',
    giverEmail: 'alice@gmail.com',
    giverSection: '',
    recipient: 'David',
    recipientTeam: 'Team 1',
    recipientEmail: 'david@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 3,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '7',
    giver: 'Charles',
    giverTeam: 'Team 1',
    giverEmail: 'charles@gmail.com',
    giverSection: '',
    recipient: 'David',
    recipientTeam: 'Team 1',
    recipientEmail: 'david@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 2,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
  {
    isMissingResponse: false,
    responseId: '8',
    giver: 'Bob',
    giverTeam: 'Team 2',
    giverEmail: 'bob@gmail.com',
    giverSection: '',
    recipient: 'Emma',
    recipientTeam: 'Team 2',
    recipientEmail: 'emma@gmail.com',
    recipientSection: '',
    responseDetails: {
      answer: 1,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    } as FeedbackRankRecipientsResponseDetails,
    instructorComments: [],
  },
];
/**
 * Structure for example of rank recipient question tab model
 */
export const EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL: QuestionTabModel = {
  question: EXAMPLE_RANK_RECIPIENT_QUESTION_MODEL,
  responses: EXAMPLE_RANK_RECIPIENT_RESPONSE_OUTPUT,
  statistics: '',
  hasPopulated: true,
  isTabExpanded: true,
};
/**
 * Structure for example of rank recipient questions
 */
export const EXAMPLE_RANK_RECIPIENT_QUESTIONS: Record<string, QuestionTabModel> = {
  question: EXAMPLE_RANK_RECIPIENT_QUESTION_TAB_MODEL,
};
/**
 * Structure for example of rank option question model
 */
export const EXAMPLE_RANK_OPTION_QUESTION_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.RANK_OPTIONS,
  questionDetails: DEFAULT_RANK_OPTIONS_QUESTION_DETAILS(),

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
    FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of MCQ question without weights model
 */
export const EXAMPLE_MCQ_QUESTION_WITHOUT_WEIGHTS_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: 'How much did you think you contributed?',
  questionDescription: '',
  questionType: FeedbackQuestionType.MCQ,
  questionDetails: {
    ...DEFAULT_MCQ_QUESTION_DETAILS(),
    mcqChoices: ['I did great!', 'I performed satisfactorily.', 'I did not contribute as much as I wanted to.'],
    hasAssignedWeights: false,
    mcqWeights: [],
  } as FeedbackMcqQuestionDetails,

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
    FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of MCQ question with weights model
 */
export const EXAMPLE_MCQ_QUESTION_WITH_WEIGHTS_MODEL: QuestionEditFormModel = {
  feedbackQuestionId: '',
  isQuestionHasResponses: false,

  questionNumber: 1,
  questionBrief: 'How much did you think you contributed?',
  questionDescription: '',
  questionType: FeedbackQuestionType.MCQ,
  questionDetails: {
    ...DEFAULT_MCQ_QUESTION_DETAILS(),
    mcqChoices: ['I did great!', 'I performed satisfactorily.', 'I did not contribute as much as I wanted to.'],
    hasAssignedWeights: true,
    mcqWeights: [1, 3, 5],
  } as FeedbackMcqQuestionDetails,

  isDeleting: false,
  isDuplicating: false,
  isEditable: false,
  isSaving: false,
  isCollapsed: false,
  isVisibilityChanged: false,
  isFeedbackPathChanged: false,
  isQuestionDetailsChanged: false,

  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

  customNumberOfEntitiesToGiveFeedbackTo: 0,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
    FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
};
/**
 * Structure for example of responder rubric submission form model
 */
export const EXAMPLE_RESPONDER_RUBRIC_SUBMISSION_FORM_MODEL: QuestionSubmissionFormModel = {
  isLoading: false,
  isLoaded: true,
  isTabExpanded: true,
  recipientList: [
    {
      recipientIdentifier: 'alice',
      recipientName: 'Alice',
    },
    {
      recipientIdentifier: 'bob',
      recipientName: 'Bob',
    },
  ],
  recipientSubmissionForms: [
    {
      responseId: 'response1',
      recipientIdentifier: 'alice',
      responseDetails: { questionType: FeedbackQuestionType.RUBRIC, answer: [0, 2] } as FeedbackRubricResponseDetails,
      isValid: true,
    },
    {
      responseId: 'response2',
      recipientIdentifier: 'bob',
      responseDetails: { questionType: FeedbackQuestionType.RUBRIC, answer: [1, 3] } as FeedbackRubricResponseDetails,
      isValid: true,
    },
  ],
  customNumberOfEntitiesToGiveFeedbackTo: 0,
  feedbackQuestionId: '',
  questionNumber: 1,
  questionBrief: '',
  questionDescription: '',
  questionType: FeedbackQuestionType.RUBRIC,
  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
  showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  questionDetails: {
    ...DEFAULT_RUBRIC_QUESTION_DETAILS(),
    rubricChoices: ['Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree'],
    rubricSubQuestions: [
      'This student participates well in online discussions.',
      'This student completes assigned tasks on time.',
    ],
    rubricDescriptions:
    [
      [
        'Rarely or never responds.', 'Occasionally responds, but never initiates discussions.',
        'Takes part in discussions and sometimes initiates discussions.',
        'Initiates discussions frequently, and engages the team.',
      ],
      [
        'Rarely or never completes tasks.',
        'Often misses deadlines.',
        'Occasionally misses deadlines.',
        'Tasks are always completed before the deadline.',
      ],
    ],
  } as FeedbackRubricQuestionDetails,
};
