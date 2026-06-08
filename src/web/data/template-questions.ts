import {
  FeedbackContributionQuestionDetails,
  FeedbackQuestion,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
} from '../types/api-output';

/**
 * A template question with description
 */
export interface TemplateQuestion {
  description: string;
  question: FeedbackQuestion;
}

/**
 * Template questions for feedback
 */
export const templateQuestions: TemplateQuestion[] = [
  {
    description:
      'Use <b>peer estimates</b> to determine the <b>work distribution percentage</b> among team members in <b>a team activity</b>',
    question: {
      feedbackQuestionId: '',
      questionNumber: 1,
      questionBrief:
        'How much work did each team member contribute? (response will be shown anonymously to each team member).',
      questionDescription: '',

      questionType: FeedbackQuestionType.CONTRIB,
      questionDetails: {
        questionType: FeedbackQuestionType.CONTRIB,
        questionText: 'How much work did each team member contribute?',
        isZeroSum: true,
        isNotSureAllowed: false,
      } as FeedbackContributionQuestionDetails,

      giverType: QuestionGiverType.STUDENTS,
      recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF,

      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: 1,

      showResponsesTo: [
        FeedbackVisibilityType.INSTRUCTORS,
        FeedbackVisibilityType.RECIPIENT,
        FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
      ],
      showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
      showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    },
  },
  {
    description: 'Ask each student to describe something related to themselves i.e., <b>self-reflection</b>',
    question: {
      feedbackQuestionId: '',
      questionNumber: 2,
      questionBrief: 'What contributions did you make to the team? (response will be shown to each team member).',
      questionDescription: '',

      questionType: FeedbackQuestionType.TEXT,
      questionDetails: {
        questionType: FeedbackQuestionType.TEXT,
        questionText: '',
        shouldAllowRichText: true,
      } as FeedbackTextQuestionDetails,

      giverType: QuestionGiverType.STUDENTS,
      recipientType: QuestionRecipientType.SELF,

      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: 1,

      showResponsesTo: [
        FeedbackVisibilityType.RECIPIENT,
        FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
        FeedbackVisibilityType.INSTRUCTORS,
      ],
      showGiverNameTo: [
        FeedbackVisibilityType.RECIPIENT,
        FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
        FeedbackVisibilityType.INSTRUCTORS,
      ],
      showRecipientNameTo: [
        FeedbackVisibilityType.RECIPIENT,
        FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
        FeedbackVisibilityType.INSTRUCTORS,
      ],
    },
  },
  {
    description:
      'Ask each student to give <b>confidential peer feedback (qualitative)</b> to other <b>team members</b>',
    question: {
      feedbackQuestionId: '',
      questionNumber: 3,
      questionBrief:
        'What comments do you have regarding each of your team members? (response is confidential and will only be shown to the instructor).',
      questionDescription: '',

      questionType: FeedbackQuestionType.TEXT,
      questionDetails: {
        questionType: FeedbackQuestionType.TEXT,
        questionText: '',
        shouldAllowRichText: true,
      } as FeedbackTextQuestionDetails,

      giverType: QuestionGiverType.STUDENTS,
      recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS,

      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: 1,

      showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
      showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
      showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    },
  },
  {
    description: 'Ask each student to give <b>confidential feedback (qualitative)</b> about the team behaviour',
    question: {
      feedbackQuestionId: '',
      questionNumber: 4,
      questionBrief:
        'How are the team dynamics thus far? (response is confidential and will only be to the instructor).',
      questionDescription: '',

      questionType: FeedbackQuestionType.TEXT,
      questionDetails: {
        questionType: FeedbackQuestionType.TEXT,
        questionText: '',
        shouldAllowRichText: true,
      } as FeedbackTextQuestionDetails,

      giverType: QuestionGiverType.STUDENTS,
      recipientType: QuestionRecipientType.OWN_TEAM,

      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: 1,

      showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
      showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
      showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    },
  },
  {
    description: 'Ask each student to give <b>comments about other team members, in confidence</b>',
    question: {
      feedbackQuestionId: '',
      questionNumber: 5,
      questionBrief:
        'What feedback do you have for each of your team members? (response will be shown anonymously to each team member).',
      questionDescription: '',

      questionType: FeedbackQuestionType.TEXT,
      questionDetails: {
        questionType: FeedbackQuestionType.TEXT,
        questionText: '',
        shouldAllowRichText: true,
      } as FeedbackTextQuestionDetails,

      giverType: QuestionGiverType.STUDENTS,
      recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS,

      numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: 1,

      showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
      showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    },
  },
];
