import { FeedbackParticipantType } from '../app/feedback-participant-type';
import {
  FeedbackQuestion,
  FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../app/feedback-question';
import { FeedbackVisibilityType } from '../app/feedback-visibility';

/**
 * A template session.
 */
export interface TemplateSession {
  name: string;
  questions: FeedbackQuestion[];
}

/**
 * Template sessions.
 */
export const templateSessions: TemplateSession[] = [
  {
    name: 'session using template: team peer evaluation',
    questions: [
      {
        feedbackQuestionId: '',
        questionNumber: 1,
        questionBrief: 'Your estimate of how much each team member has contributed.',
        questionDescription: '',

        questionType: FeedbackQuestionType.CONTRIB,
        questionDetails: {
          isNotSureAllowed: false,
        },

        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,

        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
        customNumberOfEntitiesToGiveFeedbackTo: 1,

        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
          FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
      },
      {
        feedbackQuestionId: '',
        questionNumber: 2,
        questionBrief: 'Comments about your contribution (shown to other teammates).',
        questionDescription: '',

        questionType: FeedbackQuestionType.TEXT,
        questionDetails: {
          recommendedLength: 0,
        },

        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.SELF,

        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
        customNumberOfEntitiesToGiveFeedbackTo: 1,

        showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
          FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
          FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
          FeedbackVisibilityType.INSTRUCTORS],
      },
      {
        feedbackQuestionId: '',
        questionNumber: 3,
        questionBrief: 'Your comments about this teammate (confidential and only shown to instructor).',
        questionDescription: '',

        questionType: FeedbackQuestionType.TEXT,
        questionDetails: {
          recommendedLength: 0,
        },

        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
        customNumberOfEntitiesToGiveFeedbackTo: 1,

        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
      },
      {
        feedbackQuestionId: '',
        questionNumber: 4,
        questionBrief: 'Comments about team dynamics (confidential and only shown to instructor).',
        questionDescription: '',

        questionType: FeedbackQuestionType.TEXT,
        questionDetails: {
          recommendedLength: 0,
        },

        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.OWN_TEAM,

        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
        customNumberOfEntitiesToGiveFeedbackTo: 1,

        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
      },
      {
        feedbackQuestionId: '',
        questionNumber: 5,
        questionBrief: 'Your feedback to this teammate (shown anonymously to the teammate).',
        questionDescription: '',

        questionType: FeedbackQuestionType.TEXT,
        questionDetails: {
          recommendedLength: 0,
        },

        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

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
