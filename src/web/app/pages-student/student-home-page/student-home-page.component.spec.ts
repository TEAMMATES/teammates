import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { StudentHomePageComponent } from './student-home-page.component';

import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { ResponseStatusPipe } from '../../pipes/session-response-status.pipe';
import { SubmissionStatusPipe } from '../../pipes/session-submission-status.pipe';

describe('StudentHomePageComponent', () => {
  let component: StudentHomePageComponent;
  let fixture: ComponentFixture<StudentHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        StudentHomePageComponent,
        ResponseStatusPipe,
        SubmissionStatusPipe,
      ],
      imports: [
        HttpClientTestingModule,
        NgbModule,
        RouterTestingModule,
        TeammatesCommonModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to student course page to view the corresponding team', () => {
    const studentCourseA: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems I',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    const studentCourseB: any = {
      course: {
        courseId: 'CS3282',
        courseName: 'Thematic Systems II',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    component.courses = [studentCourseA, studentCourseB];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: any = fixture.debugElement.nativeElement.querySelector('#view-team-btn-0').getAttribute('href');
    const href2: any = fixture.debugElement.nativeElement.querySelector('#view-team-btn-1').getAttribute('href');
    expect(href1).toEqual('/web/student/course?courseid=CS3281');
    expect(href2).toEqual('/web/student/course?courseid=CS3282');
  });

  it('should navigate to student session result page to view responses', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: any = fixture.debugElement.nativeElement.querySelector('#view-responses-btn-0').getAttribute('href');
    const href2: any = fixture.debugElement.nativeElement.querySelector('#view-responses-btn-1').getAttribute('href');
    expect(href1).toEqual('/web/student/sessions/result?courseid=CS1231&fsname=First%20Session');
    expect(href2).toEqual('/web/student/sessions/result?courseid=CS1231&fsname=Second%20Session');
  });

  // start/edit/view submission button share the same router link and query params
  // here we only have to test one of them
  it('should navigate to student session submission page for viewing', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: any = fixture.debugElement.nativeElement.querySelector('#view-submit-btn-0').getAttribute('href');
    const href2: any = fixture.debugElement.nativeElement.querySelector('#view-submit-btn-1').getAttribute('href');
    expect(href1).toEqual('/web/student/sessions/submission?courseid=CS1231&fsname=First%20Session');
    expect(href2).toEqual('/web/student/sessions/submission?courseid=CS1231&fsname=Second%20Session');
  });

  it('should sort courses by their IDs', () => {
    const studentCourseA: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    const studentCourseB: any = {
      course: {
        courseId: 'LSM1306',
        courseName: 'Forensic Science',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'Third Session',
            courseId: 'LSM1306',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Fourth Session',
            courseId: 'LSM1306',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    const studentCourseC: any = {
      course: {
        courseId: 'MA1521',
        courseName: 'Calculus for Computing',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'Fifth Session',
            courseId: 'MA1521',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Sixth Session',
            courseId: 'MA1521',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    component.courses = [studentCourseB, studentCourseC, studentCourseA];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-id-btn');
    button.click();

    expect(component.courses[0].course.courseId).toEqual(studentCourseA.course.courseId);
    expect(component.courses[1].course.courseId).toEqual(studentCourseB.course.courseId);
    expect(component.courses[2].course.courseId).toEqual(studentCourseC.course.courseId);
  });

  it('should sort courses by their names', () => {
    const studentCourseA: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    const studentCourseB: any = {
      course: {
        courseId: 'LSM1306',
        courseName: 'Forensic Science',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'Third Session',
            courseId: 'LSM1306',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Fourth Session',
            courseId: 'LSM1306',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    const studentCourseC: any = {
      course: {
        courseId: 'MA1521',
        courseName: 'Calculus for Computing',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'Fifth Session',
            courseId: 'MA1521',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Sixth Session',
            courseId: 'MA1521',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    component.courses = [studentCourseA, studentCourseB, studentCourseC];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-name-btn');
    button.click();

    expect(component.courses[0].course.courseId).toEqual(studentCourseC.course.courseId);
    expect(component.courses[1].course.courseId).toEqual(studentCourseA.course.courseId);
    expect(component.courses[2].course.courseId).toEqual(studentCourseB.course.courseId);
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no course', () => {
    component.courses = [];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback sessions', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };
    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback session over 2 courses', () => {
    const studentCourseA: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems I',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    const studentCourseB: any = {
      course: {
        courseId: 'CS3282',
        courseName: 'Thematic Systems II',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    component.courses = [studentCourseA, studentCourseB];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback sessions', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS2103',
        courseName: 'Software Engineering',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS2103',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: true,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS2103',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: false,
        },
      ],
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with all feedback sessions over 2 courses', () => {
    const studentCourseA: any = {
      course: {
        courseId: 'CS2103',
        courseName: 'Software Engineering',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'First Session',
            courseId: 'CS2103',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: true,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS2103',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: false,
        },
      ],
    };

    const studentCourseB: any = {
      course: {
        courseId: 'CS2102',
        courseName: 'Databases',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionName: 'Third Session',
            courseId: 'CS2102',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionName: 'Fourth Session',
            courseId: 'CS2102',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000,
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
    };

    component.courses = [studentCourseA, studentCourseB];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    component.isCoursesLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there is course loading failed', () => {
    component.isCoursesLoading = false;
    component.hasCoursesLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
