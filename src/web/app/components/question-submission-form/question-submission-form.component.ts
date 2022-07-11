import { Component, DoCheck, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  FeedbackParticipantType,
  FeedbackQuestionType, FeedbackResponseDetails, FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';
import { CommentRowModel } from '../comment-box/comment-row/comment-row.component';
import { CommentRowMode } from '../comment-box/comment-row/comment-row.mode';
import {
  FeedbackRecipientLabelType,
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from './question-submission-form-model';

/**
 * The question submission form for a question.
 */
@Component({
  selector: 'tm-question-submission-form',
  templateUrl: './question-submission-form.component.html',
  styleUrls: ['./question-submission-form.component.scss'],
})
export class QuestionSubmissionFormComponent implements DoCheck {

  // enum
  QuestionSubmissionFormMode: typeof QuestionSubmissionFormMode = QuestionSubmissionFormMode;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input()
  formMode: QuestionSubmissionFormMode = QuestionSubmissionFormMode.FIXED_RECIPIENT;

  @Input()
  isFormsDisabled: boolean = false;

  @Input()
  isSubmissionDisabled: boolean = false;

  @Input()
  isSavingResponses: boolean = false;

  @Input()
  set formModel(model: QuestionSubmissionFormModel) {
    this.model = model;
    this.visibilityStateMachine =
        this.feedbackQuestionsService.getNewVisibilityStateMachine(model.giverType, model.recipientType);
    const visibilitySetting: { [TKey in VisibilityControl]: FeedbackVisibilityType[] } = {
      SHOW_RESPONSE: model.showResponsesTo,
      SHOW_GIVER_NAME: model.showGiverNameTo,
      SHOW_RECIPIENT_NAME: model.showRecipientNameTo,
    };
    this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);
    this.allowedToHaveParticipantComment =
        this.feedbackQuestionsService.isAllowedToHaveParticipantComment(this.model.questionType);
    this.recipientLabelType = this.getSelectionLabelType(model.recipientType);
  }

  @Output()
  formModelChange: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  @Output()
  responsesSave: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  model: QuestionSubmissionFormModel = {
    isLoading: false,
    isLoaded: false,
    feedbackQuestionId: '',

    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [],
    recipientSubmissionForms: [],

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionText: '',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextQuestionDetails,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 0,

    showGiverNameTo: [],
    showRecipientNameTo: [],
    showResponsesTo: [],
  };

  recipientLabelType: FeedbackRecipientLabelType = FeedbackRecipientLabelType.INCLUDE_NAME;
  isRecipientListSorted: boolean = false;
  isSectionTeamShown: boolean = false;

  @Output()
  deleteCommentEvent: EventEmitter<number> = new EventEmitter();

  visibilityStateMachine: VisibilityStateMachine;
  allowedToHaveParticipantComment: boolean = false;

  constructor(private feedbackQuestionsService: FeedbackQuestionsService,
              private feedbackResponseService: FeedbackResponsesService) {
    this.visibilityStateMachine =
        this.feedbackQuestionsService.getNewVisibilityStateMachine(
            this.model.giverType, this.model.recipientType);
  }

  ngDoCheck(): void {
    if (!(this.isRecipientListSorted) && this.model.isLoaded) {
      this.isRecipientListSorted = true;
      this.sortRecipientsByName();
    }
  }

  /**
   * Compares FeedbackResponseRecipients' names using localeCompare.
   */
  private compareByName(firstRecipient: FeedbackResponseRecipient,
     secondRecipient: FeedbackResponseRecipient): number {
    return firstRecipient.recipientName.localeCompare(secondRecipient.recipientName);
  }

  /**
   * Compares FeedbackResponseRecipients' sections using localeCompare.
   */
  private compareBySection(firstRecipient: FeedbackResponseRecipient,
     secondRecipient: FeedbackResponseRecipient): number {
    return firstRecipient!.recipientSection!.localeCompare(secondRecipient.recipientSection!);
  }

  /**
   * Compares FeedbackResponseRecipients' teams using localeCompare.
   */
  private compareByTeam(firstRecipient: FeedbackResponseRecipient,
     secondRecipient: FeedbackResponseRecipient): number {
    return firstRecipient.recipientTeam!.localeCompare(secondRecipient.recipientTeam!);
  }

  /**
   * Updates the recipientSubmissionForms to correspond to the recipientList.
   */
  private updateSubmissionFormIndexes(): void {
    const indexes: Map<String, number> = new Map();
    this.model.recipientList.forEach((recipient: FeedbackResponseRecipient, index: number) => {
      indexes.set(recipient.recipientIdentifier, index);
    });

    this.model.recipientSubmissionForms.sort((firstRecipient: FeedbackResponseRecipientSubmissionFormModel,
      secondRecipient: FeedbackResponseRecipientSubmissionFormModel) => {
      const firstRecipientIndex: number = indexes.get(firstRecipient.recipientIdentifier) || 0;
      const secondRecipientIndex: number = indexes.get(secondRecipient.recipientIdentifier) || 0;

      return firstRecipientIndex - secondRecipientIndex;
    });
  }

  /**
   * Sorts recipients of feedback by their name.
   */
  public sortRecipientsByName(): void {
    this.model.recipientList.sort(this.compareByName);
    this.updateSubmissionFormIndexes();
  }

  /**
   * Sorts recipients of feedback by their section and then by their team.
   */
  private sortRecipientsBySectionTeam(): void {
    if (this.recipientLabelType === FeedbackRecipientLabelType.INCLUDE_SECTION) {
      this.model.recipientList.sort((firstRecipient, secondRecipient) => {
        return this.compareBySection(firstRecipient, secondRecipient)
         || this.compareByTeam(firstRecipient, secondRecipient);
        });

    } else if (this.recipientLabelType === FeedbackRecipientLabelType.INCLUDE_TEAM) {
      this.model.recipientList.sort(this.compareByTeam);
    }
    this.updateSubmissionFormIndexes();
  }

  /**
   * Tracks submission form for each recipient by the index in the array.
   *
   * @see https://angular.io/api/common/NgForOf#properties
   */
  trackRecipientSubmissionFormByFn(index: number): any {
    return index;
  }

  /**
   * Gets recipient name in {@code FIXED_RECIPIENT} mode.
   */
  getRecipientName(recipientIdentifier: string): string {
    const recipient: FeedbackResponseRecipient | undefined =
        this.model.recipientList.find(
            (r: FeedbackResponseRecipient) => r.recipientIdentifier === recipientIdentifier);
    return recipient ? recipient.recipientName : 'Unknown';
  }

  /**
   * Checks whether the recipient is already selected in {@code FLEXIBLE_RECIPIENT} mode.
   */
  isRecipientSelected(recipient: FeedbackResponseRecipient): boolean {
    return this.model.recipientSubmissionForms.some(
        (recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) =>
            recipientSubmissionFormModel.recipientIdentifier === recipient.recipientIdentifier);
  }

  /**
   * Triggers the change of the recipient submission form.
   */
  triggerRecipientSubmissionFormChange(index: number, field: string, data: any): void {
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] =
        this.model.recipientSubmissionForms.slice();
    recipientSubmissionForms[index] = {
      ...recipientSubmissionForms[index],
      [field]: data,
    };

    this.formModelChange.emit({
      ...this.model,
      recipientSubmissionForms,
    });
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
    this.triggerRecipientSubmissionFormChange(index, 'commentByGiver', {
      commentEditFormModel: {
        commentText: '',
      },

      isEditing: true,
    });
  }

  /**
   * Cancel adding new participant comment.
   */
  cancelAddingNewParticipantComment(index: number): void {
    this.triggerRecipientSubmissionFormChange(index, 'commentByGiver', null);
  }

  /**
   * Discards the current editing and restore the original comment.
   */
  discardEditedParticipantComment(index: number): void {
    const commentModel: CommentRowModel | undefined = this.model.recipientSubmissionForms[index].commentByGiver;
    if (!commentModel || !commentModel.originalComment) {
      return;
    }
    this.triggerRecipientSubmissionFormChange(index, 'commentByGiver',
        {
          ...commentModel,
          commentEditFormModel: {
            commentText: commentModel.originalComment.commentText,
          },
          isEditing: false,
        });
  }

  /**
   * Checks whether the response is empty or not.
   */
  isFeedbackResponseDetailsEmpty(responseDetails: FeedbackResponseDetails): boolean {
    return this.feedbackResponseService.isFeedbackResponseDetailsEmpty(
        this.model.questionType, responseDetails);
  }

  /**
   * Updates validity of all responses in a question.
   */
  updateValidity(isValid: boolean): void {
    if (this.model.recipientSubmissionForms.length === 0) { return; }
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] =
        this.model.recipientSubmissionForms.slice().map(
            (model: FeedbackResponseRecipientSubmissionFormModel) => ({ ...model, isValid }));
    this.formModelChange.emit({
      ...this.model,
      recipientSubmissionForms,
    });
  }

  /**
   * Triggers saving of responses for the specific question.
   */
  saveFeedbackResponses(): void {
    this.responsesSave.emit(this.model);
  }

  /**
   * Returns the respective {@code FeedbackRecipientLabelType}
   */
  getSelectionLabelType(recipientType: FeedbackParticipantType): FeedbackRecipientLabelType {
    switch (recipientType) {
      case FeedbackParticipantType.STUDENTS:
      case FeedbackParticipantType.STUDENTS_EXCLUDING_SELF:
        return FeedbackRecipientLabelType.INCLUDE_SECTION;
      case FeedbackParticipantType.STUDENTS_IN_SAME_SECTION:
        return FeedbackRecipientLabelType.INCLUDE_TEAM;
      default:
        return FeedbackRecipientLabelType.INCLUDE_NAME;
    }
  }

  /**
   * Returns the Selection Option label as per the recipientType.
   */
  getSelectionOptionLabel(recipient: FeedbackResponseRecipient): string {
    if (!this.isSectionTeamShown) {
      return recipient.recipientName;
    }

    switch (this.recipientLabelType) {
      case FeedbackRecipientLabelType.INCLUDE_SECTION:
        return `${recipient.recipientSection} | ${recipient.recipientTeam} | ${recipient.recipientName}`;
      case FeedbackRecipientLabelType.INCLUDE_TEAM:
        return `${recipient.recipientTeam} | ${recipient.recipientName}`;
      case FeedbackRecipientLabelType.INCLUDE_NAME:
        return recipient.recipientName;
      default:
        return 'Unkonwn';
    }
  }

  /**
   * Toggles to either show recipients' section and team or only the name.
   */
  toggleSectionTeam(event: Event): void {
    const checkbox : HTMLInputElement = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.isSectionTeamShown = true;
      this.sortRecipientsBySectionTeam();
    } else {
      this.isSectionTeamShown = false;
      this.sortRecipientsByName();
    }
  }
}
