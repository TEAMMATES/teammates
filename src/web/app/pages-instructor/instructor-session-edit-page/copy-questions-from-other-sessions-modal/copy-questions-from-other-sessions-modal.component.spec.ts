import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { FeedbackSessionTabModel, QuestionToCopyCandidate } from './copy-questions-from-other-sessions-modal-model';
import { CopyQuestionsFromOtherSessionsModalComponent } from './copy-questions-from-other-sessions-modal.component';
import { FeedbackQuestionsService } from '../../../../services/feedback-questions.service';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackQuestionType,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { LoadingRetryModule } from '../../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';

describe('CopyQuestionsFromOtherSessionsModalComponent', () => {

  const testFeedbackQuestion1: FeedbackQuestion = {
    feedbackQuestionId: 'feedback-question-1',
    questionNumber: 1,
    questionBrief: 'question brief',
    questionDescription: 'description',
    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'question text',
    } as FeedbackTextQuestionDetails,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.INSTRUCTORS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  const testFeedbackQuestion2: FeedbackQuestion = {
    feedbackQuestionId: 'feedback-question-2',
    questionNumber: 2,
    questionBrief: 'question brief',
    questionDescription: 'description',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'question text',
      mcqChoices: ['choice 1', 'choice 2', 'choice 3'],
    } as FeedbackMcqQuestionDetails,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.INSTRUCTORS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  const testFeedbackQuestion3: FeedbackQuestion = {
    feedbackQuestionId: 'feedback-question-3',
    questionNumber: 3,
    questionBrief: 'question brief',
    questionDescription: 'description',
    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    questionDetails: {
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      questionText: 'question text',
      minOptionsToBeRanked: 5,
      maxOptionsToBeRanked: 5,
      areDuplicatesAllowed: true,
    } as FeedbackRankRecipientsQuestionDetails,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.INSTRUCTORS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  const testQuestionToCopyCandidate1: QuestionToCopyCandidate = {
    question: testFeedbackQuestion1,
    isSelected: false,
  };

  const testQuestionToCopyCandidate2: QuestionToCopyCandidate = {
    question: testFeedbackQuestion2,
    isSelected: false,
  };

  const testQuestionToCopyCandidate3: QuestionToCopyCandidate = {
    question: testFeedbackQuestion3,
    isSelected: false,
  };

  const testFeedbackSessionTabModel1: FeedbackSessionTabModel = {
    courseId: 'testId1',
    feedbackSessionName: 'Test Session 1',
    createdAtTimestamp: 1644458400, // Thursday, 10 February 2022 10:00:00 GMT+08:00
    questionsTableRowModels: [],
    questionsTableRowModelsSortBy: SortBy.NONE,
    questionsTableRowModelsSortOrder: SortOrder.ASC,

    hasQuestionsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  const testFeedbackSessionTabModel2: FeedbackSessionTabModel = {
    courseId: 'testId2',
    feedbackSessionName: 'Test Session 2',
    createdAtTimestamp: 1645063200, // Thursday, 17 February 2022 10:00:00 GMT+08:00
    questionsTableRowModels: [],
    questionsTableRowModelsSortBy: SortBy.NONE,
    questionsTableRowModelsSortOrder: SortOrder.ASC,

    hasQuestionsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  let component: CopyQuestionsFromOtherSessionsModalComponent;
  let fixture: ComponentFixture<CopyQuestionsFromOtherSessionsModalComponent>;
  let feedbackQuestionsService: FeedbackQuestionsService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CopyQuestionsFromOtherSessionsModalComponent],
      imports: [
        CommonModule,
        FormsModule,
        TeammatesCommonModule,
        PanelChevronModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        HttpClientTestingModule,
      ],
      providers: [
        NgbActiveModal,
        FeedbackQuestionsService,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyQuestionsFromOtherSessionsModalComponent);
    feedbackQuestionsService = TestBed.inject(FeedbackQuestionsService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback sessions', () => {
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1, testFeedbackSessionTabModel2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions are loading', () => {
    testFeedbackSessionTabModel1.isTabExpanded = true;
    testFeedbackSessionTabModel1.hasQuestionsLoaded = false;
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions are loaded', () => {
    testFeedbackSessionTabModel1.isTabExpanded = true;
    testFeedbackSessionTabModel1.hasQuestionsLoaded = true;
    testFeedbackSessionTabModel1.questionsTableRowModels = [testQuestionToCopyCandidate1, testQuestionToCopyCandidate2];
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions failed to load', () => {
    testFeedbackSessionTabModel1.isTabExpanded = true;
    testFeedbackSessionTabModel1.hasLoadingFailed = true;
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load feedback questions', () => {
    const feedbackQuestions: FeedbackQuestions = {
      questions: [testFeedbackQuestion1, testFeedbackQuestion2],
    };
    jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions').mockReturnValue(of(feedbackQuestions));
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1];

    component.loadQuestions(testFeedbackSessionTabModel1);

    expect(component.feedbackSessionTabModels[0].hasQuestionsLoaded).toBeTruthy();
    expect(component.feedbackSessionTabModels[0].hasLoadingFailed).toBeFalsy();
    expect(component.feedbackSessionTabModels[0].questionsTableRowModels.length).toBe(2);
    expect(component.feedbackSessionTabModels[0].questionsTableRowModels[0].question.feedbackQuestionId)
      .toBe(testFeedbackQuestion1.feedbackQuestionId);
    expect(component.feedbackSessionTabModels[0].questionsTableRowModels[1].question.feedbackQuestionId)
      .toBe(testFeedbackQuestion2.feedbackQuestionId);
  });

  it('should not allow copying when no questions are selected', () => {
    testFeedbackSessionTabModel1.questionsTableRowModels = [testQuestionToCopyCandidate1, testQuestionToCopyCandidate2];
    testFeedbackSessionTabModel2.questionsTableRowModels = [testQuestionToCopyCandidate3];
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1, testFeedbackSessionTabModel2];
    fixture.detectChanges();

    const questions: FeedbackQuestion[] = component.getSelectedQuestions();
    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-question');

    expect(component.hasAnyQuestionsToCopySelected).toBeFalsy();
    expect(questions.length).toBe(0);
    expect(button.disabled).toBeTruthy();
  });

  it('should copy selected questions', () => {
    testQuestionToCopyCandidate1.isSelected = true;
    testQuestionToCopyCandidate2.isSelected = true;
    testQuestionToCopyCandidate3.isSelected = true;
    testFeedbackSessionTabModel1.questionsTableRowModels = [testQuestionToCopyCandidate1, testQuestionToCopyCandidate2];
    testFeedbackSessionTabModel2.questionsTableRowModels = [testQuestionToCopyCandidate3];
    component.feedbackSessionTabModels = [testFeedbackSessionTabModel1, testFeedbackSessionTabModel2];
    fixture.detectChanges();

    jest.spyOn(component.activeModal, 'close').mockImplementation((questions: FeedbackQuestion[]) => {
      expect(questions.length).toBe(3);
      expect(questions[0].feedbackQuestionId).toBe(testFeedbackQuestion1.feedbackQuestionId);
      expect(questions[1].feedbackQuestionId).toBe(testFeedbackQuestion2.feedbackQuestionId);
      expect(questions[2].feedbackQuestionId).toBe(testFeedbackQuestion3.feedbackQuestionId);
    });

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-question');

    expect(component.hasAnyQuestionsToCopySelected).toBeTruthy();
    expect(button.disabled).toBeFalsy();

    button.click();
    expect(component.activeModal.close).toHaveBeenCalledTimes(1);
  });
});
