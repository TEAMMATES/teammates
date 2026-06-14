import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { InstructorSessionSendRemindersPageComponent } from './instructor-session-send-reminders-page.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import {
  Course,
  CourseView,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  FeedbackSessionView,
  Instructor,
  Instructors,
  JoinState,
  MessageOutput,
  Student,
  Students,
} from '../../../types/api-output';
import { ResponseVisibleSetting, SessionVisibleSetting } from '../../../types/api-request';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';

describe('InstructorSessionSendRemindersPageComponent', () => {
  const testCourse: Course = {
    courseId: 'exampleId',
    courseName: 'Example Course',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const testFeedbackSession: FeedbackSession = {
    feedbackSessionId: '23901a20-48bb-4fcc-a3fb-0b2489b07886',
    courseId: 'exampleId',
    timeZone: 'UTC',
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
  };

  const testCourseView: CourseView = {
    course: testCourse,
  };

  const testFeedbackSessionView: FeedbackSessionView = {
    feedbackSession: testFeedbackSession,
  };

  const testStudent1: Student = {
    userId: 'alice-id',
    email: 'alice@tmms.com',
    courseId: 'exampleId',
    courseName: 'Example Course',
    institute: 'Test Institute',
    name: 'Alice',
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Section 1',
    sectionId: 'section-1',
  };
  const testStudent2: Student = {
    userId: 'bob-id',
    email: 'bob@tmms.com',
    courseId: 'exampleId',
    courseName: 'Example Course',
    institute: 'Test Institute',
    name: 'Bob',
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Section 1',
    sectionId: 'section-1',
  };
  const students: Students = {
    students: [testStudent1, testStudent2],
  };

  const testInstructor1: Instructor = {
    userId: 'tester1-id',
    name: 'tester1',
    email: 'tester1@tester.com',
    googleId: 'instructor-google-id',
    courseId: 'exampleId',
    courseName: 'Example Course',
    institute: 'institute',
    joinState: JoinState.JOINED,
  };
  const instructors: Instructors = {
    instructors: [testInstructor1],
  };

  const testFeedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet = {
    studentGivers: [testStudent2.userId],
    instructorGivers: [],
    studentNonGivers: [testStudent1.userId],
    instructorNonGivers: [testInstructor1.userId],
  };

  const testTimeString = '5 Apr 2000 2:00:00';

  const studentModelBuilder = createBuilder<StudentListInfoTableRowModel>({
    id: 'd6a66c69-6ea6-4d7b-84f0-7701277503e4',
    email: 'student@gmail.com',
    name: 'Student',
    teamName: 'Team A',
    sectionName: 'Section 1',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const instructorModelBuilder = createBuilder<InstructorListInfoTableRowModel>({
    id: '76a78858-f8bd-4b30-81b7-7a0c541cc0b0',
    email: 'instructor@gmail.com',
    name: 'Instructor',
    hasSubmittedSession: false,
    isSelected: false,
  });

  let component: InstructorSessionSendRemindersPageComponent;
  let fixture: ComponentFixture<InstructorSessionSendRemindersPageComponent>;
  let studentService: StudentService;
  let instructorService: InstructorService;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;
  let navigationService: NavigationService;
  let statusMessageService: StatusMessageService;
  let timezoneService: TimezoneService;

  const mockServicesWithData = (): void => {
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      of(testFeedbackSessionSubmittedGiverSet),
    );
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              fsid: testFeedbackSession.feedbackSessionId,
              preselectnonsubmitters: 'false',
            }),
          },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorSessionSendRemindersPageComponent);
    component = fixture.componentInstance;
    studentService = TestBed.inject(StudentService);
    instructorService = TestBed.inject(InstructorService);
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    navigationService = TestBed.inject(NavigationService);
    statusMessageService = TestBed.inject(StatusMessageService);
    timezoneService = TestBed.inject(TimezoneService);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should snap while loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with students and instructors loaded', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    mockServicesWithData();

    component.ngOnInit();

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there are no participants to remind', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of({ students: [] }));
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of({ instructors: [] }));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      of(testFeedbackSessionSubmittedGiverSet),
    );

    component.ngOnInit();

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load students and instructors with their submission status', () => {
    mockServicesWithData();

    component.ngOnInit();

    expect(component.courseName).toBe(testCourse.courseName);
    expect(component.feedbackSessionName).toBe(testFeedbackSession.feedbackSessionName);
    expect(component.studentListInfoTableRowModels).toStrictEqual([
      {
        id: testStudent1.userId,
        email: testStudent1.email,
        name: testStudent1.name,
        teamName: testStudent1.teamName,
        sectionName: testStudent1.sectionName,
        hasSubmittedSession: false,
        isSelected: false,
      },
      {
        id: testStudent2.userId,
        email: testStudent2.email,
        name: testStudent2.name,
        teamName: testStudent2.teamName,
        sectionName: testStudent2.sectionName,
        hasSubmittedSession: true,
        isSelected: false,
      },
    ]);
    expect(component.instructorListInfoTableRowModels).toStrictEqual([
      {
        id: testInstructor1.userId,
        email: testInstructor1.email,
        name: testInstructor1.name,
        hasSubmittedSession: false,
        isSelected: false,
      },
    ]);
  });

  it('should preselect non-submitters when preselectnonsubmitters is true', () => {
    mockServicesWithData();
    component.preselectNonSubmitters = true;

    component.loadFeedbackSessionAndRespondents();

    expect(component.studentListInfoTableRowModels.find((m) => m.id === testStudent1.userId)?.isSelected).toBeTruthy();
    expect(component.studentListInfoTableRowModels.find((m) => m.id === testStudent2.userId)?.isSelected).toBeFalsy();
    expect(
      component.instructorListInfoTableRowModels.find((m) => m.id === testInstructor1.userId)?.isSelected,
    ).toBeTruthy();
  });

  it('should show error toast and set failed flag if loading fails', () => {
    const showErrorToastSpy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );

    component.ngOnInit();

    expect(component.hasLoadingFailed).toBeTruthy();
    expect(component.isLoading).toBeFalsy();
    expect(showErrorToastSpy).toHaveBeenCalledWith('This is a test message');
  });

  it('changeSelectionStatusForAllStudentsHandler: should select all students', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.isSelected(false).build(),
      studentModelBuilder.isSelected(true).build(),
    ];

    component.changeSelectionStatusForAllStudentsHandler(true);

    expect(component.studentListInfoTableRowModels.every((m) => m.isSelected)).toBeTruthy();
  });

  it('changeSelectionStatusForAllYetSubmittedStudentsHandler: should only select non-submitted students', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
      studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
    ];

    component.changeSelectionStatusForAllYetSubmittedStudentsHandler(true);

    expect(component.studentListInfoTableRowModels[0].isSelected).toBeTruthy();
    expect(component.studentListInfoTableRowModels[1].isSelected).toBeFalsy();
  });

  it('changeSelectionStatusForAllInstructorsHandler: should select all instructors', () => {
    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.isSelected(false).build(),
      instructorModelBuilder.isSelected(true).build(),
    ];

    component.changeSelectionStatusForAllInstructorsHandler(true);

    expect(component.instructorListInfoTableRowModels.every((m) => m.isSelected)).toBeTruthy();
  });

  it('changeSelectionStatusForAllYetSubmittedInstructorsHandler: should only select non-submitted instructors', () => {
    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
      instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
    ];

    component.changeSelectionStatusForAllYetSubmittedInstructorsHandler(true);

    expect(component.instructorListInfoTableRowModels[0].isSelected).toBeTruthy();
    expect(component.instructorListInfoTableRowModels[1].isSelected).toBeFalsy();
  });

  it('changeSelectionStatusForSendingCopyToInstructorHandler: should toggle isSendingCopyToInstructor', () => {
    component.isSendingCopyToInstructor = true;

    component.changeSelectionStatusForSendingCopyToInstructorHandler(false);

    expect(component.isSendingCopyToInstructor).toBeFalsy();
  });

  it('isAllYetToSubmitStudentsSelected: should be false when all students have submitted', () => {
    component.studentListInfoTableRowModels = [studentModelBuilder.isSelected(true).hasSubmittedSession(true).build()];

    expect(component.isAllYetToSubmitStudentsSelected).toBeFalsy();
  });

  it('hasSelectedRespondents: should be true when at least one respondent is selected', () => {
    component.studentListInfoTableRowModels = [studentModelBuilder.isSelected(false).build()];
    component.instructorListInfoTableRowModels = [instructorModelBuilder.isSelected(true).build()];

    expect(component.hasSelectedRespondents).toBeTruthy();
  });

  it('sendReminders: should remind selected respondents and navigate back with a success message', () => {
    component.feedbackSessionId = testFeedbackSession.feedbackSessionId;
    component.isSendingCopyToInstructor = true;
    component.studentListInfoTableRowModels = [
      studentModelBuilder.id('alice-id').isSelected(true).build(),
      studentModelBuilder.id('bob-id').isSelected(false).build(),
    ];
    component.instructorListInfoTableRowModels = [instructorModelBuilder.id('tester1-id').isSelected(true).build()];

    const remindSpy = vi
      .spyOn(feedbackSessionsService, 'remindFeedbackSessionSubmissionForRespondents')
      .mockReturnValue(of({} as MessageOutput));
    const navigateBackSpy = vi.spyOn(navigationService, 'navigateBackWithSuccessMessage').mockImplementation(() => {});

    component.sendReminders();

    expect(remindSpy).toHaveBeenCalledWith(testFeedbackSession.feedbackSessionId, {
      usersToRemind: ['alice-id', 'tester1-id'],
      isSendingCopyToInstructor: true,
    });
    expect(navigateBackSpy).toHaveBeenCalledWith(expect.any(String));
  });

  it('sendReminders: should show error toast if reminder fails', () => {
    const showErrorToastSpy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});
    vi.spyOn(feedbackSessionsService, 'remindFeedbackSessionSubmissionForRespondents').mockReturnValue(
      throwError(() => ({
        error: { message: 'This is a test message' },
      })),
    );

    component.sendReminders();

    expect(showErrorToastSpy).toHaveBeenCalledWith('This is a test message');
    expect(component.isSendingReminders).toBeFalsy();
  });

  it('cancel: should navigate back to the previous page', () => {
    const navigateBackSpy = vi.spyOn(navigationService, 'navigateBack').mockImplementation(() => {});

    component.cancel();

    expect(navigateBackSpy).toHaveBeenCalled();
  });
});
