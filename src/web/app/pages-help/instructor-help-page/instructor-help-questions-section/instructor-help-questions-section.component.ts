import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
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
    DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS,
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
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import { QuestionSubmissionFormModel,
} from '../../../components/question-submission-form/question-submission-form-model';
import { Response } from '../../../components/question-types/question-statistics/question-statistics';
import {
    QuestionTabModel,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-page.component';
import {
    InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;

  readonly exampleEssayQuestionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.TEXT,
    questionDetails: DEFAULT_TEXT_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleNumericalScaleQuestionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.NUMSCALE,
    questionDetails: DEFAULT_NUMSCALE_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleNumericalScaleResponses: Response<FeedbackNumericalScaleResponseDetails>[] = [
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

  readonly exampleNumericalScaleResponseOutput: ResponseOutput[] = [
    {
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

  readonly exampleNumericalScaleQuestionTabModel: QuestionTabModel = {
    question: this.exampleNumericalScaleQuestionModel,
    responses: this.exampleNumericalScaleResponseOutput,
    statistics: '',
    hasPopulated: true,
    isTabExpanded: true,
  };

  readonly exampleNumericalScaleQuestions: Record<string, QuestionTabModel> = {
    question: this.exampleNumericalScaleQuestionTabModel,
  };

  readonly exampleInstructorCommentTableModel: Record<string, CommentTableModel> = {
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

  readonly exampleFeedbackSession: FeedbackSession = {
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
  };

  readonly exampleDistributedPointOptionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    questionDetails: DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleDistributePointOptionResponses: Response<FeedbackConstantSumResponseDetails>[] = [
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
         answers: ['Option A'],
         questionType: FeedbackQuestionType.CONSTSUM,
       },
      }
  ];

  readonly exampleDistributePointOptionQuestionDetail: FeedbackConstantSumQuestionDetails = {
    numOfConstSumOptions: 2,
    constSumOptions: ['Option A', 'Option B'],
    distributeToRecipients: false,
    pointsPerOption: false,
    forceUnevenDistribution: false,
    distributePointsFor: FeedbackConstantSumDistributePointsType.NONE,
    points: 100,
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    questionText: '',
  };

  readonly exampleDistributedPointRecipientModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
    questionDetails: DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleTeamContributionQuestionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.CONTRIB,
    questionDetails: DEFAULT_CONTRIBUTION_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleRubricQuestionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.RUBRIC,
    questionDetails: {
      ...DEFAULT_RUBRIC_QUESTION_DETAILS(),
      numOfRubricChoices: 4,
      rubricChoices: ['Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree'],
      numOfRubricSubQuestions: 2,
      rubricSubQuestions:
        ['This student participates well in online discussions.', 'This student completes assigned tasks on time.'],
      rubricDescriptions: [
        ['Rarely or never responds.', 'Occasionally responds, but never initiates discussions.',
          'Takes part in discussions and sometimes initiates discussions.',
          'Initiates discussions frequently, and engages the team.'],
        ['Rarely or never completes tasks.', 'Often misses deadlines.', 'Occasionally misses deadlines.',
          'Tasks are always completed before the deadline.']],
    } as FeedbackRubricQuestionDetails,

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleRankRecipientQuestionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    questionDetails: DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleRankOptionQuestionModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionDetails: DEFAULT_RANK_OPTIONS_QUESTION_DETAILS(),

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleMCQQuestionWithoutWeightsModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: 'How much did you think you contributed?',
    questionDescription: '',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      ...DEFAULT_MCQ_QUESTION_DETAILS(),
      numOfMcqChoices: 3,
      mcqChoices: ['I did great!', 'I performed satisfactorily.', 'I did not contribute as much as I wanted to.'],
      hasAssignedWeights: false,
      mcqWeights: [],
    } as FeedbackMcqQuestionDetails,

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleMCQQuestionWithWeightsModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    isQuestionHasResponses: false,

    questionNumber: 1,
    questionBrief: 'How much did you think you contributed?',
    questionDescription: '',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      ...DEFAULT_MCQ_QUESTION_DETAILS(),
      numOfMcqChoices: 3,
      mcqChoices: ['I did great!', 'I performed satisfactorily.', 'I did not contribute as much as I wanted to.'],
      hasAssignedWeights: true,
      mcqWeights: [1, 3, 5],
    } as FeedbackMcqQuestionDetails,

    isEditable: false,
    isSaving: false,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    customNumberOfEntitiesToGiveFeedbackTo: 0,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleResponderRubricSubmissionFormModel: QuestionSubmissionFormModel = {
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
      },
      {
        responseId: 'response2',
        recipientIdentifier: 'bob',
        responseDetails: { questionType: FeedbackQuestionType.RUBRIC, answer: [1, 3] } as FeedbackRubricResponseDetails,
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
      numOfRubricChoices: 4,
      rubricChoices: ['Strongly Disagree', 'Disagree', 'Agree', 'Strongly Agree'],
      numOfRubricSubQuestions: 2,
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

  isEssayQuestionsCollapsed: boolean = false;
  isMCQSingleAnsCollapsed: boolean = false;
  isMCQMultipleAnsCollapsed: boolean = false;
  isNumericalScaleCollapsed: boolean = false;
  isPointsOptionsCollapsed: boolean = false;
  isPointsRecipientsCollapsed: boolean = false;
  isContributionQsCollapsed: boolean = false;
  isRubricQsCollapsed: boolean = false;
  isRankOptionsCollapsed: boolean = false;
  isRankRecipientsCollapsed: boolean = false;
  @Output() collapsePeerEvalTips: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private modalService: NgbModal,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  /**
   * Opens modal window.
   */
  openModal(modal: any): void {
    this.modalService.open(modal);
  }

  ngOnInit(): void {
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    if (target === 'tips-for-conducting-peer-eval') {
      this.collapsePeerEvalTips.emit(true);
    }
    return false;
  }
}
