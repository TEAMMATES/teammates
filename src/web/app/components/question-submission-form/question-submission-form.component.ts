import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output, ViewChild, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import {
  FeedbackRecipientLabelType,
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
  ResponseSubmissionStatus,
} from './question-submission-form-model';
import { NoRecipientsWarningComponent } from './no-recipients-warning.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  FeedbackQuestionType,
  FeedbackResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
} from '../../../types/api-output';
import { QuestionDetailsTypeChecker } from '../../../types/question-details-impl/question-details-caster';
import { ResponseDetailsTypeChecker } from '../../../types/response-details-impl/response-details-caster';
import { VisibilityControl } from '../../../types/visibility-control';
import { SessionView } from '../../pages-session/session-submission-page/session-view.enum';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { createNewCommentRowModel } from '../comment-box/comment-row-model-mapper';
import type { CommentRowModel, GiverCommentRowModel, NewCommentRowModel } from '../comment-box/comment.model';
import { CommentRowComponent } from '../comment-box/comment-row/comment-row.component';
import { CommentRowMode } from '../comment-box/comment-row/comment-row.mode';
import { PanelChevronComponent } from '../panel-chevron/panel-chevron.component';
import { ConstsumRecipientsQuestionConstraintComponent } from '../question-types/question-constraint/constsum-recipients-question-constraint.component';
import { ContributionQuestionConstraintComponent } from '../question-types/question-constraint/contribution-question-constraint.component';
import { MsqQuestionConstraintComponent } from '../question-types/question-constraint/msq-question-constraint.component';
import { NumScaleQuestionConstraintComponent } from '../question-types/question-constraint/num-scale-question-constraint.component';
import { RankRecipientsQuestionConstraintComponent } from '../question-types/question-constraint/rank-recipients-question-constraint.component';
import { TextQuestionConstraintComponent } from '../question-types/question-constraint/text-question-constraint.component';
import { ConstsumOptionsQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/constsum-options-question-edit-answer-form.component';
import { ConstsumRecipientsQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/constsum-recipients-question-edit-answer-form.component';
import { ContributionQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/contribution-question-edit-answer-form.component';
import { McqQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/mcq-question-edit-answer-form.component';
import { MsqQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/msq-question-edit-answer-form.component';
import { NumScaleQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/num-scale-question-edit-answer-form.component';
import { RankOptionsQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/rank-options-question-edit-answer-form.component';
import { RankRecipientsQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/rank-recipients-question-edit-answer-form.component';
import { RubricQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/rubric-question-edit-answer-form.component';
import { TextQuestionEditAnswerFormComponent } from '../question-types/question-edit-answer-form/text-question-edit-answer-form.component';
import { ConstsumOptionsQuestionInstructionComponent } from '../question-types/question-instruction/constsum-options-question-instruction.component';
import { ConstsumRecipientsQuestionInstructionComponent } from '../question-types/question-instruction/constsum-recipients-question-instruction.component';
import { ContributionQuestionInstructionComponent } from '../question-types/question-instruction/contribution-question-instruction.component';
import { NumScaleQuestionInstructionComponent } from '../question-types/question-instruction/num-scale-question-instruction.component';
import { RankOptionsQuestionInstructionComponent } from '../question-types/question-instruction/rank-options-question-instruction.component';
import { RankRecipientsQuestionInstructionComponent } from '../question-types/question-instruction/rank-recipients-question-instruction.component';
import { TextQuestionInstructionComponent } from '../question-types/question-instruction/text-question-instruction.component';
import { SafeHtmlPipe } from '../teammates-common/safe-html.pipe';
import { VisibilityCapabilityPipe } from '../visibility-messages/visibility-capability.pipe';
import { VisibilityEntityNamePipe } from '../visibility-messages/visibility-entity-name.pipe';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { ComboboxOption, SearchableComboboxComponent } from '../searchable-combobox/searchable-combobox.component';

/**
 * The question submission form for a question.
 */
@Component({
  selector: 'tm-question-submission-form',
  templateUrl: './question-submission-form.component.html',
  styleUrls: ['./question-submission-form.component.scss'],
  imports: [
    NgClass,
    NgbCollapse,
    PanelChevronComponent,
    ContributionQuestionInstructionComponent,
    TextQuestionInstructionComponent,
    NumScaleQuestionInstructionComponent,
    TextQuestionConstraintComponent,
    NumScaleQuestionConstraintComponent,
    RankOptionsQuestionInstructionComponent,
    MsqQuestionConstraintComponent,
    RankRecipientsQuestionInstructionComponent,
    ConstsumOptionsQuestionInstructionComponent,
    ConstsumRecipientsQuestionInstructionComponent,
    NgbTooltip,
    FormsModule,
    ContributionQuestionEditAnswerFormComponent,
    TextQuestionEditAnswerFormComponent,
    RankOptionsQuestionEditAnswerFormComponent,
    RankRecipientsQuestionEditAnswerFormComponent,
    NumScaleQuestionEditAnswerFormComponent,
    McqQuestionEditAnswerFormComponent,
    MsqQuestionEditAnswerFormComponent,
    RubricQuestionEditAnswerFormComponent,
    ConstsumOptionsQuestionEditAnswerFormComponent,
    ConstsumRecipientsQuestionEditAnswerFormComponent,
    CommentRowComponent,
    ContributionQuestionConstraintComponent,
    RankRecipientsQuestionConstraintComponent,
    ConstsumRecipientsQuestionConstraintComponent,
    AjaxLoadingComponent,
    SafeHtmlPipe,
    VisibilityEntityNamePipe,
    VisibilityCapabilityPipe,
    RecipientTypeNamePipe,
    SearchableComboboxComponent,
    NoRecipientsWarningComponent,
  ],
})
export class QuestionSubmissionFormComponent {
  private readonly feedbackQuestionsService = inject(FeedbackQuestionsService);
  private readonly feedbackResponseService = inject(FeedbackResponsesService);

  readonly QuestionDetailsTypeChecker: typeof QuestionDetailsTypeChecker;
  readonly ResponseDetailsTypeChecker: typeof ResponseDetailsTypeChecker;

  // enum
  QuestionSubmissionFormMode!: typeof QuestionSubmissionFormMode;
  QuestionGiverType!: typeof QuestionGiverType;
  QuestionRecipientType!: typeof QuestionRecipientType;
  CommentRowMode!: typeof CommentRowMode;
  FeedbackVisibilityType!: typeof FeedbackVisibilityType;
  isMCQDropDownEnabled = false;
  ResponseSubmissionStatus!: typeof ResponseSubmissionStatus;

  readonly feedbackVisibilityTypes = Object.values(FeedbackVisibilityType);

  private _formMode: QuestionSubmissionFormMode = QuestionSubmissionFormMode.FIXED_RECIPIENT;

  @Input()
  set formMode(value: QuestionSubmissionFormMode) {
    this._formMode = value;
    this.sortRecipientsIfReady();
  }

  get formMode(): QuestionSubmissionFormMode {
    return this._formMode;
  }

  @Input()
  isFormsDisabled = false;

  @Input()
  isSubmissionDisabled = false;

  @Input()
  isSavingResponses = false;

  readonly isTabExpanded = signal(true);

  @Input()
  set formModel(model: QuestionSubmissionFormModel) {
    this.model = model;
    this.visibilityStateMachine = this.feedbackQuestionsService.getNewVisibilityStateMachine(
      model.giverType,
      model.recipientType,
    );
    const visibilitySetting: { [TKey in VisibilityControl]: FeedbackVisibilityType[] } = {
      SHOW_RESPONSE: model.showResponsesTo,
      SHOW_GIVER_NAME: model.showGiverNameTo,
      SHOW_RECIPIENT_NAME: model.showRecipientNameTo,
    };
    this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);
    this.recipientLabelType = this.getSelectionLabelType(model.recipientType);

    this.sortRecipientsIfReady();
  }

  @Input()
  isQuestionCountOne = false;

  allSessionViews!: typeof SessionView;

  @Input()
  currentSelectedSessionView: SessionView = SessionView.DEFAULT;

  @Input()
  recipientId = '';

  @Output()
  formModelChange: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  @Output()
  responsesSave: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  @ViewChild(ContributionQuestionConstraintComponent)
  private contributionQuestionConstraint!: ContributionQuestionConstraintComponent;

  @ViewChild(RankRecipientsQuestionConstraintComponent)
  private rankRecipientsQuestionConstraint!: RankRecipientsQuestionConstraintComponent;

  @ViewChild(ConstsumRecipientsQuestionConstraintComponent)
  private constsumRecipientQuesitonConstraint!: ConstsumRecipientsQuestionConstraintComponent;

  model: QuestionSubmissionFormModel = {
    feedbackQuestionId: '',

    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [],
    recipientSubmissionForms: [],

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionText: '',
      questionType: FeedbackQuestionType.TEXT,
    },

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 0,

    showGiverNameTo: [],
    showRecipientNameTo: [],
    showResponsesTo: [],
  };

  recipientLabelType: FeedbackRecipientLabelType = FeedbackRecipientLabelType.INCLUDE_NAME;

  @Output()
  deleteCommentEvent: EventEmitter<number> = new EventEmitter();

  visibilityStateMachine: VisibilityStateMachine;

  constructor() {
    this.QuestionDetailsTypeChecker = QuestionDetailsTypeChecker;
    this.ResponseDetailsTypeChecker = ResponseDetailsTypeChecker;
    this.QuestionSubmissionFormMode = QuestionSubmissionFormMode;
    this.QuestionGiverType = QuestionGiverType;
    this.QuestionRecipientType = QuestionRecipientType;
    this.CommentRowMode = CommentRowMode;
    this.FeedbackVisibilityType = FeedbackVisibilityType;
    this.ResponseSubmissionStatus = ResponseSubmissionStatus;
    this.allSessionViews = SessionView;
    this.visibilityStateMachine = this.feedbackQuestionsService.getNewVisibilityStateMachine(
      this.model.giverType,
      this.model.recipientType,
    );
  }

  get hasSectionTeam(): boolean {
    if (this.formMode === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT) {
      switch (this.recipientLabelType) {
        case FeedbackRecipientLabelType.INCLUDE_SECTION:
        case FeedbackRecipientLabelType.INCLUDE_TEAM:
          return true;
        default:
          return false;
      }
    }
    return false;
  }

  toggleQuestionTab(): void {
    this.isTabExpanded.update((isExpanded) => !isExpanded);
  }

  shouldShowSavedState(): boolean {
    return this.isSaved(this.recipientId);
  }

  getQuestionHeaderClass(): string {
    return this.shouldShowSavedState() ? 'bg-success' : 'bg-primary';
  }

  private compareByName(firstRecipient: FeedbackResponseRecipient, secondRecipient: FeedbackResponseRecipient): number {
    return firstRecipient.recipientName.localeCompare(secondRecipient.recipientName);
  }

  private compareBySection(
    firstRecipient: FeedbackResponseRecipient,
    secondRecipient: FeedbackResponseRecipient,
  ): number {
    if (firstRecipient.recipientSection && secondRecipient.recipientSection) {
      return firstRecipient.recipientSection.localeCompare(secondRecipient.recipientSection);
    }

    if (firstRecipient.recipientSection) {
      return -1;
    }

    if (secondRecipient.recipientSection) {
      return 1;
    }

    return 0;
  }

  private compareByTeam(firstRecipient: FeedbackResponseRecipient, secondRecipient: FeedbackResponseRecipient): number {
    if (firstRecipient.recipientTeam && secondRecipient.recipientTeam) {
      return firstRecipient.recipientTeam.localeCompare(secondRecipient.recipientTeam);
    }

    if (firstRecipient.recipientTeam) {
      return -1;
    }

    if (secondRecipient.recipientTeam) {
      return 1;
    }

    return 0;
  }

  private updateSubmissionFormIndexes(): void {
    const indexes: Map<string, number> = new Map();
    this.model.recipientList.forEach((recipient: FeedbackResponseRecipient, index: number) => {
      indexes.set(recipient.recipientIdentifier, index + 1);
    });

    this.model.recipientSubmissionForms.sort(
      (
        firstRecipient: FeedbackResponseRecipientSubmissionFormModel,
        secondRecipient: FeedbackResponseRecipientSubmissionFormModel,
      ) => {
        const firstRecipientIndex: number = indexes.get(firstRecipient.recipientIdentifier) ?? Number.MAX_SAFE_INTEGER;
        const secondRecipientIndex: number =
          indexes.get(secondRecipient.recipientIdentifier) ?? Number.MAX_SAFE_INTEGER;

        return firstRecipientIndex - secondRecipientIndex;
      },
    );
  }

  private sortRecipientsIfReady(): void {
    if (this.model.recipientList.length > 0) {
      this.sortRecipients();
    }
  }

  private sortRecipients(): void {
    if (this.hasSectionTeam) {
      if (this.recipientLabelType === FeedbackRecipientLabelType.INCLUDE_SECTION) {
        this.model.recipientList.sort((firstRecipient, secondRecipient) => {
          return (
            this.compareBySection(firstRecipient, secondRecipient) ||
            this.compareByTeam(firstRecipient, secondRecipient)
          );
        });
      } else if (this.recipientLabelType === FeedbackRecipientLabelType.INCLUDE_TEAM) {
        this.model.recipientList.sort(this.compareByTeam);
      } else {
        this.model.recipientList.sort(this.compareByName);
      }
    } else {
      this.model.recipientList.sort(this.compareByName);
    }
    this.updateSubmissionFormIndexes();
  }

  /**
   * Gets recipient name in {@code FIXED_RECIPIENT} mode.
   */
  getRecipientName(recipientIdentifier: string): string {
    const recipient: FeedbackResponseRecipient | undefined = this.model.recipientList.find(
      (r: FeedbackResponseRecipient) => r.recipientIdentifier === recipientIdentifier,
    );
    return recipient ? recipient.recipientName : 'Unknown';
  }

  /**
   * Checks whether the recipient is already selected in {@code FLEXIBLE_RECIPIENT} mode.
   */
  isRecipientSelected(recipient: FeedbackResponseRecipient): boolean {
    return this.model.recipientSubmissionForms.some(
      (recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) =>
        recipientSubmissionFormModel.recipientIdentifier === recipient.recipientIdentifier,
    );
  }

  /**
   * Triggers the change of the recipient submission form.
   */
  triggerRecipientSubmissionFormChange(index: number, field: string, data: unknown): void {
    if (this.isFormsDisabled) {
      return;
    }

    this.model.recipientSubmissionForms[index] = {
      ...this.model.recipientSubmissionForms[index],
      [field]: data,
    };

    if (
      this.model.recipientSubmissionForms[index].responseId ||
      !this.feedbackResponseService.isFeedbackResponseDetailsEmpty(
        this.model.questionType,
        this.model.recipientSubmissionForms[index].responseDetails,
      )
    ) {
      this.model.recipientSubmissionForms[index].status = ResponseSubmissionStatus.MODIFIED;
    } else {
      // Response details is empty and response has not been saved before
      this.model.recipientSubmissionForms[index].status = ResponseSubmissionStatus.NEW;
    }

    this.updateIsValidByQuestionConstraint();
    this.formModelChange.emit(this.model);
  }

  updateIsValidByQuestionConstraint(): void {
    let isValid: boolean;
    const questionType: string = this.model.questionType;
    if (questionType === FeedbackQuestionType.CONTRIB) {
      isValid = this.contributionQuestionConstraint.isValid;
    } else if (questionType === FeedbackQuestionType.RANK_RECIPIENTS) {
      isValid = this.rankRecipientsQuestionConstraint.isValid;
    } else if (questionType === FeedbackQuestionType.CONSTSUM_RECIPIENTS) {
      isValid = this.constsumRecipientQuesitonConstraint.isValid;
    } else {
      return;
    }

    this.updateValidity(isValid);
  }

  /**
   * Triggers deletion of a participant comment associated with the response.
   */
  triggerDeleteCommentEvent(index: number): void {
    this.deleteCommentEvent.emit(index);
  }

  /**
   * Add new participant comment to response with index.
   */
  addNewParticipantCommentToResponse(index: number): void {
    const newComment: NewCommentRowModel = createNewCommentRowModel(this.model.showResponsesTo, true);
    this.triggerRecipientSubmissionFormChange(index, 'commentByGiver', newComment);
  }

  /**
   * Cancel adding new participant comment.
   */
  cancelAddingNewParticipantComment(index: number): void {
    this.triggerRecipientSubmissionFormChange(index, 'commentByGiver', undefined);
  }

  /**
   * Discard changes to an existing participant comment.
   */
  discardEditedParticipantComment(index: number): void {
    const comment: CommentRowModel | undefined = this.model.recipientSubmissionForms[index].commentByGiver;
    if (comment?.commentType !== 'giver') {
      return;
    }

    this.triggerRecipientSubmissionFormChange(index, 'commentByGiver', {
      ...comment,
      commentEditFormModel: structuredClone(comment.originalCommentFormModel),
      isEditing: false,
    });
  }

  hasExistingGiverComment(comment: CommentRowModel | undefined): comment is GiverCommentRowModel {
    return comment?.commentType === 'giver';
  }

  /**
   * Checks whether the response is empty or not.
   */
  isFeedbackResponseDetailsEmpty(responseDetails: FeedbackResponseDetails): boolean {
    return this.feedbackResponseService.isFeedbackResponseDetailsEmpty(this.model.questionType, responseDetails);
  }

  /**
   * Updates validity of all responses in a question.
   */
  updateValidity(isValid: boolean): void {
    if (this.model.recipientSubmissionForms.length === 0) {
      return;
    }

    for (const recipientSubmissionForm of this.model.recipientSubmissionForms) {
      recipientSubmissionForm.isValid = isValid;
    }
    this.formModelChange.emit(this.model);
  }

  /**
   * Triggers saving of responses for the specific question.
   */
  saveFeedbackResponses(): void {
    this.responsesSave.emit(this.model);
  }

  getSelectionLabelType(recipientType: QuestionRecipientType): FeedbackRecipientLabelType {
    switch (recipientType) {
      case QuestionRecipientType.STUDENTS:
      case QuestionRecipientType.STUDENTS_EXCLUDING_SELF:
        return FeedbackRecipientLabelType.INCLUDE_SECTION;
      case QuestionRecipientType.STUDENTS_IN_SAME_SECTION:
        return FeedbackRecipientLabelType.INCLUDE_TEAM;
      default:
        return FeedbackRecipientLabelType.INCLUDE_NAME;
    }
  }

  getSelectionOptionLabel(recipient: FeedbackResponseRecipient): string {
    if (!this.hasSectionTeam) {
      return recipient.recipientName;
    }

    if (recipient.recipientSection && recipient.recipientTeam) {
      return `${recipient.recipientName} (${recipient.recipientTeam} / ${recipient.recipientSection})`;
    }

    if (recipient.recipientSection) {
      return `${recipient.recipientName} (${recipient.recipientSection})`;
    }

    if (recipient.recipientTeam) {
      return `${recipient.recipientName} (${recipient.recipientTeam})`;
    }

    return recipient.recipientName;
  }

  getRecipientComboboxOptions(
    recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel,
  ): ComboboxOption<string, FeedbackResponseRecipient>[] {
    return this.model.recipientList
      .filter(
        (recipient: FeedbackResponseRecipient) =>
          !this.isRecipientSelected(recipient) ||
          recipientSubmissionFormModel.recipientIdentifier === recipient.recipientIdentifier,
      )
      .map((recipient: FeedbackResponseRecipient) => ({
        value: recipient.recipientIdentifier,
        label: this.getSelectionOptionLabel(recipient),
        keywords: [recipient.recipientName, recipient.recipientSection ?? '', recipient.recipientTeam ?? ''],
        data: recipient,
      }));
  }

  /**
   * Triggers adding a col-12 if MCQ Dropdown is enabled.
   */
  refreshCssForDropdownMCQ(add: boolean): void {
    this.isMCQDropDownEnabled = add;
  }

  /**
   * Checks whether the response of this question has been saved.
   *
   * For questions with recipient specific responses, it checks whether the response for the recipient has been saved.
   */
  isSaved(recipientId?: string): boolean {
    const relevantForms = recipientId
      ? this.model.recipientSubmissionForms.filter((form) => form.recipientIdentifier === recipientId)
      : this.model.recipientSubmissionForms;

    if (relevantForms.length === 0) {
      return false;
    }

    return relevantForms.some((form) => form.status === ResponseSubmissionStatus.SAVED);
  }
}
