import {
  FeedbackConstantSumRecipientsQuestionDetails,
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
 * A template session with questions
 */
export interface TemplateSession {
  name: string;
  questions: FeedbackQuestion[];
}

/**
 * Template sessions for feedback
 */
export const templateSessions: TemplateSession[] = [
  {
    name: 'session using template: team peer feedback (point-based)',
    questions: [
      {
        feedbackQuestionId: '',
        questionNumber: 1,
        questionBrief:
          'How much work did each team member contribute? (response will be shown anonymously to each team member). ',
        questionDescription:
          'If a team member did an equal share of the work, give 100 points. If a team member did about 10% more than an equal share of the work, give 110 points, and so on.',
        questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
        questionDetails: {
          questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          questionText: 'How much work did each team member contribute?',
          pointsPerOption: true,
          forceUnevenDistribution: false,
          distributePointsFor: 'None',
          points: 100,
        } as FeedbackConstantSumRecipientsQuestionDetails,
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
      {
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
      {
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
      {
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
      {
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
    ],
  },
  {
    name: 'session using template: team peer feedback (percentage-based)',
    questions: [
      {
        feedbackQuestionId: '',
        questionNumber: 1,
        questionBrief:
          'How much work did each team member contribute? (response will be shown anonymously to each team member). ',
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
      {
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
      {
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
      {
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
      {
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
    ],
  },
  {
    name: 'session with my own questions',
    questions: [],
  },
];
