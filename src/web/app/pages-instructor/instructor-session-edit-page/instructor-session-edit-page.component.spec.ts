import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { InstructorSessionEditPageComponent } from './instructor-session-edit-page.component';
import { InstructorSessionEditPageModule } from './instructor-session-edit-page.module';
import { QuestionEditFormModel } from '../../components/question-edit-form/question-edit-form-model';
import {
  Course,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FeedbackSessionsService } from 'src/web/services/feedback-sessions.service';
import { CourseService } from 'src/web/services/course.service';
import { of } from 'rxjs';
import { FeedbackQuestionsService } from 'src/web/services/feedback-questions.service';


describe('InstructorSessionEditPageComponent', () => {

  const testCourse: Course = {
    courseId: 'testId',
    courseName: 'Test Course',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const testFeedbackSession: FeedbackSession = {
    courseId: 'testId',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'Test Session',
    instructions: 'Instructions',
    submissionStartTimestamp: 1000000000000,
    submissionEndTimestamp: 1500000000000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

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

  const testQuestionEditFormModel1: QuestionEditFormModel = {
    feedbackQuestionId: 'feedback-question-1',
    questionNumber: 1,
    questionBrief: 'question brief',
    questionDescription: 'description',
    isQuestionHasResponses: false,
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
    isEditable: true,
    isSaving: false,
    isCollapsed: false,
    isVisibilityChanged: false,
    isFeedbackPathChanged: false,
    isQuestionDetailsChanged: false,
  };

  const testQuestionEditFormModel2: QuestionEditFormModel = {
    feedbackQuestionId: 'feedback-question-2',
    questionNumber: 2,
    questionBrief: 'question brief',
    questionDescription: 'description',
    isQuestionHasResponses: false,
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
    isEditable: true,
    isSaving: false,
    isCollapsed: false,
    isVisibilityChanged: false,
    isFeedbackPathChanged: false,
    isQuestionDetailsChanged: false,
  };

  let component: InstructorSessionEditPageComponent;
  let fixture: ComponentFixture<InstructorSessionEditPageComponent>;
  let feedbackSessionsService: FeedbackSessionsService;
  let feedbackQuestionsService: FeedbackQuestionsService;
  let courseService: CourseService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        FormsModule,
        AjaxLoadingModule,
        RouterTestingModule,
        TeammatesRouterModule,
        HttpClientTestingModule,
        InstructorSessionEditPageModule,
        BrowserAnimationsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionEditPageComponent);
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
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

  it('should snap with feedback session questions', () => {
    component.questionEditFormModels = [testQuestionEditFormModel1, testQuestionEditFormModel2];
    component.isLoadingFeedbackSession = false;
    component.isLoadingFeedbackQuestions = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with new question added', () => {
    component.isAddingQuestionPanelExpanded = true;
    component.isLoadingFeedbackSession = false;
    component.isLoadingFeedbackQuestions = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback session failed to load', () => {
    component.hasLoadingFeedbackSessionFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load correct feedback session for a given API output', () => {
    spyOn(courseService, 'getCourseAsInstructor').and.returnValue(of(testCourse));
    spyOn(feedbackSessionsService, 'getFeedbackSession').and.returnValue(of(testFeedbackSession));

    component.loadFeedbackSession();

    expect(component.courseName).toBe('Test Course');
    expect(component.sessionEditFormModel.courseId).toBe('testId');
    expect(component.sessionEditFormModel.courseName).toBe('Test Course');
    expect(component.sessionEditFormModel.feedbackSessionName).toBe('Test Session');
  });

  it('should load correct feedback session questions', () => {
    const feedbackQuestions: FeedbackQuestions = {
      questions: [testFeedbackQuestion1, testFeedbackQuestion2]
    };
    spyOn(feedbackQuestionsService, 'getFeedbackQuestions').and.returnValue(of(feedbackQuestions));
    
    component.loadFeedbackQuestions();
    expect(component.questionEditFormModels.length).toBe(2);
    expect(component.questionEditFormModels[0].feedbackQuestionId).toBe('feedback-question-1');
    expect(component.questionEditFormModels[1].feedbackQuestionId).toBe('feedback-question-2');
  })
});
