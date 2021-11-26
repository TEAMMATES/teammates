import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EventEmitter } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';

import { CopyCourseModalComponent } from '../../../../app/components/copy-course-modal/copy-course-modal.component';
import { CourseService } from '../../../../services/course.service';
import { FeedbackSessionsService } from '../../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { AddCourseFormComponent } from './add-course-form.component';

describe('AddCourseFormComponent', () => {
  let component: AddCourseFormComponent;
  let fixture: ComponentFixture<AddCourseFormComponent>;
  let feedbackSessionsService: FeedbackSessionsService;
  let ngbModal: NgbModal;

  const testCourseId: string = 'CS3281';
  const testCourseName: string = 'Valid course';
  const testTimeZone: string = 'UTC';
  const timeZoneOffsets1: Record<string, number> = { GMT: 0 };
  const testCourse: Course = {
    courseId: 'testId',
    courseName: 'Test Course',
    timeZone: 'Asia/Singapore',
    institute: 'Test Institute',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const spyStatusMessageService: any = {
    showErrorToast: jest.fn(),
    showSuccessToast: jest.fn(),
  };
  const timezoneServiceStub: any = {
    getTzOffsets: jest.fn(() => timeZoneOffsets1),
    guessTimezone: jest.fn(() => 'UTC'),
  };
  const spyCourseService: any = {
    createCourse: jest.fn(() => of({})),
    getAllCoursesAsInstructor: jest.fn(() => of({ courses: [testCourse] })),
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddCourseFormComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        NgbModule,
        AjaxLoadingModule,
      ],
      providers: [
        { provide: StatusMessageService, useValue: spyStatusMessageService },
        { provide: CourseService, useValue: spyCourseService },
        { provide: TimezoneService, useValue: timezoneServiceStub },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCourseFormComponent);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    ngbModal = TestBed.inject(NgbModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    // Unable to leave timezone as default field, otherwise the field defaults to the
    // timezone the system is on. This will differ from
    // place to place causing the snapshot to constantly be mismatched.
    component.timezone = testTimeZone;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when not enabled', () => {
    component.isEnabled = false;
    component.timezone = testTimeZone;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should call showErrorToast when courseId is blank', () => {
    component.newCourseId = '';
    component.onSubmit();
    fixture.detectChanges();
    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalled();
  });

  it('should hold added course with valid details', () => {
    component.newCourseId = testCourseId;
    component.newCourseName = testCourseName;
    component.onSubmit();
    fixture.detectChanges();
    expect(spyStatusMessageService.showSuccessToast).toHaveBeenCalled();
  });

  it('should open copy course modal', () => {
    class MockNgbModalRef {
      componentInstance: any = {
        isCopyFromOtherSession: false,
        courses: [],
        courseToFeedbackSession: {},
        fetchFeedbackSessionsEvent: new EventEmitter<string>(),
      };
      result: Promise<any> = Promise.resolve();
    }

    const mockModalRef: MockNgbModalRef = new MockNgbModalRef();

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

    spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor')
      .and.returnValue(of({ feedbackSessions: [testFeedbackSession] }));
    spyOn(ngbModal, 'open').and.returnValue(mockModalRef);
    component.activeCourses = [testCourse];

    component.onCopy();
    mockModalRef.componentInstance.fetchFeedbackSessionsEvent.emit(testCourse.courseId);

    expect(ngbModal.open).toHaveBeenCalledWith(CopyCourseModalComponent);
    expect(mockModalRef.componentInstance.isCopyFromOtherSession).toEqual(true);
    expect(mockModalRef.componentInstance.activeCourses[0]).toEqual(testCourse);
    expect(mockModalRef.componentInstance.courseToFeedbackSession[testCourse.courseId]).toEqual([testFeedbackSession]);
  });
});
