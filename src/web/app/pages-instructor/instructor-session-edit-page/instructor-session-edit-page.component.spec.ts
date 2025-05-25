import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import {
  CopyQuestionsFromOtherSessionsModalComponent,
} from './copy-questions-from-other-sessions-modal/copy-questions-from-other-sessions-modal.component';
import { InstructorSessionEditPageComponent } from './instructor-session-edit-page.component';
import { InstructorSessionEditPageModule } from './instructor-session-edit-page.module';
import { TemplateQuestionModalComponent } from './template-question-modal/template-question-modal.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import {
  Course,
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackQuestionType,
  FeedbackRankRecipientsQuestionDetails,
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
import { getDefaultDateFormat, getLatestTimeFormat } from '../../../types/datetime-const';
import { CopySessionModalResult } from '../../components/copy-session-modal/copy-session-modal-model';
import { CopySessionModalComponent } from '../../components/copy-session-modal/copy-session-modal.component';
import {
  ExtensionConfirmModalComponent,
} from '../../components/extension-confirm-modal/extension-confirm-modal.component';
import { QuestionEditFormModel } from '../../components/question-edit-form/question-edit-form-model';
import { SessionEditFormModel } from '../../components/session-edit-form/session-edit-form-model';

describe('InstructorSessionEditPageComponent', () => {

  const testCourse1: Course = {
    courseId: 'testId1',
    courseName: 'Test Course 1',
    institute: 'Test Institute',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const testCourse2: Course = {
    courseId: 'testId2',
    courseName: 'Test Course 2',
    institute: 'Test Institute',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const testFeedbackSession: FeedbackSession = {
    courseId: 'testId1',
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
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
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
    isDuplicating: false,
    isDeleting: false,
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
    isDuplicating: false,
    isDeleting: false,
  };

  const sessionEditFormModel: SessionEditFormModel = {
    courseId: 'testId',
    timeZone: 'Asia/Singapore',
    courseName: 'Test Course',
    feedbackSessionName: 'test session',
    instructions: 'Instructions',

    submissionStartTime: getLatestTimeFormat(),
    submissionStartDate: getDefaultDateFormat(),
    submissionEndTime: getLatestTimeFormat(),
    submissionEndDate: getDefaultDateFormat(),
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: getLatestTimeFormat(),
    customSessionVisibleDate: getDefaultDateFormat(),

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: getLatestTimeFormat(),
    customResponseVisibleDate: getDefaultDateFormat(),

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: false,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  const testStudentDeadlines: Record<string, number> = {
    'alice@tmms.com': 1400000000000,
    'bob@tmms.com': 1400000000000,
  };
  const testInstructorDeadlines: Record<string, number> = {
    'testB@example.com': 1300000000000,
    'testA@example.com': 1300000000000,
  };

  let component: InstructorSessionEditPageComponent;
  let fixture: ComponentFixture<InstructorSessionEditPageComponent>;
  let feedbackSessionsService: FeedbackSessionsService;
  let feedbackQuestionsService: FeedbackQuestionsService;
  let courseService: CourseService;
  let studentService: StudentService;
  let instructorService: InstructorService;
  let navigationService: NavigationService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;
  let timeZoneService: TimezoneService;
  let ngbModal: NgbModal;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        HttpClientTestingModule,
        InstructorSessionEditPageModule,
        BrowserAnimationsModule,
        RouterTestingModule,
      ],
      providers: [
        CourseService,
        FeedbackSessionsService,
        FeedbackQuestionsService,
        StudentService,
        InstructorService,
        NavigationService,
        StatusMessageService,
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
    statusMessageService = TestBed.inject(StatusMessageService);
    simpleModalService = TestBed.inject(SimpleModalService);
    timeZoneService = TestBed.inject(TimezoneService);
    ngbModal = TestBed.inject(NgbModal);
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
    component.courseId = testCourse1.courseId;
    component.courseName = testCourse1.courseName;
    component.feedbackSessionName = testFeedbackSession.feedbackSessionName;
    component.sessionEditFormModel = sessionEditFormModel;
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

  it('should snap when feedback question failed to load', () => {
    component.hasLoadingFeedbackQuestionsFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback session is loading', () => {
    component.isLoadingFeedbackSession = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions are loading', () => {
    component.isLoadingFeedbackQuestions = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load correct feedback session for a given API output', () => {
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse1));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));

    component.loadFeedbackSession();

    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(component.courseName).toBe(testCourse1.courseName);
    expect(component.sessionEditFormModel.courseId).toBe(testCourse1.courseId);
    expect(component.sessionEditFormModel.courseName).toBe(testCourse1.courseName);
    expect(component.sessionEditFormModel.feedbackSessionName).toBe(testFeedbackSession.feedbackSessionName);
    expect(component.sessionEditFormModel.timeZone).toBe(testFeedbackSession.timeZone);
    expect(component.sessionEditFormModel.instructions).toBe(testFeedbackSession.instructions);
    expect(component.sessionEditFormModel.sessionVisibleSetting).toBe(testFeedbackSession.sessionVisibleSetting);
    expect(component.sessionEditFormModel.responseVisibleSetting).toBe(testFeedbackSession.responseVisibleSetting);
  });

  it('should display error message when feedback session failed to load', () => {
    component.hasLoadingFeedbackSessionFailed = false;
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse1));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.loadFeedbackSession();

    expect(spy).toHaveBeenCalled();
    expect(component.hasLoadingFeedbackSessionFailed).toBeTruthy();
  });

  it('should load correct feedback session questions', () => {
    const feedbackQuestions: FeedbackQuestions = {
      questions: [testFeedbackQuestion1, testFeedbackQuestion2, testFeedbackQuestion3],
    };
    jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions').mockReturnValue(of(feedbackQuestions));

    component.loadFeedbackQuestions();
    expect(component.questionEditFormModels.length).toBe(3);
    expect(component.questionEditFormModels[0].feedbackQuestionId).toBe('feedback-question-1');
    expect(component.questionEditFormModels[1].feedbackQuestionId).toBe('feedback-question-2');
    expect(component.questionEditFormModels[2].feedbackQuestionId).toBe('feedback-question-3');
  });

  it('should display error message when feedback question failed to load', () => {
    component.hasLoadingFeedbackQuestionsFailed = false;
    jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.loadFeedbackQuestions();

    expect(spy).toHaveBeenCalled();
    expect(component.hasLoadingFeedbackQuestionsFailed).toBeTruthy();
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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));

    component.getAllStudentsOfCourse();

    expect(component.studentsOfCourse.length).toBe(2);
    expect(component.studentsOfCourse[0].name).toBe(testStudent1.name);
    expect(component.emailOfStudentToPreview).toBe(testStudent1.email);
  });

  it('should display error message when failed to get student', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.getAllStudentsOfCourse();

    expect(spy).toHaveBeenCalled();
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
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));

    component.getAllInstructors();
    expect(component.instructorsOfCourse.length).toBe(2);
    expect(component.instructorsOfCourse[0].name).toBe(testInstructor1.name);
    expect(component.emailOfInstructorToPreview).toBe(testInstructor1.email);
  });

  it('should display error message when failed to get instructor', () => {
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.getAllInstructors();

    expect(spy).toHaveBeenCalled();
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
    component.sessionEditFormModel = JSON.parse(JSON.stringify(sessionEditFormModel));
    const navSpy: SpyInstance = jest.spyOn(navigationService, 'navigateWithSuccessMessage').mockImplementation();
    jest.spyOn(feedbackSessionsService, 'moveSessionToRecycleBin').mockReturnValue(of(true));
    component.deleteExistingSessionHandler();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/instructor/sessions',
        'The feedback session has been deleted. You can restore it from the deleted sessions table below.');
  });

  it('should create new question', () => {
    component.isAddingQuestionPanelExpanded = true;
    component.questionEditFormModels = [testQuestionEditFormModel1];
    component.feedbackQuestionModels = new Map().set(testFeedbackQuestion1.feedbackQuestionId, testFeedbackQuestion1);
    jest.spyOn(feedbackQuestionsService, 'createFeedbackQuestion').mockReturnValue(of(testFeedbackQuestion2));
    component.createNewQuestionHandler();
    expect(component.questionEditFormModels.length).toEqual(2);
    expect(component.feedbackQuestionModels.has(testFeedbackQuestion2.feedbackQuestionId)).toEqual(true);
  });

  it('should save existing question and move question to new position', () => {
    const questionEditFormModel: QuestionEditFormModel = JSON.parse(JSON.stringify(testQuestionEditFormModel1));
    questionEditFormModel.questionDescription = 'new description';
    questionEditFormModel.questionNumber = 2;
    const updatedFeedbackQuestion: FeedbackQuestion = JSON.parse(JSON.stringify(testFeedbackQuestion1));
    updatedFeedbackQuestion.questionDescription = 'new description';
    updatedFeedbackQuestion.questionNumber = 2;
    const feedbackQuestionSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'saveFeedbackQuestion')
      .mockReturnValue(of(updatedFeedbackQuestion));
    component.questionEditFormModels = [questionEditFormModel, testQuestionEditFormModel2];
    component.feedbackQuestionModels.set(testFeedbackQuestion1.feedbackQuestionId, testFeedbackQuestion1);
    component.feedbackQuestionModels.set(testFeedbackQuestion2.feedbackQuestionId, testFeedbackQuestion2);

    component.saveExistingQuestionHandler(0);

    expect(feedbackQuestionSpy).toHaveBeenLastCalledWith(testFeedbackQuestion1.feedbackQuestionId, expect.anything());
    expect(component.feedbackQuestionModels.get(testFeedbackQuestion1.feedbackQuestionId))
      .toEqual(updatedFeedbackQuestion);
    expect(component.questionEditFormModels[1].feedbackQuestionId).toEqual(questionEditFormModel.feedbackQuestionId);
    expect(component.questionEditFormModels[0].feedbackQuestionId)
      .toEqual(testQuestionEditFormModel2.feedbackQuestionId);
  });

  it('should discard the changes made to the existing question', () => {
    const questionEditFormModel: QuestionEditFormModel = JSON.parse(JSON.stringify(testQuestionEditFormModel1));
    questionEditFormModel.questionDescription = 'new description';
    component.questionEditFormModels = [questionEditFormModel];
    component.feedbackQuestionModels.set(testFeedbackQuestion1.feedbackQuestionId, testFeedbackQuestion1);

    component.discardExistingQuestionHandler(0);

    expect(component.questionEditFormModels[0].questionDescription)
      .toEqual(testQuestionEditFormModel1.questionDescription);
  });

  it('should duplicate question', () => {
    const duplicateFeedbackQuestion: FeedbackQuestion = JSON.parse(JSON.stringify(testFeedbackQuestion1));
    duplicateFeedbackQuestion.questionNumber = 2;
    duplicateFeedbackQuestion.feedbackQuestionId = 'duplicate question id';
    component.questionEditFormModels = [testQuestionEditFormModel1];
    const feedbackQuestionSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'createFeedbackQuestion')
      .mockReturnValue(of(duplicateFeedbackQuestion));

    component.duplicateCurrentQuestionHandler(0);

    expect(feedbackQuestionSpy).toHaveBeenCalledTimes(1);
    expect(component.feedbackQuestionModels.get(duplicateFeedbackQuestion.feedbackQuestionId))
      .toEqual(duplicateFeedbackQuestion);
  });

  it('should delete existing question', async () => {
    const promise: Promise<void> = Promise.resolve();
    jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef({}, promise));
    component.questionEditFormModels = [testQuestionEditFormModel1];
    component.feedbackQuestionModels.set(testFeedbackQuestion1.feedbackQuestionId, testFeedbackQuestion1);
    const feedbackQuestionSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'deleteFeedbackQuestion')
      .mockReturnValue(of(true));

    component.deleteExistingQuestionHandler(0);
    await promise;

    expect(feedbackQuestionSpy).toHaveBeenLastCalledWith(testQuestionEditFormModel1.feedbackQuestionId);
    expect(component.feedbackQuestionModels.get(testFeedbackQuestion1.feedbackQuestionId)).toBeUndefined();
    expect(component.questionEditFormModels.length).toEqual(0);
  });

  it('should display template questions', async () => {
    const promise: Promise<FeedbackQuestion[]> = Promise.resolve([testFeedbackQuestion1]);
    jest.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, promise));
    const feedbackQuestionSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'createFeedbackQuestion')
      .mockReturnValue(of(testFeedbackQuestion1));

    component.templateQuestionModalHandler();
    await promise;

    expect(ngbModal.open).toHaveBeenCalledWith(TemplateQuestionModalComponent, { windowClass: 'modal-large' });
    expect(feedbackQuestionSpy).toHaveBeenCalledTimes(1);
    expect(component.questionEditFormModels[0].feedbackQuestionId).toEqual(testFeedbackQuestion1.feedbackQuestionId);
    expect(component.feedbackQuestionModels.get(testFeedbackQuestion1.feedbackQuestionId))
      .toEqual(testFeedbackQuestion1);
  });

  it('should copy question from other session', async () => {
    const promise: Promise<FeedbackQuestion[]> = Promise.resolve([testFeedbackQuestion1]);
    jest.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef(
      { questionToCopyCandidates: [] },
      promise,
    ));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor')
      .mockReturnValue(of({ feedbackSessions: [testFeedbackSession] }));
    jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions')
      .mockReturnValue(of({ questions: [testFeedbackQuestion1, testFeedbackQuestion2] }));
    jest.spyOn(feedbackQuestionsService, 'createFeedbackQuestion').mockReturnValue(of(testFeedbackQuestion1));

    component.copyQuestionsFromOtherSessionsHandler();
    await promise;

    expect(ngbModal.open).toHaveBeenCalledWith(CopyQuestionsFromOtherSessionsModalComponent);
    expect(component.questionEditFormModels.length).toEqual(1);
    expect(component.feedbackQuestionModels.get(testFeedbackQuestion1.feedbackQuestionId))
      .toEqual(testFeedbackQuestion1);
  });

  it('should open modal and copy current session', async () => {
    const promise: Promise<CopySessionModalResult> = Promise.resolve({
      sessionToCopyCourseId: 'testId1',
      newFeedbackSessionName: 'new feedback session',
      copyToCourseList: ['testId2'],
    });

    const mockModalRef: any = createMockNgbModalRef({
      newFeedbackSessionName: '',
      courseCandidates: [],
      sessionToCopyCourseId: '',
    }, promise);
    const copiedFeedbackSession: FeedbackSession = JSON.parse(JSON.stringify(testFeedbackSession));
    copiedFeedbackSession.courseId = 'testId2';

    component.feedbackSessionName = testFeedbackSession.feedbackSessionName;
    component.courseId = testCourse1.courseId;
    jest.spyOn(courseService, 'getInstructorCoursesThatAreActive').mockReturnValue(of({ courses: [testCourse2] }));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(feedbackSessionsService, 'createFeedbackSession').mockReturnValue(of(copiedFeedbackSession));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);
    jest.spyOn(InstructorSessionEditPageComponent.prototype, 'createSessionCopyRequestsFromModal')
      .mockReturnValue([of(copiedFeedbackSession)]);
    const navSpy: SpyInstance = jest.spyOn(navigationService, 'navigateWithSuccessMessage').mockImplementation();

    component.copyCurrentSession();
    await promise;

    expect(ngbModal.open).toHaveBeenCalledWith(CopySessionModalComponent);
    expect(mockModalRef.componentInstance.newFeedbackSessionName).toEqual(testFeedbackSession.feedbackSessionName);
    expect(mockModalRef.componentInstance.courseCandidates[0]).toEqual(testCourse2);
    expect(mockModalRef.componentInstance.sessionToCopyCourseId).toEqual(testCourse1.courseId);
    expect(navSpy).toHaveBeenLastCalledWith('/web/instructor/sessions/edit',
        'The feedback session has been copied. Please modify settings/questions as necessary.',
        { courseid: 'testId2', fsname: 'Test Session' });
  });

  it('should open danger modal if session end time updates end time after any extensions deadline', () => {
    jest.spyOn(ngbModal, 'open');
    jest.spyOn(timeZoneService, 'resolveLocalDateTime').mockReturnValue(testFeedbackSession.submissionEndTimestamp);
    const validateSpy = jest.spyOn(InstructorSessionEditPageComponent.prototype,
      'deleteDeadlineExtensionsHandler');
    component.studentDeadlines = testStudentDeadlines;
    component.instructorDeadlines = testInstructorDeadlines;
    component.sessionEditFormModel = sessionEditFormModel;
    component.editExistingSessionHandler();
    expect(validateSpy).toHaveBeenCalledWith(testFeedbackSession.submissionEndTimestamp);
    expect(ngbModal.open).toHaveBeenCalledWith(ExtensionConfirmModalComponent);
  });
});
