import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackRecipientLabelType,
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from './question-submission-form-model';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { mapReplacer, mapReviver } from '../../../test-helpers/json-helpers';
import testEventEmission from '../../../test-helpers/test-event-emitter';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackResponseComment,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED } from '../../../types/feedback-response-details';
import { SessionView } from '../../pages-session/session-submission-page/session-submission-page.component';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { CommentBoxModule } from '../comment-box/comment-box.module';
import { CommentRowModel } from '../comment-box/comment-row/comment-row.component';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { QuestionConstraintModule } from '../question-types/question-constraint/question-constraint.module';
import {
  QuestionEditAnswerFormModule,
} from '../question-types/question-edit-answer-form/question-edit-answer-form.module';
import { QuestionInstructionModule } from '../question-types/question-instruction/question-instruction.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';

const formResponse1: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-1',
  recipientIdentifier: 'hans-charlie-id',
  responseDetails: {
    answer: 5,
  } as FeedbackNumericalScaleResponseDetails,
  isValid: true,
};

const formResponse2: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-2',
  recipientIdentifier: 'harris-barry-id',
  responseDetails: {
    answer: 4,
  } as FeedbackNumericalScaleResponseDetails,
  isValid: true,
};

const formResponse3: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-3',
  recipientIdentifier: 'rogers-alan-id',
  responseDetails: {
    answer: 3,
  } as FeedbackNumericalScaleResponseDetails,
  isValid: true,
};

const formResponse4: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-4',
  recipientIdentifier: 'buck-arthur-id',
  responseDetails: {
    answer: 2,
  } as FeedbackNumericalScaleResponseDetails,
  isValid: true,
};

const testNumscaleQuestionSubmissionForm: QuestionSubmissionFormModel = {
  feedbackQuestionId: 'feedback-question-id-numscale',
  questionNumber: 1,
  questionBrief: 'numerical scale question',
  questionDescription: 'question description',
  questionType: FeedbackQuestionType.NUMSCALE,
  questionDetails: {
    minScale: 1,
    maxScale: 10,
    step: 1,
  } as FeedbackNumericalScaleQuestionDetails,
  giverType: FeedbackParticipantType.STUDENTS,
  recipientType: FeedbackParticipantType.STUDENTS,

  recipientList: [{ recipientName: 'Alan Rogers', recipientIdentifier: 'rogers-alan-id' },
    { recipientName: 'Arthur Buck', recipientIdentifier: 'buck-arthur-id' },
    { recipientName: 'Barry Harris', recipientIdentifier: 'harris-barry-id' },
    { recipientName: 'Charlie Hans', recipientIdentifier: 'hans-charlie-id' }],

  recipientSubmissionForms: [formResponse1, formResponse2, formResponse3, formResponse4],
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
  customNumberOfEntitiesToGiveFeedbackTo: 4,
  showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  isLoading: false,
  isLoaded: true,

  hasResponseChangedForRecipients: new Map<string, boolean>([
    ['rogers-alan-id', false],
    ['buck-arthur-id', false],
    ['harris-barry-id', false],
    ['hans-charlie-id', false],
  ]),

  isTabExpanded: true,
  isTabExpandedForRecipients: new Map<string, boolean>([
    ['rogers-alan-id', true],
    ['buck-arthur-id', true],
    ['harris-barry-id', true],
    ['hans-charlie-id', true],
  ]),
};

describe('QuestionSubmissionFormComponent', () => {
  let component: QuestionSubmissionFormComponent;
  let fixture: ComponentFixture<QuestionSubmissionFormComponent>;

  const getShowSectionTeamCheckBox = (): DebugElement => {
    return fixture.debugElement.query(By.css('.form-check input[type="checkbox"]'));
  };

  const feedbackResponseRecipientBuilder = createBuilder<FeedbackResponseRecipient>({
    recipientIdentifier: 'testIdentifier',
    recipientName: 'test-name',
  });

  const recipientSubmissionFormBuilder = createBuilder<FeedbackResponseRecipientSubmissionFormModel>({
    responseId: 'test-id',
    responseDetails: {
      questionType: FeedbackQuestionType.TEXT,
    },
    recipientIdentifier: 'testIdentifier',
    isValid: true,
  });

  const commentRowModelBuilder = createBuilder<CommentRowModel>({
    commentEditFormModel: {
      commentText: 'test comment text',
      isUsingCustomVisibilities: false,
      showCommentTo: [],
      showGiverNameTo: [],
    },
    isEditing: false,
  });

  const feedbackResponseCommentBuilder = createBuilder<FeedbackResponseComment>({
    commentGiver: 'comment-giver',
    commentText: 'comment-text',
    showCommentTo: [],
    showGiverNameTo: [],
    lastEditedAt: 0,
    lastEditorEmail: 'last-editor@gmail.com',
    feedbackResponseCommentId: 0,
    createdAt: 0,
    isVisibilityFollowingFeedbackQuestion: true,
  });

  const feedbackResponseTextDetailsBuilder = createBuilder<FeedbackTextResponseDetails>({
    questionType: FeedbackQuestionType.TEXT,
    answer: '',
  });

  const feedbackMcqQuestionDetailsBuilder = createBuilder<FeedbackMcqQuestionDetails>({
    questionType: FeedbackQuestionType.MCQ,
    questionText: '',
    hasAssignedWeights: false,
    mcqWeights: [],
    mcqOtherWeight: 0,
    mcqChoices: [],
    otherEnabled: false,
    questionDropdownEnabled: false,
    generateOptionsFor: FeedbackParticipantType.GIVER,
  });

  const feedbackResponseMcqDetailsBuilder = createBuilder<FeedbackMcqResponseDetails>({
    questionType: FeedbackQuestionType.MCQ,
    answer: '',
    isOther: false,
    otherFieldContent: '',
  });

  const feedbackMsqQuestionDetailsBuilder = createBuilder<FeedbackMsqQuestionDetails>({
    questionType: FeedbackQuestionType.MSQ,
    questionText: '',
    msqChoices: [],
    otherEnabled: false,
    hasAssignedWeights: false,
    msqWeights: [],
    msqOtherWeight: 0,
    generateOptionsFor: FeedbackParticipantType.GIVER,
    maxSelectableChoices: 1,
    minSelectableChoices: 0,
  });

  const feedbackMsqResponseDetailsBuilder = createBuilder<FeedbackMsqResponseDetails>({
    questionType: FeedbackQuestionType.MSQ,
    answers: [],
    isOther: false,
    otherFieldContent: '',
  });

  const feedbackNumericalScaleQuestionDetailsBuilder = createBuilder<FeedbackNumericalScaleQuestionDetails>({
    questionType: FeedbackQuestionType.NUMSCALE,
    questionText: '',
    minScale: 0,
    maxScale: 1,
    step: 0.5,
  });

  const feedbackNumericalScaleResponseDetailsBuilder = createBuilder<FeedbackNumericalScaleResponseDetails>({
    questionType: FeedbackQuestionType.NUMSCALE,
    answer: 0,
  });

  const feedbackConstantSumQuestionDetailsBuilder = createBuilder<FeedbackConstantSumQuestionDetails>({
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    questionText: '',
    constSumOptions: [],
    distributeToRecipients: false,
    pointsPerOption: false,
    forceUnevenDistribution: false,
    distributePointsFor: '',
    points: 0,
  });

  const feedbackConstantSumResponseDetailsBuilder = createBuilder<FeedbackConstantSumResponseDetails>({
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    answers: [],
  });

  const feedbackRubricQuestionDetailsBuilder = createBuilder<FeedbackRubricQuestionDetails>({
    questionType: FeedbackQuestionType.RUBRIC,
    questionText: '',
    hasAssignedWeights: false,
    rubricWeightsForEachCell: [],
    rubricChoices: [],
    rubricSubQuestions: [],
    rubricDescriptions: [],
  });

  const feedbackRubricResponseDetailsBuilder = createBuilder<FeedbackRubricResponseDetails>({
    questionType: FeedbackQuestionType.RUBRIC,
    answer: [],
  });

  const feedbackRankOptionsQuestionDetailsBuilder = createBuilder<FeedbackRankOptionsQuestionDetails>({
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionText: '',
    options: [],
    minOptionsToBeRanked: 0,
    maxOptionsToBeRanked: 1,
    areDuplicatesAllowed: false,
  });

  const feedbackRankOptionsResponseDetailsBuilder = createBuilder<FeedbackRankOptionsResponseDetails>({
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    answers: [],
  });

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionSubmissionFormComponent,
        RecipientTypeNamePipe,
      ],
      imports: [
        HttpClientTestingModule,
        TeammatesCommonModule,
        VisibilityMessagesModule,
        QuestionInstructionModule,
        QuestionConstraintModule,
        QuestionEditAnswerFormModule,
        RichTextEditorModule,
        FormsModule,
        CommentBoxModule,
        NgbModule,
        LoadingSpinnerModule,
        AjaxLoadingModule,
        BrowserAnimationsModule,
        PanelChevronModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionSubmissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set model', () => {
    const model: QuestionSubmissionFormModel = testNumscaleQuestionSubmissionForm;
    component.formModel = model;

    expect(component.model).toBe(model);
  });

  it('should arrange recipients according to alphabetical order of name after ngDoCheck (Sorted recipient list)',
   () => {
    const model: QuestionSubmissionFormModel = JSON.parse(
        JSON.stringify(testNumscaleQuestionSubmissionForm, mapReplacer), mapReviver);
    component.formModel = model;
    component.ngDoCheck();

    expect(model.recipientSubmissionForms).toEqual([formResponse3, formResponse4, formResponse2, formResponse1]);
  });

  it('should arrange recipients according to alphabetical order of name after ngDoCheck (Unsorted recipient list)',
    () => {
      const model: QuestionSubmissionFormModel = JSON.parse(
          JSON.stringify(testNumscaleQuestionSubmissionForm, mapReplacer), mapReviver);

      // Change recipient list to unsorted
      model.recipientList = [{ recipientName: 'Charlie Hans', recipientIdentifier: 'hans-charlie-id' },
      { recipientName: 'Alan Rogers', recipientIdentifier: 'rogers-alan-id' },
      { recipientName: 'Barry Harris', recipientIdentifier: 'harris-barry-id' },
      { recipientName: 'Arthur Buck', recipientIdentifier: 'buck-arthur-id' },
      ];

      component.formModel = model;
      component.ngDoCheck();

      expect(model.recipientSubmissionForms).toEqual([formResponse3, formResponse4, formResponse2, formResponse1]);
    });

  it('ngDoCheck: sets isSaved to false if hasResponseChanged is true and isSubmitAllClicked is false', () => {
    component.hasResponseChanged = true;

    component.ngDoCheck();

    expect(component.isSaved).toBeFalsy();
  });

  it('ngDoCheck: sets isSaved to true if hasResponseChanged is true and isSubmitAllClicked is true'
  + 'and some responses have responseId', () => {
    component.hasResponseChanged = true;
    component.isSubmitAllClicked = true;
    const model: QuestionSubmissionFormModel = JSON.parse(
      JSON.stringify(testNumscaleQuestionSubmissionForm, mapReplacer), mapReviver);
    component.formModel = model;
    component.ngDoCheck();

    expect(component.isSaved).toBeTruthy();
  });

  it('hasSectionTeam: should return false if QuestionSubmissionFormMode is not FLEXIBLE_RECIPIENT', () => {
    component.formMode = QuestionSubmissionFormMode.FIXED_RECIPIENT;

    fixture.detectChanges();

    expect(component.hasSectionTeam).toBeFalsy();
  });

  it('hasSectionTeam: should return false if QuestionSubmissionFormMode is FLEXIBLE_RECIPIENT'
  + 'and FeedbackRecipientLabelType is not INCLUDE_TEAM or INCLUDE_SECTION', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_NAME;

    fixture.detectChanges();
    expect(component.hasSectionTeam).toBeFalsy();
  });

  it('hasSectionTeam: should return true if QuestionSubmissionFormMode is FLEXIBLE_RECIPIENT'
  + 'and FeedbackRecipientLabelType is INCLUDE_SECTION', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_SECTION;

    fixture.detectChanges();

    expect(component.hasSectionTeam).toBeTruthy();
  });

  it('hasSectionTeam: should return true if QuestionSubmissionFormMode is FLEXIBLE_RECIPIENT'
  + 'and FeedbackRecipientLabelType is INCLUDE_TEAM', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_TEAM;

    fixture.detectChanges();
    expect(component.hasSectionTeam).toBeTruthy();
  });

  it('toggleQuestionTab: should toggle isTabExpanded if currentSelectedSessionView is DEFAULT', () => {
    component.currentSelectedSessionView = SessionView.DEFAULT;
    component.model.isTabExpanded = true;
    let emittedModel: QuestionSubmissionFormModel | undefined;
    testEventEmission(component.formModelChange, (value) => { emittedModel = value; });

    component.toggleQuestionTab();

    expect(component.model.isTabExpanded).toBeFalsy();
    expect(emittedModel).toStrictEqual(component.model);
  });

  it('toggleQuestionTab: should toggle isTabExpanded for only the recipientId in isTabExpandedForRecipients'
  + 'if currentSelectedSessionView is not DEFAULT', () => {
    component.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
    component.model.isTabExpanded = true;
    component.recipientId = 'test-id';
    const otherRecipientId = 'test-other-id';
    component.model.hasResponseChangedForRecipients.set(component.recipientId, false);
    component.model.hasResponseChangedForRecipients.set(otherRecipientId, false);

    let emittedModel: QuestionSubmissionFormModel | undefined;
    testEventEmission(component.formModelChange, (value) => { emittedModel = value; });

    component.toggleQuestionTab();

    expect(component.model.isTabExpanded).toBeTruthy();
    expect(component.model.isTabExpandedForRecipients.get(component.recipientId)).toBeTruthy();
    expect(component.model.isTabExpandedForRecipients.get(otherRecipientId)).toBeFalsy();
    expect(emittedModel).toStrictEqual(component.model);
  });

  it('shouldTabExpand: should set recipientId in isTabExpandedForRecipients to true if its undefined'
  + 'and currentSelectedSessionView is not DEFAULT', () => {
    component.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
    const recipientId = 'test-id';
    component.recipientId = recipientId;

    expect(component.shouldTabExpand()).toBeTruthy();
    expect(component.model.isTabExpandedForRecipients.get(recipientId)).toBeTruthy();
  });

  it('shouldTabExpand: should return isTabExpandedForRecipients for recipientId if'
  + 'currentSelectedSessionView is not DEFAULT', () => {
    component.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
    const recipientId = 'test-id';
    component.recipientId = recipientId;
    component.model.isTabExpandedForRecipients.set(recipientId, false);

    expect(component.shouldTabExpand()).toBeFalsy();
  });

  it('isRecipientSelected: should return true if FeedbackResponseRecipient exists in recipientSubmissionForms', () => {
    const feedbackResponseRecipientIdentifier = 'test-identifer';
    const feedbackResponseRecipient =
      feedbackResponseRecipientBuilder.recipientIdentifier(feedbackResponseRecipientIdentifier).build();
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(feedbackResponseRecipientIdentifier).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('testid').build(),
    ];

    expect(component.isRecipientSelected(feedbackResponseRecipient)).toBeTruthy();
  });

  it('isRecipientSelected: should return false if FeedbackResponseRecipient does not'
  + 'exist in recipientSubmissionForms', () => {
    const feedbackResponseRecipientIdentifier = 'test-identifer';
    const feedbackResponseRecipient =
      feedbackResponseRecipientBuilder.recipientIdentifier(feedbackResponseRecipientIdentifier).build();
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier('testid1').build(),
      recipientSubmissionFormBuilder.recipientIdentifier('testid2').build(),
    ];

    expect(component.isRecipientSelected(feedbackResponseRecipient)).toBeFalsy();
  });

  it('triggerDeleteCommentEvent: should emit the correct index to deleteCommentEvent', () => {
    let emittedIndex: number | undefined;
    testEventEmission(component.deleteCommentEvent, (index) => { emittedIndex = index; });

    component.triggerDeleteCommentEvent(5);
    expect(emittedIndex).toBe(5);
  });

  it('addNewParticipantCommentToResponse: should call triggerRecipientSubmissionFormChange'
  + 'with the correct index', () => {
    const triggerRecipientSubmissionFormChangeSpy =
      jest.spyOn(component, 'triggerRecipientSubmissionFormChange').mockReturnValue();

    component.addNewParticipantCommentToResponse(3);

    expect(triggerRecipientSubmissionFormChangeSpy).toHaveBeenCalledWith(3, 'commentByGiver', {
      commentEditFormModel: {
        commentText: '',
      },

      isEditing: true,
    });
  });

  it('cancelAddingNewParticipantComment: should call triggerRecipientSubmissionFormChange'
  + 'with the correct index', () => {
    const triggerRecipientSubmissionFormChangeSpy =
      jest.spyOn(component, 'triggerRecipientSubmissionFormChange').mockReturnValue();

    component.cancelAddingNewParticipantComment(3);

    expect(triggerRecipientSubmissionFormChangeSpy).toHaveBeenCalledWith(3, 'commentByGiver', null);
  });

  it('discardEditedParticipantComment: should not call triggerRecipientSubmissionFormChange'
  + 'if commentModel is undefined', () => {
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier('testid').build(),
    ];
    const triggerRecipientSubmissionFormChangeSpy =
      jest.spyOn(component, 'triggerRecipientSubmissionFormChange').mockReturnValue();

    component.discardEditedParticipantComment(0);

    expect(triggerRecipientSubmissionFormChangeSpy).not.toHaveBeenCalled();
  });

  it('discardEditedParticipantComment: should not call triggerRecipientSubmissionFormChange if'
  + 'originalComment in commentModel is undefined', () => {
    const recipientSubmissionForm = recipientSubmissionFormBuilder.build();
    recipientSubmissionForm.commentByGiver = commentRowModelBuilder.build();
    component.model.recipientSubmissionForms = [
      recipientSubmissionForm,
    ];

    const triggerRecipientSubmissionFormChangeSpy =
      jest.spyOn(component, 'triggerRecipientSubmissionFormChange').mockReturnValue();

    component.discardEditedParticipantComment(0);

    expect(triggerRecipientSubmissionFormChangeSpy).not.toHaveBeenCalled();
  });

  it('discardEditedParticipantComment: should call triggerRecipientSubmissionFormChange if'
  + 'originalComment in commentModel is defined', () => {
    const recipientSubmissionForm = recipientSubmissionFormBuilder.build();
    const commentModel = commentRowModelBuilder.build();
    const feedbackResponseComment = feedbackResponseCommentBuilder.build();

    recipientSubmissionForm.commentByGiver = commentModel;
    recipientSubmissionForm.commentByGiver.originalComment = feedbackResponseComment;

    component.model.recipientSubmissionForms = [
      recipientSubmissionForm,
    ];
    const triggerRecipientSubmissionFormChangeSpy =
      jest.spyOn(component, 'triggerRecipientSubmissionFormChange').mockReturnValue();

    component.discardEditedParticipantComment(0);

    expect(triggerRecipientSubmissionFormChangeSpy).toHaveBeenCalledWith(0, 'commentByGiver', {
      ...commentModel,
      commentEditFormModel: {
        commentText: commentModel.originalComment?.commentText,
      },
      isEditing: false,
    });
  });

  it('updateValidity: should not emit formModelChange if there are no recipientSubmissionForms', () => {
    component.model.recipientSubmissionForms = [];
    const formModelChangeSpy = jest.spyOn(component.formModelChange, 'emit');

    component.updateValidity(true);

    expect(formModelChangeSpy).not.toHaveBeenCalled();
  });

  it('saveFeedbackResponses: should set isSaved to true, hasResponseChanged to false'
  + 'and set model.hasResponseChangedForRecipients all to false', () => {
    component.isSaved = false;
    component.hasResponseChanged = true;
    component.model.hasResponseChangedForRecipients = new Map<string, boolean>([
      ['id1', true],
      ['id2', false],
      ['id3', true],
    ]);

    let emittedModel: QuestionSubmissionFormModel | undefined;
    testEventEmission(component.responsesSave, (value) => { emittedModel = value; });

    component.saveFeedbackResponses();

    expect(component.isSaved).toBeTruthy();
    expect(component.hasResponseChanged).toBeFalsy();

    const expectedHasResponseChangedForRecipients = new Map<string, boolean>([
      ['id1', false],
      ['id2', false],
      ['id3', false],
    ]);
    expect(component.model.hasResponseChangedForRecipients).toStrictEqual(expectedHasResponseChangedForRecipients);
    expect(emittedModel).toStrictEqual(component.model);
  });

  it('getSelectionOptionLabel: should return recipient name if isSectionTeamShown is false', () => {
    component.isSectionTeamShown = false;

    const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name');
  });

  it('getSelectionOptionLabel: should return recipientSection and recipientTeam if both are defined', () => {
    component.isSectionTeamShown = true;

    const feedbackResponseRecipient =
      feedbackResponseRecipientBuilder.recipientName('test-name').build();
    feedbackResponseRecipient.recipientSection = 'test-section';
    feedbackResponseRecipient.recipientTeam = 'test-team';

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-section / test-team | test-name');
  });

  it('getSelectionOptionLabel: should return only recipientSection if recipientTeam is undefined', () => {
    component.isSectionTeamShown = true;

    const feedbackResponseRecipient =
      feedbackResponseRecipientBuilder.recipientName('test-name').build();
    feedbackResponseRecipient.recipientSection = 'test-section';

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-section | test-name');
  });

  it('getSelectionOptionLabel: should return only recipientTeam if recipientSection is undefined', () => {
    component.isSectionTeamShown = true;

    const feedbackResponseRecipient =
      feedbackResponseRecipientBuilder.recipientName('test-name').build();
    feedbackResponseRecipient.recipientTeam = 'test-team';

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient))
      .toBe('test-team | test-name');
  });

  it('getSelectionOptionLabel: should return recipientName if'
  + 'both recipientSection and recipientTeam are undefined', () => {
    component.isSectionTeamShown = true;

    const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name');
  });

  it('toggleSectionTeam: should set isSectionTeamShown to true and sort recipients by'
  + 'Section and Team if FeedbackRecipientLabelType is INCLUDE_SECTION', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_SECTION;
    component.isSectionTeamShown = false;

    const recipient1 = feedbackResponseRecipientBuilder.recipientName('A').build();
    recipient1.recipientSection = 'Section A';
    recipient1.recipientTeam = 'Team B';
    const recipient2 = feedbackResponseRecipientBuilder.recipientName('B').build();
    recipient2.recipientSection = 'Section C';
    recipient2.recipientTeam = 'Team A';
    const recipient3 = feedbackResponseRecipientBuilder.recipientName('C').build();
    recipient3.recipientSection = 'Section B';
    recipient3.recipientTeam = 'Team A';
    component.model.recipientList = [
      recipient1,
      recipient2,
      recipient3,
    ];

    fixture.detectChanges();

    const toggleSectionTeamSpy = jest.spyOn(component, 'toggleSectionTeam');

    getShowSectionTeamCheckBox().nativeElement.click();

    expect(toggleSectionTeamSpy).toHaveBeenCalled();
    expect(component.isSectionTeamShown).toBeTruthy();
    expect(component.model.recipientList).toStrictEqual([recipient1, recipient3, recipient2]);
  });

  it('toggleSectionTeam: should set isSectionTeamShown to true and sort recipients by'
  + 'Team if FeedbackRecipientLabelType is INCLUDE_TEAM', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_TEAM;
    component.isSectionTeamShown = false;

    const recipient1 = feedbackResponseRecipientBuilder.recipientName('A').build();
    recipient1.recipientTeam = 'Team B';
    const recipient2 = feedbackResponseRecipientBuilder.recipientName('B').build();
    recipient2.recipientTeam = 'Team A';
    const recipient3 = feedbackResponseRecipientBuilder.recipientName('C').build();
    recipient3.recipientTeam = 'Team A';
    component.model.recipientList = [
      recipient1,
      recipient2,
      recipient3,
    ];

    fixture.detectChanges();

    const toggleSectionTeamSpy = jest.spyOn(component, 'toggleSectionTeam');

    getShowSectionTeamCheckBox().nativeElement.click();

    expect(toggleSectionTeamSpy).toHaveBeenCalled();
    expect(component.isSectionTeamShown).toBeTruthy();
    expect(component.model.recipientList).toStrictEqual([recipient2, recipient3, recipient1]);
  });

  it('toggleSectionTeam: should set isSectionTeamShown to false and sort recipients by name if toggled', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_TEAM;
    component.isSectionTeamShown = true;

    const recipient1 = feedbackResponseRecipientBuilder.recipientName('B').build();
    recipient1.recipientTeam = 'Team A';
    const recipient2 = feedbackResponseRecipientBuilder.recipientName('C').build();
    recipient2.recipientTeam = 'Team B';
    const recipient3 = feedbackResponseRecipientBuilder.recipientName('A').build();
    recipient3.recipientTeam = 'Team C';
    component.model.recipientList = [
      recipient1,
      recipient2,
      recipient3,
    ];

    fixture.detectChanges();

    const toggleSectionTeamSpy = jest.spyOn(component, 'toggleSectionTeam');

    getShowSectionTeamCheckBox().nativeElement.click();
    getShowSectionTeamCheckBox().nativeElement.click();

    expect(toggleSectionTeamSpy).toHaveBeenCalled();
    expect(component.isSectionTeamShown).toBeFalsy();
    expect(component.model.recipientList).toStrictEqual([recipient3, recipient1, recipient2]);
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty string'
    + 'for recipient in FeedbackQuestionType.TEXT', () => {
    component.model.questionType = FeedbackQuestionType.TEXT;

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackResponseTextDetailsBuilder.answer('').build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackResponseTextDetailsBuilder.answer('some answer').build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answer is not empty string'
    + 'for recipient  FeedbackQuestionType.TEXT', () => {
    component.model.questionType = FeedbackQuestionType.TEXT;

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackResponseTextDetailsBuilder.answer('some answer').build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackResponseTextDetailsBuilder.answer('').build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty string'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.TEXT', () => {
    component.model.questionType = FeedbackQuestionType.TEXT;

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackResponseTextDetailsBuilder.answer('').build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty string'
  + 'for recipient in FeedbackQuestionType.MCQ', () => {
    component.model.questionType = FeedbackQuestionType.MCQ;
    component.model.questionDetails = feedbackMcqQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackResponseMcqDetailsBuilder.answer('').build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackResponseMcqDetailsBuilder.answer('some answer').build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answer is not empty string'
    + 'for recipient  FeedbackQuestionType.MCQ', () => {
    component.model.questionType = FeedbackQuestionType.MCQ;
    component.model.questionDetails = feedbackMcqQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackResponseMcqDetailsBuilder.answer('some answer').build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackResponseMcqDetailsBuilder.answer('').build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty string'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.MCQ', () => {
    component.model.questionType = FeedbackQuestionType.MCQ;
    component.model.questionDetails = feedbackMcqQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackResponseMcqDetailsBuilder.answer('').build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answers is empty array'
  + 'for recipient in FeedbackQuestionType.MSQ', () => {
    component.model.questionType = FeedbackQuestionType.MSQ;
    component.model.questionDetails = feedbackMsqQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackMsqResponseDetailsBuilder.answers([]).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackMsqResponseDetailsBuilder.answers(['some answer']).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answers is not empty arrray'
    + 'for recipient  FeedbackQuestionType.MSQ', () => {
    component.model.questionType = FeedbackQuestionType.MSQ;
    component.model.questionDetails = feedbackMsqQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackMsqResponseDetailsBuilder.answers(['some answer']).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackMsqResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty array'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.MSQ', () => {
    component.model.questionType = FeedbackQuestionType.MSQ;
    component.model.questionDetails = feedbackMsqQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackMsqResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answers is NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED'
  + 'for recipient in FeedbackQuestionType.NUM_SCALE', () => {
    component.model.questionType = FeedbackQuestionType.NUMSCALE;
    component.model.questionDetails = feedbackNumericalScaleQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackNumericalScaleResponseDetailsBuilder.answer(NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackNumericalScaleResponseDetailsBuilder.answer(1).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answers is not NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED'
    + 'for recipient  FeedbackQuestionType.NUM_SCALE', () => {
    component.model.questionType = FeedbackQuestionType.NUMSCALE;
    component.model.questionDetails = feedbackNumericalScaleQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackNumericalScaleResponseDetailsBuilder.answer(1).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackNumericalScaleResponseDetailsBuilder.answer(NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.NUM_SCALE', () => {
    component.model.questionType = FeedbackQuestionType.NUMSCALE;
    component.model.questionDetails = feedbackNumericalScaleQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackNumericalScaleResponseDetailsBuilder.answer(NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED).build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answers is empty array'
  + 'for recipient in FeedbackQuestionType.CONSTSUM_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
    component.model.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackConstantSumResponseDetailsBuilder.answers([]).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackConstantSumResponseDetailsBuilder.answers([1]).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answers is not empty array'
    + 'for recipient  FeedbackQuestionType.CONSTSUM_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
    component.model.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackConstantSumResponseDetailsBuilder.answers([1]).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackConstantSumResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty array'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.CONSTSUM_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
    component.model.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackConstantSumResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answers is empty array'
  + 'for recipient in FeedbackQuestionType.CONSTSUM_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.RUBRIC;
    component.model.questionDetails = feedbackRubricQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackRubricResponseDetailsBuilder.answer([]).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackRubricResponseDetailsBuilder.answer([1]).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answers is not empty array'
    + 'for recipient  FeedbackQuestionType.RUBRIC', () => {
    component.model.questionType = FeedbackQuestionType.RUBRIC;
    component.model.questionDetails = feedbackRubricQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackRubricResponseDetailsBuilder.answer([1]).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackRubricResponseDetailsBuilder.answer([]).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty array'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.RANK_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.RANK_OPTIONS;
    component.model.questionDetails = feedbackRankOptionsQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackRankOptionsResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if responseDetails.answers is not empty array'
  + 'for recipient  FeedbackQuestionType.RANK_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.RANK_OPTIONS;
    component.model.questionDetails = feedbackRankOptionsQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackRankOptionsResponseDetailsBuilder.answers([1]).build(),
      ).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('someid').responseDetails(
        feedbackRankOptionsResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeTruthy();
  });

  it('isSavedForRecipient: returns false if responseDetails.answer is empty array'
    + 'and hasResponseChangedForRecipients for recipient is false in FeedbackQuestionType.RANK_OPTIONS', () => {
    component.model.questionType = FeedbackQuestionType.RANK_OPTIONS;
    component.model.questionDetails = feedbackRankOptionsQuestionDetailsBuilder.build();

    const recipientId = 'recipient-id';
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(recipientId).responseDetails(
        feedbackRankOptionsResponseDetailsBuilder.answers([]).build(),
      ).build(),
    ];
    component.model.hasResponseChangedForRecipients.set(recipientId, false);

    fixture.detectChanges();
    expect(component.isSavedForRecipient(recipientId)).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if isSaved is false for FeedbackQuestionType.CONSTSUM_RECIPIENTS', () => {
    component.model.questionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;
    component.isSaved = false;

    fixture.detectChanges();
    expect(component.isSavedForRecipient('recipientId')).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if isSaved is false for FeedbackQuestionType.CONTRIB', () => {
    component.model.questionType = FeedbackQuestionType.CONTRIB;
    component.isSaved = false;

    fixture.detectChanges();
    expect(component.isSavedForRecipient('recipientId')).toBeFalsy();
  });

  it('isSavedForRecipient: returns false if isSaved is false for FeedbackQuestionType.RANK_RECIPIENTS', () => {
    component.model.questionType = FeedbackQuestionType.RANK_RECIPIENTS;
    component.isSaved = false;

    fixture.detectChanges();
    expect(component.isSavedForRecipient('recipientId')).toBeFalsy();
  });

  it('isSavedForRecipient: returns true if isSaved is true for FeedbackQuestionType.CONSTSUM_RECIPIENTS', () => {
    component.model.questionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;
    component.isSaved = true;

    fixture.detectChanges();
    expect(component.isSavedForRecipient('recipientId')).toBeTruthy();
  });

  it('isSavedForRecipient: returns true if isSaved is true for FeedbackQuestionType.CONTRIB', () => {
    component.model.questionType = FeedbackQuestionType.CONTRIB;
    component.isSaved = true;

    fixture.detectChanges();
    expect(component.isSavedForRecipient('recipientId')).toBeTruthy();
  });

  it('isSavedForRecipient: returns true if isSaved is true for FeedbackQuestionType.RANK_RECIPIENTS', () => {
    component.model.questionType = FeedbackQuestionType.RANK_RECIPIENTS;
    component.isSaved = true;

    fixture.detectChanges();
    expect(component.isSavedForRecipient('recipientId')).toBeTruthy();
  });
});
