import {
  Component, DoCheck, ElementRef, EventEmitter, Input, Output, ViewChild, ViewChildren, QueryList,
} from '@angular/core';
import { Observable, OperatorFunction, Subject, merge } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map } from 'rxjs/operators';
import {
  FeedbackRecipientLabelType,
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from './question-submission-form-model';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  FeedbackConstantSumResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRankOptionsResponseDetails,
  FeedbackResponseDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from '../../../types/feedback-response-details';
import { VisibilityControl } from '../../../types/visibility-control';
import { SessionView } from '../../pages-session/session-submission-page/session-submission-page.component';
import { CommentRowModel } from '../comment-box/comment-row/comment-row.component';
import { CommentRowMode } from '../comment-box/comment-row/comment-row.mode';
import { ConstsumRecipientsQuestionConstraintComponent }
  from '../question-types/question-constraint/constsum-recipients-question-constraint.component';
import { ContributionQuestionConstraintComponent }
  from '../question-types/question-constraint/contribution-question-constraint.component';
import { RankRecipientsQuestionConstraintComponent }
  from '../question-types/question-constraint/rank-recipients-question-constraint.component';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * The question submission form for a question.
 */
@Component({
  selector: 'tm-question-submission-form',
  templateUrl: './question-submission-form.component.html',
  styleUrls: ['./question-submission-form.component.scss'],
  animations: [collapseAnim],
})
export class QuestionSubmissionFormComponent implements DoCheck {

  // enum
  QuestionSubmissionFormMode: typeof QuestionSubmissionFormMode = QuestionSubmissionFormMode;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  isMCQDropDownEnabled: boolean = false;
  isSaved: boolean = false;
  hasResponseChanged: boolean = false;

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
    this.recipientLabelType = this.getSelectionLabelType(model.recipientType);

    // Initialise the "hasResponseChanged" variable and the "isTabExpandedForRecipients" variable
    // for a recipient when the recipients of the questions is not loaded.
    this.model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
      if (!this.model.hasResponseChangedForRecipients.has(recipient.recipientIdentifier)) {
        this.model.hasResponseChangedForRecipients.set(recipient.recipientIdentifier, false);
      }

      this.model.isTabExpandedForRecipients.set(recipient.recipientIdentifier, true);
    });
    this.hasResponseChanged = Array.from(this.model.hasResponseChangedForRecipients.values()).some((value) => value);
  }

  @Input()
  isQuestionCountOne: boolean = false;

  @Input()
  isSubmitAllClicked: boolean = false;

  allSessionViews = SessionView;

  @Input()
  currentSelectedSessionView: SessionView = SessionView.DEFAULT;

  @Input()
  recipientId: string = '';

  @Output()
  isSubmitAllClickedChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  formModelChange: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  @Output()
  responsesSave: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  @Output()
  autoSave: EventEmitter<{ id: string, model: QuestionSubmissionFormModel }> = new EventEmitter();

  @Output()
  resetFeedback: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter<QuestionSubmissionFormModel>();

  @ViewChild(ContributionQuestionConstraintComponent)
  private contributionQuestionConstraint!: ContributionQuestionConstraintComponent;

  @ViewChild(RankRecipientsQuestionConstraintComponent)
  private rankRecipientsQuestionConstraint!: RankRecipientsQuestionConstraintComponent;

  @ViewChild(ConstsumRecipientsQuestionConstraintComponent)
  private constsumRecipientQuesitonConstraint!: ConstsumRecipientsQuestionConstraintComponent;

  @ViewChildren('recipientInput')
  private recipientInputs!: QueryList<ElementRef<HTMLInputElement>>;

  model: QuestionSubmissionFormModel = {
    isLoading: false,
    isLoaded: false,
    isTabExpanded: true,
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

    hasResponseChangedForRecipients: new Map<string, boolean>(),
    isTabExpandedForRecipients: new Map<string, boolean>(),
  };

  recipientLabelType: FeedbackRecipientLabelType = FeedbackRecipientLabelType.INCLUDE_NAME;
  isSectionTeamShown: boolean = false;

  @Output()
  deleteCommentEvent: EventEmitter<number> = new EventEmitter();

  visibilityStateMachine: VisibilityStateMachine;
  isEveryRecipientSorted: boolean = false;

  autosaveTimeout: any;

  constructor(private feedbackQuestionsService: FeedbackQuestionsService,
    private feedbackResponseService: FeedbackResponsesService) {
    this.visibilityStateMachine =
      this.feedbackQuestionsService.getNewVisibilityStateMachine(
        this.model.giverType, this.model.recipientType);
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

  ngDoCheck(): void {
    if (this.model.isLoaded && !this.isEveryRecipientSorted) {
      this.sortRecipientsByName();
    }

    if (this.model.recipientSubmissionForms.some(
      (response) => response.responseId.length > 0) && !this.isSaved) {
      this.isSaved = true;
    }

    if (this.hasResponseChanged) {
      this.isSaved = false;
    }

    if (this.isSubmitAllClicked) {
      if (this.model.recipientSubmissionForms.some((response) => response.responseId.length > 0)) {
        this.isSaved = true;
      } else if (this.model.recipientSubmissionForms.every((form) => form.responseId.length === 0)) {
        this.isSaved = false;
      }

      this.model.hasResponseChangedForRecipients.forEach((_hasResponseChanged: boolean, recipientId: string) => {
        this.model.hasResponseChangedForRecipients.set(recipientId, false);
      });
    }

    this.model.hasResponseChangedForRecipients.forEach((hasResponseChanged: boolean) => {
      if (hasResponseChanged) {
        this.isSaved = false;
      }
    });
  }

  resetForm(): void {
    this.resetFeedback.emit(this.model);
    this.isSaved = true;
    this.hasResponseChanged = false;
    clearTimeout(this.autosaveTimeout);

    // Force clear input fields after model reset
    setTimeout(() => {
      this.clearRecipientInputsIfEmpty();
    }, 0);
  }

  /**
   * Clears recipient input fields if their identifiers are empty.
   * This ensures the display is properly cleared after reset.
   */
  private clearRecipientInputsIfEmpty(): void {
    if (!this.recipientInputs) {
      return;
    }

    this.recipientInputs.forEach((inputRef: ElementRef<HTMLInputElement>, index: number) => {
      const identifier = this.model.recipientSubmissionForms[index]?.recipientIdentifier;
      if (!identifier || identifier.trim() === '') {
        // Manually clear the input value if identifier is empty
        inputRef.nativeElement.value = '';
      }
    });
  }

  toggleQuestionTab(): void {
    if (this.currentSelectedSessionView === this.allSessionViews.DEFAULT) {
      this.model.isTabExpanded = !this.model.isTabExpanded;
    } else {
      this.model.isTabExpandedForRecipients
          .set(this.recipientId, !(this.model.isTabExpandedForRecipients.get(this.recipientId)!));
    }
    this.formModelChange.emit(this.model);
  }

  shouldTabExpand(): boolean {
    if (this.currentSelectedSessionView === this.allSessionViews.DEFAULT) {
      return this.model.isTabExpanded;
    }

    if (this.model.isTabExpandedForRecipients.get(this.recipientId) === undefined) {
      this.model.isTabExpandedForRecipients.set(this.recipientId, true);
    }
    return this.model.isTabExpandedForRecipients.get(this.recipientId)!;
  }

  private compareByName(firstRecipient: FeedbackResponseRecipient,
    secondRecipient: FeedbackResponseRecipient): number {
    return firstRecipient.recipientName.localeCompare(secondRecipient.recipientName);
  }

  private compareBySection(firstRecipient: FeedbackResponseRecipient,
    secondRecipient: FeedbackResponseRecipient): number {

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

  private compareByTeam(firstRecipient: FeedbackResponseRecipient,
    secondRecipient: FeedbackResponseRecipient): number {

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

    this.model.recipientSubmissionForms.sort((firstRecipient: FeedbackResponseRecipientSubmissionFormModel,
      secondRecipient: FeedbackResponseRecipientSubmissionFormModel) => {
      const firstRecipientIndex: number = indexes.get(firstRecipient.recipientIdentifier) || Number.MAX_SAFE_INTEGER;
      const secondRecipientIndex: number = indexes.get(secondRecipient.recipientIdentifier) || Number.MAX_SAFE_INTEGER;

      return firstRecipientIndex - secondRecipientIndex;
    });
    this.isEveryRecipientSorted = true;
  }

  sortRecipientsByName(): void {
    this.model.recipientList.sort(this.compareByName);
    this.updateSubmissionFormIndexes();
  }

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
    if (!this.isFormsDisabled) {
      this.isSubmitAllClickedChange.emit(false);
      this.model.hasResponseChangedForRecipients.set(this.model.recipientList[index].recipientIdentifier, true);

      this.model.recipientSubmissionForms[index] =
      {
        ...this.model.recipientSubmissionForms[index],
        [field]: data,
      };

      this.updateIsValidByQuestionConstraint();
      this.formModelChange.emit(this.model);

      this.autoSave.emit({ id: this.model.feedbackQuestionId, model: this.model });
      clearTimeout(this.autosaveTimeout);
      this.autosaveTimeout = setTimeout(() => {
        this.hasResponseChanged = true;
      }, 100); // 0.1 second to prevent people from trying to immediately reset before autosave kicks in
    }
  }

  updateIsValidByQuestionConstraint(): void {
    let isValid: boolean = false;
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

    for (const recipientSubmissionForm of this.model.recipientSubmissionForms) {
      recipientSubmissionForm.isValid = isValid;
    }

    this.formModelChange.emit(this.model);
  }

  /**
   * Triggers saving of responses for the specific question.
   */
  saveFeedbackResponses(): void {
    clearTimeout(this.autosaveTimeout);
    this.isSaved = true;
    this.hasResponseChanged = false;
    this.model.hasResponseChangedForRecipients.forEach(
        (_hasResponseChangedForRecipient: boolean, recipientId: string) => {
        this.model.hasResponseChangedForRecipients.set(recipientId, false);
    });
    this.responsesSave.emit(this.model);
  }

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

  getSelectionOptionLabel(recipient: FeedbackResponseRecipient): string {
    if (!this.isSectionTeamShown) {
      return recipient.recipientName;
    }

    if (recipient.recipientSection && recipient.recipientTeam) {
      return `${recipient.recipientSection} / ${recipient.recipientTeam} | ${recipient.recipientName}`;
    }

    if (recipient.recipientSection) {
      return `${recipient.recipientSection} | ${recipient.recipientName}`;
    }

    if (recipient.recipientTeam) {
      return `${recipient.recipientTeam} | ${recipient.recipientName}`;
    }

    return recipient.recipientName;
  }

  /**
   * Performs case-insensitive substring filtering for recipient search.
   *
   * @param query The search query entered by the user
   * @param optionText The full display text of the option
   * @returns true if query is a substring of optionText (case-insensitive), false otherwise
   */
  substringFilter(query: string, optionText: string): boolean {
    if (!query || query.trim() === '') {
      return true; // Show all options when query is empty
    }

    const normalizedQuery = query.toLowerCase().trim();
    const normalizedOption = optionText.toLowerCase();

    return normalizedOption.includes(normalizedQuery);
  }

  /**
   * Gets filtered recipient list based on search query.
   *
   * @param query The search query entered by the user
   * @param recipients The full list of recipients
   * @returns Filtered list of recipients matching the query
   */
  getFilteredRecipients(query: string, recipients: FeedbackResponseRecipient[]): FeedbackResponseRecipient[] {
    if (!query || query.trim() === '') {
      return recipients;
    }

    return recipients.filter((recipient: FeedbackResponseRecipient) => {
      const optionLabel = this.getSelectionOptionLabel(recipient);
      return this.substringFilter(query, optionLabel);
    });
  }

  focus$ = new Subject<string>();
  click$ = new Subject<string>();
  activeTypeaheadIndex: number = -1;

  // Cache for search functions to avoid recreating them on every change detection
  private searchRecipientsMap = new Map<number, OperatorFunction<string, readonly FeedbackResponseRecipient[]>>();

  /**
   * Typeahead search function factory for recipient selection.
   * Returns an Observable that performs substring filtering on recipient names.
   */
  getSearchRecipients(index: number): OperatorFunction<string, readonly FeedbackResponseRecipient[]> {
    if (!this.searchRecipientsMap.has(index)) {
      const searchFn: OperatorFunction<string, readonly FeedbackResponseRecipient[]> = (text$: Observable<string>) => {
        const debouncedText$ = text$.pipe(debounceTime(200), distinctUntilChanged());

        // Only trigger for this specific input index
        const clicksOrFocus$ = merge(this.focus$, this.click$).pipe(
          map((term) => ({ term, activeIndex: this.activeTypeaheadIndex })),
          // Filter events that don't belong to this input
          // We check activeTypeaheadIndex to ensure we only respond when THIS input is the active one
          filter(({ activeIndex }) => activeIndex === index),
          map(() => ''), // Force empty string to show all options on click/focus
        );

        return merge(debouncedText$, clicksOrFocus$).pipe(
          distinctUntilChanged(),
          map((term: string) => {
            // For contribution questions, show all recipients (including already selected)
            if (this.model.questionType === FeedbackQuestionType.CONTRIB) {
              return this.getFilteredRecipients(term, this.model.recipientList);
            }

            // For other question types, filter out already-selected recipients to prevent duplicates
            // But allow the currently selected recipient in the active input to remain visible
            const availableRecipients = this.model.recipientList.filter((recipient: FeedbackResponseRecipient) => {
              const isSelected = this.isRecipientSelected(recipient);
              if (!isSelected) {
                return true;
              }
              // If selected, check if it is the one currently selected in this specific input
              // Use the captured index to be safe and explicit
              const currentForm = this.model.recipientSubmissionForms[index];
              return currentForm && currentForm.recipientIdentifier === recipient.recipientIdentifier;
            });

            // Return all filtered results
            return this.getFilteredRecipients(term, availableRecipients);
          }),
        );
      };
      this.searchRecipientsMap.set(index, searchFn);
    }
    return this.searchRecipientsMap.get(index)!;
  }

  /**
   * Formats the recipient for display in the typeahead dropdown.
   */
  formatRecipientResult = (recipient: FeedbackResponseRecipient): string => {
    return this.getSelectionOptionLabel(recipient);
  };

  /**
   * Formats the recipient for display in the input field after selection.
   */
  formatRecipientInput = (recipient: FeedbackResponseRecipient | null): string => {
    return recipient ? this.getSelectionOptionLabel(recipient) : '';
  };

  /**
   * Handles recipient selection from the typeahead.
   */
  onRecipientSelect(event: { item: FeedbackResponseRecipient }, index: number): void {
    const selectedRecipient: FeedbackResponseRecipient = event.item;
    this.triggerRecipientSubmissionFormChange(index, 'recipientIdentifier', selectedRecipient.recipientIdentifier);
  }

  /**
   * Handles recipient change from ngModelChange event.
   */
  onRecipientChange(recipient: FeedbackResponseRecipient | null | string, index: number): void {
    if (recipient && typeof recipient === 'object' && recipient.recipientIdentifier) {
      this.triggerRecipientSubmissionFormChange(index, 'recipientIdentifier', recipient.recipientIdentifier);
    } else if (!recipient || (typeof recipient === 'string' && recipient.trim() === '')) {
      // Only clear identifier when input is actually empty (not when user is typing)
      this.triggerRecipientSubmissionFormChange(index, 'recipientIdentifier', '');
    }
    // Note: If user types random text (string with content), we don't update the model
    // This allows the input to show the typed text temporarily until a valid selection is made or field is cleared
  }

  /**
   * Gets recipient object by identifier for display.
   * Returns null if identifier is empty or undefined (no selection yet).
   */
  getRecipientByIdentifier(identifier: string): FeedbackResponseRecipient | null {
    if (!identifier || identifier.trim() === '') {
      return null;
    }
    return this.model.recipientList.find(
      (r: FeedbackResponseRecipient) => r.recipientIdentifier === identifier,
    ) || null;
  }

  toggleSectionTeam(event: Event): void {
    const checkbox: HTMLInputElement = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.isSectionTeamShown = true;
      this.sortRecipientsBySectionTeam();
    } else {
      this.isSectionTeamShown = false;
      this.sortRecipientsByName();
    }
  }

  /**
   * Triggers adding a col-12 if MCQ Dropdown is enabled.
   */
  refreshCssForDropdownMCQ(add: boolean): void {
    this.isMCQDropDownEnabled = add;
  }

  /**
   * Checks whether the response of this question has been saved for this recipient.
   */
  isSavedForRecipient(recipientId: string): boolean {
    switch (this.model.questionType) {
      case FeedbackQuestionType.TEXT:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
              result || (form.recipientIdentifier === recipientId
                  && !((form.responseDetails as FeedbackTextResponseDetails).answer === ''
                      || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))),
            false);
      case FeedbackQuestionType.MCQ:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
                result || (form.recipientIdentifier === recipientId
                  && !((form.responseDetails as FeedbackMcqResponseDetails).answer === ''
                  || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))),
            false);
      case FeedbackQuestionType.MSQ:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
                result || (form.recipientIdentifier === recipientId
                  && !((form.responseDetails as FeedbackMsqResponseDetails).answers.length === 0
                  || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))),
            false);
      case FeedbackQuestionType.NUMSCALE:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
               result || (form.recipientIdentifier === recipientId
                && !((form.responseDetails as FeedbackNumericalScaleResponseDetails)
                  .answer === NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED
                || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))),
            false);
      case FeedbackQuestionType.CONSTSUM_OPTIONS:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
                result || (form.recipientIdentifier === recipientId
                  && !((form.responseDetails as FeedbackConstantSumResponseDetails)
                    .answers.length === 0
                  || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))),
            false);
      case FeedbackQuestionType.RUBRIC:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
                result || (form.recipientIdentifier === recipientId
                  && !((form.responseDetails as FeedbackRubricResponseDetails)
                    .answer.length === 0
                  || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))), false);
      case FeedbackQuestionType.RANK_OPTIONS:
        return this.model.recipientSubmissionForms.reduce(
            (result: boolean, form: FeedbackResponseRecipientSubmissionFormModel) =>
                result || (form.recipientIdentifier === recipientId
                  && !((form.responseDetails as FeedbackRankOptionsResponseDetails).answers.length === 0
                  || this.model.hasResponseChangedForRecipients.get(form.recipientIdentifier))), false);
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS:
      case FeedbackQuestionType.CONTRIB:
      case FeedbackQuestionType.RANK_RECIPIENTS:
        return this.isSaved;
      default:
        return false;
    }
  }
}
