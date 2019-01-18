import { FeedbackParticipantType } from '../app/feedback-participant-type';
import {
  FeedbackQuestion,
  FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../app/feedback-question';
import { FeedbackVisibilityType } from '../app/feedback-visibility';

/**
 * A template question.
 */
export interface TemplateQuestion {
  description: string;
  question: FeedbackQuestion;
}

/**
 * Template questions.
 */
export const templateQuestions: TemplateQuestion[] = [
  {
    description: 'Use <b>peer estimates</b> to determine the <b>work distribution percentage</b> '
        + 'among team members in <b>a team activity</b>',
    question: {
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
  },
  {
    description: 'Ask each student to describe something related to themselves i.e., <b>self-reflection</b>',
    question: {
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
  },
  {
    description: 'Ask each student to give <b>confidential peer feedback (qualitative)</b> '
        + 'to other <b>team members</b>',
    question: {
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
  },
  {
    description: 'Ask each student to give <b>confidential feedback (qualitative)</b> about the team behaviour',
    question: {
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
  },
  {
    description: 'Ask each student to give <b>comments about other team members, in confidence</b>',
    question: {
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
  },
];
