import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType, FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../../types/api-output';
import {
    DEFAULT_MCQ_QUESTION_DETAILS,
    DEFAULT_NUMSCALE_QUESTION_DETAILS,
    DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
    DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS,
    DEFAULT_TEXT_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
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
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    feedbackQuestionId: '',
    isEditable: false,
    isQuestionHasResponses: false,
    isSaving: false,
    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: DEFAULT_TEXT_QUESTION_DETAILS(),

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleNumericalScaleEditFormModel: QuestionEditFormModel = {
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    feedbackQuestionId: '',
    isEditable: false,
    isQuestionHasResponses: false,
    isSaving: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',

    questionType: FeedbackQuestionType.NUMSCALE,
    questionDetails: DEFAULT_NUMSCALE_QUESTION_DETAILS(),

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleRankRecipientQuestionModel: QuestionEditFormModel = {
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    feedbackQuestionId: '',
    isEditable: false,
    isQuestionHasResponses: false,
    isSaving: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',

    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    questionDetails: DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS(),

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleRankOptionQuestionModel: QuestionEditFormModel = {
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    feedbackQuestionId: '',
    isEditable: false,
    isQuestionHasResponses: false,
    isSaving: false,

    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',

    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionDetails: DEFAULT_RANK_OPTIONS_QUESTION_DETAILS(),

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
  };

  readonly exampleMCQQuestionWithoutWeightsModel: QuestionEditFormModel = {
    questionDetails: {
      ...DEFAULT_MCQ_QUESTION_DETAILS(),
      numOfMcqChoices: 3,
      mcqChoices: ['I did great!', 'I performed satisfactorily.', 'I did not contribute as much as I wanted to.'],
      hasAssignedWeights: false,
      mcqWeights: [],
    } as FeedbackMcqQuestionDetails,
    questionDescription: '',
    questionType: FeedbackQuestionType.MCQ,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    feedbackQuestionId: '',
    questionBrief: 'How much did you think you contributed?',
    isEditable: false,
    isQuestionHasResponses: false,
    isSaving: false,
    questionNumber: 1,
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  readonly exampleMCQQuestionWithWeightsModel: QuestionEditFormModel = {
    questionDetails: {
      ...DEFAULT_MCQ_QUESTION_DETAILS(),
      numOfMcqChoices: 3,
      mcqChoices: ['I did great!', 'I performed satisfactorily.', 'I did not contribute as much as I wanted to.'],
      hasAssignedWeights: true,
      mcqWeights: [1, 3, 5],
    } as FeedbackMcqQuestionDetails,
    questionDescription: '',
    questionType: FeedbackQuestionType.MCQ,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
    feedbackQuestionId: '',
    questionBrief: 'How much did you think you contributed?',
    isEditable: false,
    isQuestionHasResponses: false,
    isSaving: false,
    questionNumber: 1,
    customNumberOfEntitiesToGiveFeedbackTo: 0,
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
