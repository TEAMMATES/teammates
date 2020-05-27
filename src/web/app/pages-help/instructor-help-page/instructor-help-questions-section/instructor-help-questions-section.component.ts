import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
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
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import { QuestionSubmissionFormModel,
} from '../../../components/question-submission-form/question-submission-form-model';
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
