import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import SpyInstance = jest.SpyInstance;
import { CopyCourseModalComponent } from './copy-course-modal.component';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';

describe('CopyCourseModalComponent', () => {
  let component: CopyCourseModalComponent;
  let ngbActiveModal: NgbActiveModal;
  let timezoneService: TimezoneService;
  let statusMessageService: StatusMessageService;
  let fixture: ComponentFixture<CopyCourseModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CopyCourseModalComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [NgbActiveModal, TimezoneService, StatusMessageService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyCourseModalComponent);
    timezoneService = TestBed.inject(TimezoneService);
    statusMessageService = TestBed.inject(StatusMessageService);
    ngbActiveModal = TestBed.inject(NgbActiveModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    component.timezones = [{
      id: 'Asia/Singapore',
      offset: 'UTC +08:00',
    }, {
      id: 'UTC',
      offset: 'UTC',
    }];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some course id', () => {
    component.timezones = [{
      id: 'Asia/Singapore',
      offset: 'UTC +08:00',
    }, {
      id: 'UTC',
      offset: 'UTC',
    }];
    component.newCourseId = 'Test02';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when copying from other sessions', () => {
    component.timezones = [{
      id: 'Asia/Singapore',
      offset: 'UTC +08:00',
    }, {
      id: 'UTC',
      offset: 'UTC',
    }];
    component.isCopyFromOtherSession = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should enable copy button after new courseId is provided', () => {
    component.newCourseId = 'Test02';
    component.newCourseName = 'TestName02';
    component.newCourseInstitute = 'Test institute';
    fixture.detectChanges();
    const copyButton: any = fixture.debugElement.query(By.css('#btn-confirm-copy-course'));
    expect(copyButton.nativeElement.disabled).toBeFalsy();
  });

  it('should disable copy if courseId is empty', () => {
    component.newCourseId = '';
    component.newCourseName = 'TestName02';
    fixture.detectChanges();
    const copyButton: any = fixture.debugElement.query(By.css('#btn-confirm-copy-course'));
    expect(copyButton.nativeElement.disabled).toBeTruthy();
  });

  it('should toggle selection', () => {
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
      isClosingSoonEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
      studentDeadlines: {},
      instructorDeadlines: {},
    };
    component.selectedFeedbackSessions.add(testFeedbackSession);
    fixture.detectChanges();
    component.toggleSelection(testFeedbackSession);
    expect(component.selectedFeedbackSessions.has(testFeedbackSession)).toEqual(false);
  });

  it('should set newCourseInstitute if institutes array is not empty', () => {
    const testCourses: Course[] = [
      {
        courseId: 'testId1',
        courseName: 'testCourse1',
        timeZone: 'Asia/Singapore',
        institute: 'Institute 1',
        creationTimestamp: 1000000000000,
        deletionTimestamp: 1500000000000,
      },
      {
        courseId: 'testId2',
        courseName: 'testCourse2',
        timeZone: 'Asia/Singapore',
        institute: 'Institute 2',
        creationTimestamp: 1000000000000,
        deletionTimestamp: 1500000000000,
      },
    ];
    component.allCourses = testCourses;
    component.ngOnInit();

    expect(component.newCourseInstitute).toBe('Institute 1');
  });

  it('should set the timezones property correctly', () => {
    const mockTzOffsets = {
      Tz1: 120, // UTC+02:00
      Tz2: -180, // UTC-03:00
      Tz3: 0,
    };

    jest.spyOn(timezoneService, 'getTzOffsets').mockReturnValue(mockTzOffsets);

    component.ngOnInit();

    expect(component.timezones).toEqual([
      { id: 'Tz1', offset: 'UTC +02:00' },
      { id: 'Tz2', offset: 'UTC -03:00' },
      { id: 'Tz3', offset: 'UTC' },
    ]);
  });

  it('should call showErrorToast when copying with no new courseId and name', () => {
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');
    component.copy();
    expect(spyStatusMessageService)
      .toHaveBeenCalledWith('Please make sure you have filled in both Course ID and Name before adding the course!');
  });

  it('should call showErrorToast when newCourseId is a duplicate', () => {
    const testCourses: Course[] = [
      {
        courseId: 'testId1',
        courseName: 'testCourse1',
        timeZone: 'Asia/Singapore',
        institute: 'Institute 1',
        creationTimestamp: 1000000000000,
        deletionTimestamp: 1500000000000,
      },
      {
        courseId: 'testId2',
        courseName: 'testCourse2',
        timeZone: 'Asia/Singapore',
        institute: 'Institute 2',
        creationTimestamp: 1000000000000,
        deletionTimestamp: 1500000000000,
      },
    ];
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');
    component.allCourses = testCourses;
    component.newCourseId = 'testId1';
    component.newCourseName = 'testName';
    component.copy();
    expect(spyStatusMessageService).toHaveBeenCalledWith('The course ID testId1 already exists.');
  });

  it('should call close with the result when succesfully copying a course', () => {
    const activeModalSpy: SpyInstance = jest.spyOn(ngbActiveModal, 'close');
    component.newCourseId = 'testId2';
    component.newCourseName = 'testName';
    component.newTimezone = 'Asia/Singapore';
    component.copy();
    expect(activeModalSpy).toHaveBeenCalledWith({
      newCourseId: 'testId2',
      newCourseName: 'testName',
      newCourseInstitute: '',
      newTimeZone: 'Asia/Singapore',
      oldCourseId: '',
      selectedFeedbackSessionList: new Set(),
      totalNumberOfSessions: 0,
    });
  });

  it('should toggle selection of all feedbacksessions correctly', () => {
    const testFeedbackSession1: FeedbackSession = {
      courseId: 'testId',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Test Session 1',
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
    const testFeedbackSession2: FeedbackSession = {
      courseId: 'testId',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Test Session 2',
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
    const testFeedbackSessions = [testFeedbackSession1, testFeedbackSession2];
    component.courseToFeedbackSession = {
      testCourseId: testFeedbackSessions,
    };
    component.oldCourseId = 'testCourseId';

    // selectedFeedbackSessions is empty
    component.toggleSelectionForAll();
    expect(component.selectedFeedbackSessions).toEqual(new Set(testFeedbackSessions));
    // selectedFeedbackSessions has all the feedback sessions of the old course
    component.toggleSelectionForAll();
    expect(component.selectedFeedbackSessions.size).toBe(0);
    // selectedFeedbackSessions is partially filled
    component.selectedFeedbackSessions = new Set([testFeedbackSession1]);
    component.toggleSelectionForAll();
    expect(component.selectedFeedbackSessions).toEqual(new Set(testFeedbackSessions));
  });

  it('should set newTimezone correctly when onAutoDetectTimezone is called', () => {
    const mockTimezone = 'timezone';
    jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue(mockTimezone);
    component.onAutoDetectTimezone();
    expect(component.newTimezone).toBe(mockTimezone);
  });

  it('should clear selectedFeedbackSessions when onSelectCourseChange is called', () => {
    const testFeedbackSession: FeedbackSession = {
      courseId: 'testId2',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Test Session 2',
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
    component.oldCourseId = 'testId1';
    component.courseToFeedbackSession = {
      testId2: [testFeedbackSession],
    };
    component.selectedFeedbackSessions = new Set([testFeedbackSession]);
    let emittedCourseId;
    component.fetchFeedbackSessionsEvent.subscribe((emittedValue) => { emittedCourseId = emittedValue; });
    component.onSelectCourseChange();
    expect(component.selectedFeedbackSessions.size).toBe(0);
    expect(emittedCourseId).toEqual('testId1');
  });
});
