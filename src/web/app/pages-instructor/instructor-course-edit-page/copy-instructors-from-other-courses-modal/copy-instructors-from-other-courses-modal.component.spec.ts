import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
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
import { CourseTabModel, InstructorToCopyCandidateModel } from './copy-instructors-from-other-courses-modal-model';
import { CopyInstructorsFromOtherCoursesModalComponent } from './copy-instructors-from-other-courses-modal.component';

describe('CopyInstructorsFromOtherCoursesModalComponent', () => {

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

  const testQuestionToCopyCandidate1: InstructorToCopyCandidateModel = {
    instructor: testFeedbackQuestion1,
    isSelected: false,
  };

  const testQuestionToCopyCandidate2: InstructorToCopyCandidateModel = {
    instructor: testFeedbackQuestion2,
    isSelected: false,
  };

  const testQuestionToCopyCandidate3: InstructorToCopyCandidateModel = {
    instructor: testFeedbackQuestion3,
    isSelected: false,
  };

  const testFeedbackSessionTabModel1: CourseTabModel = {
    courseId: 'testId1',
    courseName: 'Test Session 1',
    creationTimestamp: 1644458400, // Thursday, 10 February 2022 10:00:00 GMT+08:00
    isArchived: false,
    instructorCandidates: [],
    instructorCandidatesSortBy: SortBy.NONE,
    instructorCandidatesSortOrder: SortOrder.ASC,

    hasInstructorsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  const testFeedbackSessionTabModel2: CourseTabModel = {
    courseId: 'testId2',
    courseName: 'Test Session 2',
    creationTimestamp: 1645063200, // Thursday, 17 February 2022 10:00:00 GMT+08:00
    isArchived: true,
    instructorCandidates: [],
    instructorCandidatesSortBy: SortBy.NONE,
    instructorCandidatesSortOrder: SortOrder.ASC,

    hasInstructorsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  let component: CopyInstructorsFromOtherCoursesModalComponent;
  let fixture: ComponentFixture<CopyInstructorsFromOtherCoursesModalComponent>;
  let feedbackQuestionsService: FeedbackQuestionsService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CopyInstructorsFromOtherCoursesModalComponent],
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
    fixture = TestBed.createComponent(CopyInstructorsFromOtherCoursesModalComponent);
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
    component.courses = [testFeedbackSessionTabModel1, testFeedbackSessionTabModel2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions are loading', () => {
    testFeedbackSessionTabModel1.isTabExpanded = true;
    testFeedbackSessionTabModel1.hasInstructorsLoaded = false;
    component.courses = [testFeedbackSessionTabModel1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions are loaded', () => {
    testFeedbackSessionTabModel1.isTabExpanded = true;
    testFeedbackSessionTabModel1.hasInstructorsLoaded = true;
    testFeedbackSessionTabModel1.instructorCandidates = [testQuestionToCopyCandidate1, testQuestionToCopyCandidate2];
    component.courses = [testFeedbackSessionTabModel1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions failed to load', () => {
    testFeedbackSessionTabModel1.isTabExpanded = true;
    testFeedbackSessionTabModel1.hasLoadingFailed = true;
    component.courses = [testFeedbackSessionTabModel1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load feedback questions', () => {
    const feedbackQuestions: FeedbackQuestions = {
      questions: [testFeedbackQuestion1, testFeedbackQuestion2],
    };
    jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions').mockReturnValue(of(feedbackQuestions));
    component.courses = [testFeedbackSessionTabModel1];

    component.loadInstructors(testFeedbackSessionTabModel1);

    expect(component.courses[0].hasInstructorsLoaded).toBeTruthy();
    expect(component.courses[0].hasLoadingFailed).toBeFalsy();
    expect(component.courses[0].instructorCandidates.length).toBe(2);
    expect(component.courses[0].instructorCandidates[0].instructor.feedbackQuestionId)
      .toBe(testFeedbackQuestion1.feedbackQuestionId);
    expect(component.courses[0].instructorCandidates[1].instructor.feedbackQuestionId)
      .toBe(testFeedbackQuestion2.feedbackQuestionId);
  });

  it('should not allow copying when no questions are selected', () => {
    testFeedbackSessionTabModel1.instructorCandidates = [testQuestionToCopyCandidate1, testQuestionToCopyCandidate2];
    testFeedbackSessionTabModel2.instructorCandidates = [testQuestionToCopyCandidate3];
    component.courses = [testFeedbackSessionTabModel1, testFeedbackSessionTabModel2];
    fixture.detectChanges();

    const questions: FeedbackQuestion[] = component.getSelectedInstructors();
    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-question');

    expect(component.isAnyInstructorCandidatesSelected).toBeFalsy();
    expect(questions.length).toBe(0);
    expect(button.disabled).toBeTruthy();
  });

  it('should copy selected questions', () => {
    testQuestionToCopyCandidate1.isSelected = true;
    testQuestionToCopyCandidate2.isSelected = true;
    testQuestionToCopyCandidate3.isSelected = true;
    testFeedbackSessionTabModel1.instructorCandidates = [testQuestionToCopyCandidate1, testQuestionToCopyCandidate2];
    testFeedbackSessionTabModel2.instructorCandidates = [testQuestionToCopyCandidate3];
    component.courses = [testFeedbackSessionTabModel1, testFeedbackSessionTabModel2];
    fixture.detectChanges();

    jest.spyOn(component.activeModal, 'close').mockImplementation((questions: FeedbackQuestion[]) => {
      expect(questions.length).toBe(3);
      expect(questions[0].feedbackQuestionId).toBe(testFeedbackQuestion1.feedbackQuestionId);
      expect(questions[1].feedbackQuestionId).toBe(testFeedbackQuestion2.feedbackQuestionId);
      expect(questions[2].feedbackQuestionId).toBe(testFeedbackQuestion3.feedbackQuestionId);
    });

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-question');

    expect(component.isAnyInstructorCandidatesSelected).toBeTruthy();
    expect(button.disabled).toBeFalsy();

    button.click();
    expect(component.activeModal.close).toHaveBeenCalledTimes(1);
  });
});
