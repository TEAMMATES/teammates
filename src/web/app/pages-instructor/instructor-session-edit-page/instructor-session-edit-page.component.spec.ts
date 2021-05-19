import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { NavigationService } from 'src/web/services/navigation.service';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StudentService } from '../../../services/student.service';
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
  Instructor,
  Instructors,
  JoinState,
  NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
  Students,
} from '../../../types/api-output';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { QuestionEditFormModel } from '../../components/question-edit-form/question-edit-form-model';
import { SessionEditFormModel } from '../../components/session-edit-form/session-edit-form-model';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { InstructorSessionEditPageComponent } from './instructor-session-edit-page.component';
import { InstructorSessionEditPageModule } from './instructor-session-edit-page.module';
import Spy = jasmine.Spy;

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

  const sessionEditFormModel: SessionEditFormModel = {
    courseId: 'testId',
    timeZone: 'Asia/Singapore',
    courseName: 'Test Course',
    feedbackSessionName: 'test session',
    instructions: 'Instructions',

    submissionStartTime: { hour: 23, minute: 59 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 23, minute: 59 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 23, minute: 59 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 23, minute: 59 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: false,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  let component: InstructorSessionEditPageComponent;
  let fixture: ComponentFixture<InstructorSessionEditPageComponent>;
  let feedbackSessionsService: FeedbackSessionsService;
  let feedbackQuestionsService: FeedbackQuestionsService;
  let courseService: CourseService;
  let studentService: StudentService;
  let instructorService: InstructorService;
  let navigationService: NavigationService;

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
    studentService = TestBed.inject(StudentService);
    instructorService = TestBed.inject(InstructorService);
    navigationService = TestBed.inject(NavigationService);
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
      questions: [testFeedbackQuestion1, testFeedbackQuestion2],
    };
    spyOn(feedbackQuestionsService, 'getFeedbackQuestions').and.returnValue(of(feedbackQuestions));

    component.loadFeedbackQuestions();
    expect(component.questionEditFormModels.length).toBe(2);
    expect(component.questionEditFormModels[0].feedbackQuestionId).toBe('feedback-question-1');
    expect(component.questionEditFormModels[1].feedbackQuestionId).toBe('feedback-question-2');
  });

  it('should get all students of the course', () => {
    const testStudent1: Student = {
      email: 'alice@tmms.com',
      courseId: 'testId',
      name: 'Alice',
      teamName: 'Team 1',
      sectionName: 'Section 1',
    };
    const testStudent2: Student = {
      email: 'bob@tmms.com',
      courseId: 'testId',
      name: 'Bob',
      teamName: 'Team 1',
      sectionName: 'Section 1',
    };
    const students: Students = {
      students: [testStudent1, testStudent2],
    };
    spyOn(studentService, 'getStudentsFromCourse').and.returnValue(of(students));

    component.getAllStudentsOfCourse();

    expect(component.studentsOfCourse.length).toBe(2);
    expect(component.studentsOfCourse[0].name).toBe('Alice');
  });

  it('should get all instructors of the course', () => {
    const testInstructor1: Instructor = {
      courseId: 'testId',
      email: 'testB@example.com',
      name: 'Instructor A',
      joinState: JoinState.JOINED,
    };
    const testInstructor2: Instructor = {
      courseId: 'testId',
      email: 'testA@example.com',
      name: 'Instructor B',
      joinState: JoinState.JOINED,
    };
    const instructors: Instructors = {
      instructors: [testInstructor1, testInstructor2],
    };
    spyOn(instructorService, 'loadInstructors').and.returnValue(of(instructors));

    component.getAllInstructorsCanBePreviewedAs();
    expect(component.instructorsCanBePreviewedAs.length).toBe(2);
    expect(component.instructorsCanBePreviewedAs[0].name).toBe('Instructor A');
  });

  it('should collapse all questions', () => {
    testQuestionEditFormModel1.isCollapsed = false;
    testQuestionEditFormModel2.isCollapsed = false;
    component.isLoadingFeedbackQuestions = false;
    component.questionEditFormModels = [testQuestionEditFormModel1, testQuestionEditFormModel2];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-collapse-expand');
    button.click();

    expect(component.questionEditFormModels[0].isCollapsed).toBe(true);
    expect(component.questionEditFormModels[1].isCollapsed).toBe(true);
  });

  it('should expand all questions when at least one question is collapsed', () => {
    testQuestionEditFormModel1.isCollapsed = true;
    testQuestionEditFormModel2.isCollapsed = false;
    component.questionEditFormModels = [testQuestionEditFormModel1, testQuestionEditFormModel2];
    component.isLoadingFeedbackQuestions = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-collapse-expand');
    button.click();

    expect(component.questionEditFormModels[0].isCollapsed).toBe(false);
    expect(component.questionEditFormModels[1].isCollapsed).toBe(false);
  });

  it('should cancel edit session', () => {
    component.feedbackSessionModelBeforeEditing = JSON.parse(JSON.stringify(sessionEditFormModel));
    const testSessionEditFormModel: SessionEditFormModel = JSON.parse(JSON.stringify(sessionEditFormModel));
    testSessionEditFormModel.instructions = 'New instructions';
    component.sessionEditFormModel = sessionEditFormModel;
    component.isLoadingFeedbackSession = false;

    component.cancelEditingSessionHandler();
    expect(component.sessionEditFormModel.instructions).toBe('Instructions');
  });

  it('should edit existing session', () => {
    component.feedbackSessionModelBeforeEditing = JSON.parse(JSON.stringify(sessionEditFormModel));
    sessionEditFormModel.instructions = 'New instructions';
    component.sessionEditFormModel = sessionEditFormModel;
    component.isLoadingFeedbackSession = false;

    component.editExistingSessionHandler();
    expect(component.feedbackSessionModelBeforeEditing.instructions).toBe('New instructions');
  });

  it('should delete current session', () => {
    component.sessionEditFormModel = JSON.parse(JSON.stringify(sessionEditFormModel));;
    const navSpy: Spy = spyOn(navigationService, 'navigateWithSuccessMessage');
    spyOn(feedbackSessionsService, 'moveSessionToRecycleBin').and.returnValue(of(true));
    component.deleteExistingSessionHandler();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/instructor/sessions');
    expect(navSpy.calls.mostRecent().args[2]).toEqual('The feedback session has been deleted. '
      + 'You can restore it from the deleted sessions table below.');
  });
});
