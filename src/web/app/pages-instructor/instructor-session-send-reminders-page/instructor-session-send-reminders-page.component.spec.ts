import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { InstructorSessionSendRemindersPageComponent } from './instructor-session-send-reminders-page.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  Course,
  CourseView,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  FeedbackSessionView,
  Instructors,
  JoinState,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Students,
} from '../../../types/api-output';

describe('InstructorSessionSendRemindersPageComponent', () => {
  const testCourse: Course = {
    courseId: 'CS2103',
    courseName: 'Software Engineering',
    institute: 'NUS',
    country: 'SG',
    instituteId: 'nus',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };

  const testCourseView: CourseView = {
    course: testCourse,
  };

  const testFeedbackSession: FeedbackSession = {
    feedbackSessionId: 'session-id',
    courseId: testCourse.courseId,
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'Midterm Feedback',
    instructions: 'Instructions',
    submissionStartTimestamp: 1000,
    submissionEndTimestamp: 2000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testFeedbackSessionView: FeedbackSessionView = {
    feedbackSession: testFeedbackSession,
  };

  const testStudents: Students = {
    students: [
      {
        userId: 'student-1',
        email: 'alice@example.com',
        courseId: testCourse.courseId,
        courseName: testCourse.courseName,
        institute: testCourse.institute,
        name: 'Alice',
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Section 1',
        sectionId: 'section-1',
      },
      {
        userId: 'student-2',
        email: 'bob@example.com',
        courseId: testCourse.courseId,
        courseName: testCourse.courseName,
        institute: testCourse.institute,
        name: 'Bob',
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Section 1',
        sectionId: 'section-1',
      },
    ],
  };

  const testInstructors: Instructors = {
    instructors: [
      {
        userId: 'instructor-1',
        name: 'Prof Alice',
        email: 'prof@example.com',
        courseId: testCourse.courseId,
        courseName: testCourse.courseName,
        institute: testCourse.institute,
        joinState: JoinState.JOINED,
      },
    ],
  };

  const testSubmittedGiverSet: FeedbackSessionSubmittedGiverSet = {
    studentGivers: ['student-2'],
    instructorGivers: [],
    studentNonGivers: ['student-1'],
    instructorNonGivers: ['instructor-1'],
  };

  let component: InstructorSessionSendRemindersPageComponent;
  let fixture: ComponentFixture<InstructorSessionSendRemindersPageComponent>;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;
  let instructorService: InstructorService;
  let navigationService: NavigationService;
  let statusMessageService: StatusMessageService;
  let studentService: StudentService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    instructorService = TestBed.inject(InstructorService);
    navigationService = TestBed.inject(NavigationService);
    statusMessageService = TestBed.inject(StatusMessageService);
    studentService = TestBed.inject(StudentService);
  });

  function getCheckbox(id: string): HTMLInputElement {
    return fixture.nativeElement.querySelector(`#${id}`) as HTMLInputElement;
  }

  function createComponent(preselectNonSubmitters = 'false', returnUrl = '/web/instructor/home'): void {
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(studentService, 'getStudents').mockReturnValue(of(testStudents));
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(testInstructors));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(of(testSubmittedGiverSet));

    fixture = TestBed.createComponent(InstructorSessionSendRemindersPageComponent);
    component = fixture.componentInstance;
    component.feedbackSessionId = testFeedbackSession.feedbackSessionId;
    component.preselectNonSubmitters = preselectNonSubmitters;
    component.returnUrl = returnUrl;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should preselect only non-submitters when requested', () => {
    createComponent('true');

    expect(component.studentListInfoTableRowModels).toEqual([
      expect.objectContaining({ id: 'student-1', isSelected: true, hasSubmittedSession: false }),
      expect.objectContaining({ id: 'student-2', isSelected: false, hasSubmittedSession: true }),
    ]);
    expect(component.instructorListInfoTableRowModels).toEqual([
      expect.objectContaining({ id: 'instructor-1', isSelected: true, hasSubmittedSession: false }),
    ]);
  });

  it('should select all students when the student selection handler is triggered', () => {
    createComponent();
    component.studentListInfoTableRowModels = [
      { ...component.studentListInfoTableRowModels[0], isSelected: true },
      { ...component.studentListInfoTableRowModels[1], isSelected: false },
    ];

    component.changeSelectionStatusForAllStudentsHandler(true);

    expect(component.studentListInfoTableRowModels.map((model) => model.isSelected)).toEqual([true, true]);
  });

  it('should deselect all students when the student selection handler is triggered with false', () => {
    createComponent();
    component.studentListInfoTableRowModels = component.studentListInfoTableRowModels.map((model) => ({
      ...model,
      isSelected: true,
    }));

    component.changeSelectionStatusForAllStudentsHandler(false);

    expect(component.studentListInfoTableRowModels.map((model) => model.isSelected)).toEqual([false, false]);
  });

  it('should select only students who have not submitted when the corresponding handler is triggered', () => {
    createComponent();
    component.studentListInfoTableRowModels = [
      { ...component.studentListInfoTableRowModels[0], isSelected: false, hasSubmittedSession: false },
      { ...component.studentListInfoTableRowModels[1], isSelected: false, hasSubmittedSession: true },
    ];

    component.changeSelectionStatusForAllYetSubmittedStudentsHandler(true);

    expect(component.studentListInfoTableRowModels.map((model) => model.isSelected)).toEqual([true, false]);
  });

  it('should select all instructors when the instructor selection handler is triggered', () => {
    createComponent();
    component.instructorListInfoTableRowModels = [
      { ...component.instructorListInfoTableRowModels[0], isSelected: false },
      { ...component.instructorListInfoTableRowModels[0], id: 'instructor-2', email: 'prof2@example.com', isSelected: true },
    ];

    component.changeSelectionStatusForAllInstructorsHandler(true);

    expect(component.instructorListInfoTableRowModels.map((model) => model.isSelected)).toEqual([true, true]);
  });

  it('should deselect all instructors when the instructor selection handler is triggered with false', () => {
    createComponent();
    component.instructorListInfoTableRowModels = [
      { ...component.instructorListInfoTableRowModels[0], isSelected: true },
      { ...component.instructorListInfoTableRowModels[0], id: 'instructor-2', email: 'prof2@example.com', isSelected: true },
    ];

    component.changeSelectionStatusForAllInstructorsHandler(false);

    expect(component.instructorListInfoTableRowModels.map((model) => model.isSelected)).toEqual([false, false]);
  });

  it('should select only instructors who have not submitted when the corresponding handler is triggered', () => {
    createComponent();
    component.instructorListInfoTableRowModels = [
      { ...component.instructorListInfoTableRowModels[0], isSelected: false, hasSubmittedSession: false },
      { ...component.instructorListInfoTableRowModels[0], id: 'instructor-2', email: 'prof2@example.com', isSelected: false, hasSubmittedSession: true },
    ];

    component.changeSelectionStatusForAllYetSubmittedInstructorsHandler(true);

    expect(component.instructorListInfoTableRowModels.map((model) => model.isSelected)).toEqual([true, false]);
  });

  it('should toggle sending a copy to the instructor when the handler is triggered', async () => {
    createComponent();
    await vi.waitFor(() => expect(getCheckbox('sendCopyToIns').checked).toBe(true));

    component.changeSelectionStatusForSendingCopyToInstructorHandler(false);
    fixture.detectChanges();

    expect(component.isSendingCopyToInstructor).toBe(false);
    await vi.waitFor(() => expect(getCheckbox('sendCopyToIns').checked).toBe(false));
  });

  it('should reflect student selection state in the selection button computed state', async () => {
    createComponent();
    component.studentListInfoTableRowModels = [
      { ...component.studentListInfoTableRowModels[0], isSelected: true, hasSubmittedSession: false },
      { ...component.studentListInfoTableRowModels[1], isSelected: false, hasSubmittedSession: true },
    ];
    fixture.detectChanges();

    expect(component.isAllStudentsSelected).toBe(false);
    expect(component.isAllYetToSubmitStudentsSelected).toBe(true);
    await vi.waitFor(() => expect(getCheckbox('remindAllStu').checked).toBe(false));
    await vi.waitFor(() => expect(getCheckbox('remindNotSubmittedStu').checked).toBe(true));
  });

  it('should reflect instructor selection state in the selection button computed state', async () => {
    createComponent();
    component.instructorListInfoTableRowModels = [
      { ...component.instructorListInfoTableRowModels[0], isSelected: true, hasSubmittedSession: false },
      { ...component.instructorListInfoTableRowModels[0], id: 'instructor-2', email: 'prof2@example.com', isSelected: false, hasSubmittedSession: true },
    ];
    fixture.detectChanges();

    expect(component.isAllInstructorsSelected).toBe(false);
    expect(component.isAllYetToSubmitInstructorsSelected).toBe(true);
    await vi.waitFor(() => expect(getCheckbox('remindAllIns').checked).toBe(false));
    await vi.waitFor(() => expect(getCheckbox('remindNotSubmittedIns').checked).toBe(true));
  });

  it('should check the select-all student checkbox when all students are selected', async () => {
    createComponent();
    component.studentListInfoTableRowModels = component.studentListInfoTableRowModels.map((model) => ({
      ...model,
      isSelected: true,
    }));
    fixture.detectChanges();

    expect(component.isAllStudentsSelected).toBe(true);
    await vi.waitFor(() => expect(getCheckbox('remindAllStu').checked).toBe(true));
  });

  it('should check the select-all instructor checkbox when all instructors are selected', async () => {
    createComponent();
    component.instructorListInfoTableRowModels = component.instructorListInfoTableRowModels.map((model) => ({
      ...model,
      isSelected: true,
    }));
    fixture.detectChanges();

    expect(component.isAllInstructorsSelected).toBe(true);
    await vi.waitFor(() => expect(getCheckbox('remindAllIns').checked).toBe(true));
  });

  it('should send reminders and navigate back to the return URL', () => {
    createComponent('true', '/web/instructor/sessions');
    const remindSpy = vi
      .spyOn(feedbackSessionsService, 'remindFeedbackSessionSubmissionForRespondents')
      .mockReturnValue(of({ message: '' }));
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});
    const navigateSpy = vi.spyOn(navigationService, 'navigateByURL').mockResolvedValue(true);

    component.sendReminders();

    expect(remindSpy).toHaveBeenCalledWith(testFeedbackSession.feedbackSessionId, {
      usersToRemind: ['student-1', 'instructor-1'],
      isSendingCopyToInstructor: true,
    });
    expect(successSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith('/web/instructor/sessions');
  });

  it('should fall back to the report page when returnUrl is invalid', () => {
    createComponent('false', 'https://example.com');
    const navigateSpy = vi.spyOn(navigationService, 'navigateByURL').mockResolvedValue(true);

    component.cancel();

    expect(navigateSpy).toHaveBeenCalledWith('/web/instructor/sessions/session-id/report');
  });

  it('should show an error when loading fails', () => {
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(
      throwError(() => ({ error: { message: 'load failed' } })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});

    fixture = TestBed.createComponent(InstructorSessionSendRemindersPageComponent);
    component = fixture.componentInstance;
    component.feedbackSessionId = testFeedbackSession.feedbackSessionId;
    fixture.detectChanges();

    expect(component.hasLoadingDataFailed).toBeTruthy();
    expect(errorSpy).toHaveBeenCalledWith('load failed');
  });
});
