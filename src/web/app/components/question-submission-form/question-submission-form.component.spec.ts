import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  FeedbackRecipientLabelType,
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
  ResponseSubmissionStatus,
} from './question-submission-form-model';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { createBuilder } from '../../../test-helpers/generic-builder';
import testEventEmission from '../../../test-helpers/test-event-emitter';
import {
  FeedbackQuestionType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackTextResponseDetails,
} from '../../../types/api-output';
import { SessionView } from '../../pages-session/session-submission-page/session-view.enum';

const formResponse1: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-1',
  recipientIdentifier: 'hans-charlie-id',
  responseDetails: {
    answer: 5,
  } as FeedbackNumericalScaleResponseDetails,
  status: ResponseSubmissionStatus.SAVED,
  isValid: true,
};

const formResponse2: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-2',
  recipientIdentifier: 'harris-barry-id',
  responseDetails: {
    answer: 4,
  } as FeedbackNumericalScaleResponseDetails,
  status: ResponseSubmissionStatus.SAVED,
  isValid: true,
};

const formResponse3: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-3',
  recipientIdentifier: 'rogers-alan-id',
  responseDetails: {
    answer: 3,
  } as FeedbackNumericalScaleResponseDetails,
  status: ResponseSubmissionStatus.SAVED,
  isValid: true,
};

const formResponse4: FeedbackResponseRecipientSubmissionFormModel = {
  responseId: 'response-id-4',
  recipientIdentifier: 'buck-arthur-id',
  responseDetails: {
    answer: 2,
  } as FeedbackNumericalScaleResponseDetails,
  status: ResponseSubmissionStatus.SAVED,
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
  giverType: QuestionGiverType.STUDENTS,
  recipientType: QuestionRecipientType.STUDENTS,

  recipientList: [
    { recipientName: 'Alan Rogers', recipientIdentifier: 'rogers-alan-id' },
    { recipientName: 'Arthur Buck', recipientIdentifier: 'buck-arthur-id' },
    { recipientName: 'Barry Harris', recipientIdentifier: 'harris-barry-id' },
    { recipientName: 'Charlie Hans', recipientIdentifier: 'hans-charlie-id' },
  ],

  recipientSubmissionForms: [formResponse1, formResponse2, formResponse3, formResponse4],
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
  customNumberOfEntitiesToGiveFeedbackTo: 4,
  showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],

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

  const feedbackResponseRecipientBuilder = createBuilder<FeedbackResponseRecipient>({
    recipientIdentifier: 'testIdentifier',
    recipientName: 'test-name',
  });

  const recipientSubmissionFormBuilder = createBuilder<FeedbackResponseRecipientSubmissionFormModel>({
    responseId: 'test-id',
    responseDetails: {
      questionType: FeedbackQuestionType.TEXT,
      answer: 'answer',
    } as FeedbackTextResponseDetails,
    recipientIdentifier: 'testIdentifier',
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

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

  it('should arrange recipients according to alphabetical order of name after ngDoCheck (Unsorted recipient list)', () => {
    const model: QuestionSubmissionFormModel = structuredClone(testNumscaleQuestionSubmissionForm);

    // Change recipient list to unsorted
    model.recipientList = [
      { recipientName: 'Charlie Hans', recipientIdentifier: 'hans-charlie-id' },
      { recipientName: 'Alan Rogers', recipientIdentifier: 'rogers-alan-id' },
      { recipientName: 'Barry Harris', recipientIdentifier: 'harris-barry-id' },
      { recipientName: 'Arthur Buck', recipientIdentifier: 'buck-arthur-id' },
    ];

    component.formModel = model;

    expect(model.recipientSubmissionForms).toEqual([formResponse3, formResponse4, formResponse2, formResponse1]);
  });

  it('should arrange recipients by section and team when section/team labels are shown', () => {
    const model: QuestionSubmissionFormModel = structuredClone(testNumscaleQuestionSubmissionForm);
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    model.recipientList = [
      { recipientName: 'Alan Rogers', recipientIdentifier: 'rogers-alan-id', recipientSection: 'Section C', recipientTeam: 'Team B' },
      { recipientName: 'Arthur Buck', recipientIdentifier: 'buck-arthur-id', recipientSection: 'Section A', recipientTeam: 'Team C' },
      { recipientName: 'Barry Harris', recipientIdentifier: 'harris-barry-id', recipientSection: 'Section A', recipientTeam: 'Team A' },
      { recipientName: 'Charlie Hans', recipientIdentifier: 'hans-charlie-id', recipientSection: 'Section B', recipientTeam: 'Team A' },
    ];
    component.formModel = model;

    expect(model.recipientList.map((recipient) => recipient.recipientIdentifier)).toEqual([
      'harris-barry-id',
      'buck-arthur-id',
      'hans-charlie-id',
      'rogers-alan-id',
    ]);
    expect(model.recipientSubmissionForms.map((form) => form.recipientIdentifier)).toEqual([
      'harris-barry-id',
      'buck-arthur-id',
      'hans-charlie-id',
      'rogers-alan-id',
    ]);
  });

  it('isSaved: returns false when there are no saved responses', () => {
    component.model.recipientSubmissionForms.push(
      { ...recipientSubmissionFormBuilder.build(), status: ResponseSubmissionStatus.NEW },
      { ...recipientSubmissionFormBuilder.build(), status: ResponseSubmissionStatus.MODIFIED },
    );

    expect(component.isSaved()).toBeFalsy();
  });

  it('isSaved: returns true when at least one response is saved', () => {
    component.model.recipientSubmissionForms.push(
      { ...recipientSubmissionFormBuilder.build(), status: ResponseSubmissionStatus.NEW },
      {
        ...recipientSubmissionFormBuilder
          .recipientIdentifier('other-id')
          .status(ResponseSubmissionStatus.SAVED)
          .build(),
      },
    );

    expect(component.isSaved()).toBeTruthy();
  });

  it('hasSectionTeam: should return false if QuestionSubmissionFormMode is not FLEXIBLE_RECIPIENT', () => {
    component.formMode = QuestionSubmissionFormMode.FIXED_RECIPIENT;

    fixture.detectChanges();

    expect(component.hasSectionTeam).toBeFalsy();
  });

  it(
    'hasSectionTeam: should return false if QuestionSubmissionFormMode is FLEXIBLE_RECIPIENT' +
      'and FeedbackRecipientLabelType is not INCLUDE_TEAM or INCLUDE_SECTION',
    () => {
      component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
      component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_NAME;

      fixture.detectChanges();
      expect(component.hasSectionTeam).toBeFalsy();
    },
  );

  it(
    'hasSectionTeam: should return true if QuestionSubmissionFormMode is FLEXIBLE_RECIPIENT' +
      'and FeedbackRecipientLabelType is INCLUDE_SECTION',
    () => {
      component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
      component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_SECTION;

      fixture.detectChanges();

      expect(component.hasSectionTeam).toBeTruthy();
    },
  );

  it(
    'hasSectionTeam: should return true if QuestionSubmissionFormMode is FLEXIBLE_RECIPIENT' +
      'and FeedbackRecipientLabelType is INCLUDE_TEAM',
    () => {
      component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
      component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_TEAM;

      fixture.detectChanges();
      expect(component.hasSectionTeam).toBeTruthy();
    },
  );

  it('toggleQuestionTab: should toggle isTabExpanded if currentSelectedSessionView is DEFAULT', () => {
    component.currentSelectedSessionView = SessionView.DEFAULT;
    component.model.isTabExpanded = true;
    let emittedModel: QuestionSubmissionFormModel | undefined;
    testEventEmission(component.formModelChange, (value) => {
      emittedModel = value;
    });

    component.toggleQuestionTab();

    expect(component.model.isTabExpanded).toBeFalsy();
    expect(emittedModel).toStrictEqual(component.model);
  });

  it(
    'toggleQuestionTab: should toggle isTabExpanded for only the recipientId in isTabExpandedForRecipients' +
      'if currentSelectedSessionView is not DEFAULT',
    () => {
      component.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
      component.model.isTabExpanded = true;
      component.recipientId = 'test-id';
      const otherRecipientId = 'test-other-id';

      let emittedModel: QuestionSubmissionFormModel | undefined;
      testEventEmission(component.formModelChange, (value) => {
        emittedModel = value;
      });

      component.toggleQuestionTab();

      expect(component.model.isTabExpanded).toBeTruthy();
      expect(component.model.isTabExpandedForRecipients.get(component.recipientId)).toBeTruthy();
      expect(component.model.isTabExpandedForRecipients.get(otherRecipientId)).toBeFalsy();
      expect(emittedModel).toStrictEqual(component.model);
    },
  );

  it(
    'shouldTabExpand: should set recipientId in isTabExpandedForRecipients to true if its undefined' +
      'and currentSelectedSessionView is not DEFAULT',
    () => {
      component.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
      const recipientId = 'test-id';
      component.recipientId = recipientId;

      expect(component.shouldTabExpand()).toBeTruthy();
      expect(component.model.isTabExpandedForRecipients.get(recipientId)).toBeTruthy();
    },
  );

  it(
    'shouldTabExpand: should return isTabExpandedForRecipients for recipientId if' +
      'currentSelectedSessionView is not DEFAULT',
    () => {
      component.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
      const recipientId = 'test-id';
      component.recipientId = recipientId;
      component.model.isTabExpandedForRecipients.set(recipientId, false);

      expect(component.shouldTabExpand()).toBeFalsy();
    },
  );

  it('isRecipientSelected: should return true if FeedbackResponseRecipient exists in recipientSubmissionForms', () => {
    const feedbackResponseRecipientIdentifier = 'test-identifer';
    const feedbackResponseRecipient = feedbackResponseRecipientBuilder
      .recipientIdentifier(feedbackResponseRecipientIdentifier)
      .build();
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(feedbackResponseRecipientIdentifier).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('testid').build(),
    ];

    expect(component.isRecipientSelected(feedbackResponseRecipient)).toBeTruthy();
  });

  it(
    'isRecipientSelected: should return false if FeedbackResponseRecipient does not' +
      'exist in recipientSubmissionForms',
    () => {
      const feedbackResponseRecipientIdentifier = 'test-identifer';
      const feedbackResponseRecipient = feedbackResponseRecipientBuilder
        .recipientIdentifier(feedbackResponseRecipientIdentifier)
        .build();
      component.model.recipientSubmissionForms = [
        recipientSubmissionFormBuilder.recipientIdentifier('testid1').build(),
        recipientSubmissionFormBuilder.recipientIdentifier('testid2').build(),
      ];

      expect(component.isRecipientSelected(feedbackResponseRecipient)).toBeFalsy();
    },
  );

  it('triggerDeleteCommentEvent: should emit the correct index to deleteCommentEvent', () => {
    let emittedIndex: number | undefined;
    testEventEmission(component.deleteCommentEvent, (index) => {
      emittedIndex = index;
    });

    component.triggerDeleteCommentEvent(5);
    expect(emittedIndex).toBe(5);
  });

  it(
    'addNewParticipantCommentToResponse: should call triggerRecipientSubmissionFormChange' + 'with the correct index',
    () => {
      const triggerRecipientSubmissionFormChangeSpy = vi
        .spyOn(component, 'triggerRecipientSubmissionFormChange')
        .mockReturnValue();

      component.addNewParticipantCommentToResponse(3);

      expect(triggerRecipientSubmissionFormChangeSpy).toHaveBeenCalledWith(3, 'commentByGiver', {
        commentType: 'new',
        commentEditFormModel: {
          commentText: '',
          showCommentTo: ['GIVER'],
          showGiverNameTo: ['GIVER'],
        },
        isEditing: true,
      });
    },
  );

  it(
    'cancelAddingNewParticipantComment: should call triggerRecipientSubmissionFormChange' + 'with the correct index',
    () => {
      const triggerRecipientSubmissionFormChangeSpy = vi
        .spyOn(component, 'triggerRecipientSubmissionFormChange')
        .mockReturnValue();

      component.cancelAddingNewParticipantComment(3);

      expect(triggerRecipientSubmissionFormChangeSpy).toHaveBeenCalledWith(3, 'commentByGiver', undefined);
    },
  );

  it('updateValidity: should not emit formModelChange if there are no recipientSubmissionForms', () => {
    component.model.recipientSubmissionForms = [];
    const formModelChangeSpy = vi.spyOn(component.formModelChange, 'emit');

    component.updateValidity(true);

    expect(formModelChangeSpy).not.toHaveBeenCalled();
  });

  it('saveFeedbackResponses: should emit responsesSave event', () => {
    const form1 = { ...recipientSubmissionFormBuilder.build(), status: ResponseSubmissionStatus.MODIFIED };
    const form2 = { ...recipientSubmissionFormBuilder.build(), status: ResponseSubmissionStatus.NEW };
    component.model.recipientSubmissionForms = [form1, form2];

    let emittedModel: QuestionSubmissionFormModel | undefined;
    testEventEmission(component.responsesSave, (value) => {
      emittedModel = value;
    });

    component.saveFeedbackResponses();

    expect(emittedModel).toStrictEqual(component.model);
  });

  it('getSelectionOptionLabel: should return recipient name when section/team labels are not shown', () => {
    const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name');
  });

  it('getSelectionOptionLabel: should return recipientSection and recipientTeam if both are defined', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_SECTION;

    const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();
    feedbackResponseRecipient.recipientSection = 'test-section';
    feedbackResponseRecipient.recipientTeam = 'test-team';

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name (test-team / test-section)');
  });

  it('getSelectionOptionLabel: should return only recipientSection if recipientTeam is undefined', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_SECTION;

    const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();
    feedbackResponseRecipient.recipientSection = 'test-section';

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name (test-section)');
  });

  it('getSelectionOptionLabel: should return only recipientTeam if recipientSection is undefined', () => {
    component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
    component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_TEAM;

    const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();
    feedbackResponseRecipient.recipientTeam = 'test-team';

    expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name (test-team)');
  });

  it(
    'getSelectionOptionLabel: should return recipientName if both recipientSection and recipientTeam are undefined',
    () => {
      component.formMode = QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT;
      component.recipientLabelType = FeedbackRecipientLabelType.INCLUDE_SECTION;

      const feedbackResponseRecipient = feedbackResponseRecipientBuilder.recipientName('test-name').build();

      expect(component.getSelectionOptionLabel(feedbackResponseRecipient)).toBe('test-name');
    },
  );

  it('getRecipientComboboxOptions: should include unselected recipients and the current recipient', () => {
    const selectedRecipient = feedbackResponseRecipientBuilder
      .recipientIdentifier('selected-recipient-id')
      .recipientName('Selected Recipient')
      .build();
    const availableRecipient = feedbackResponseRecipientBuilder
      .recipientIdentifier('available-recipient-id')
      .recipientName('Available Recipient')
      .build();
    const currentRecipient = feedbackResponseRecipientBuilder
      .recipientIdentifier('current-recipient-id')
      .recipientName('Current Recipient')
      .build();
    const currentSubmissionForm = recipientSubmissionFormBuilder
      .recipientIdentifier(currentRecipient.recipientIdentifier)
      .build();
    component.model.recipientList = [selectedRecipient, availableRecipient, currentRecipient];
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier(selectedRecipient.recipientIdentifier).build(),
      currentSubmissionForm,
    ];

    const options = component.getRecipientComboboxOptions(currentSubmissionForm);

    expect(options.map((option) => option.value)).toEqual([
      availableRecipient.recipientIdentifier,
      currentRecipient.recipientIdentifier,
    ]);
    expect(options.map((option) => option.label)).toEqual([
      availableRecipient.recipientName,
      currentRecipient.recipientName,
    ]);
  });

  it('isSaved: returns false when no matching recipient response is saved', () => {
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier('recipient-a').status(ResponseSubmissionStatus.SAVED).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('recipient-b').status(ResponseSubmissionStatus.NEW).build(),
    ];

    expect(component.isSaved('recipient-c')).toBeFalsy();
  });

  it('isSaved: returns true when the matching recipient response is saved', () => {
    component.model.recipientSubmissionForms = [
      recipientSubmissionFormBuilder.recipientIdentifier('recipient-a').status(ResponseSubmissionStatus.NEW).build(),
      recipientSubmissionFormBuilder.recipientIdentifier('recipient-b').status(ResponseSubmissionStatus.SAVED).build(),
    ];

    expect(component.isSaved('recipient-b')).toBeTruthy();
  });
});
