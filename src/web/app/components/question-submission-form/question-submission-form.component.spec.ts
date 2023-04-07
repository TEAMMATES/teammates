import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { CommentBoxModule } from '../comment-box/comment-box.module';
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
import {
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormModel,
} from './question-submission-form-model';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

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
  isTabExpanded: true,
};

describe('QuestionSubmissionFormComponent', () => {
  let component: QuestionSubmissionFormComponent;
  let fixture: ComponentFixture<QuestionSubmissionFormComponent>;

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
    const model: QuestionSubmissionFormModel = JSON.parse(JSON.stringify(testNumscaleQuestionSubmissionForm));

    component.formModel = model;
    component.ngDoCheck();

    expect(model.recipientSubmissionForms).toEqual([formResponse3, formResponse4, formResponse2, formResponse1]);
  });

  it('should arrange recipients according to alphabetical order of name after ngDoCheck (Unsorted recipient list)',
    () => {
      const model: QuestionSubmissionFormModel = JSON.parse(JSON.stringify(testNumscaleQuestionSubmissionForm));

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
});
