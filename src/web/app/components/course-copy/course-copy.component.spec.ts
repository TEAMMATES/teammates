import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  ComponentFixture,
  TestBed,
  waitForAsync,
} from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbActiveModal, NgbModal, NgbModalRef, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService, TweakedTimestampData } from '../../../services/feedback-sessions.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
} from '../../../types/api-output';
import { ResponseVisibleSetting, SessionVisibleSetting } from '../../../types/api-request';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { CopyCourseModalResult } from '../copy-course-modal/copy-course-modal-model';
import { CopyCourseModalComponent } from '../copy-course-modal/copy-course-modal.component';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { CourseCopyComponent } from './course-copy.component';

describe('CourseEditFormComponent', () => {
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;
  let modalService: NgbModal;

  let component: CourseCopyComponent;
  let fixture: ComponentFixture<CourseCopyComponent>;

  let copyCourseModalComponent: CopyCourseModalComponent;
  let copyCourseModalFixture: ComponentFixture<CopyCourseModalComponent>;

  const date1: Date = new Date('2018-11-05T08:15:30');

  const courseCS1231: Course = {
    courseId: 'CS1231',
    courseName: 'Discrete Structures',
    creationTimestamp: date1.getTime(),
    deletionTimestamp: 0,
    timeZone: 'UTC',
    institute: 'Test Institute',
  };

  const mockFeedbackSession: FeedbackSession = {
    courseId: 'dog.gma-demo',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'First team feedback session',
    instructions: 'Please give your feedback based on the following questions.',
    submissionStartTimestamp: 1333295940000,
    submissionEndTimestamp: 1333382340000,
    submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    createdAtTimestamp: 1333324740000,
    gracePeriod: 1,
    sessionVisibleSetting: SessionVisibleSetting.CUSTOM,
    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    isClosingEmailEnabled: false,
    isPublishedEmailEnabled: false,
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CourseCopyComponent],
      providers: [
        NgbActiveModal,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        NgbModule,
        AjaxLoadingModule,
        CommonModule,
        FormsModule,
        AjaxLoadingModule,
        LoadingRetryModule,
        LoadingSpinnerModule,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    modalService = TestBed.inject(NgbModal);

    fixture = TestBed.createComponent(CourseCopyComponent);
    component = fixture.componentInstance;

    copyCourseModalFixture = TestBed.createComponent(CopyCourseModalComponent);
    copyCourseModalComponent = copyCourseModalFixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call copy course', () => {
    const modalResult: CopyCourseModalResult = {
      newCourseId: 'abc123',
      newCourseName: 'newcourse',
      newCourseInstitute: 'newinstitute',
      oldCourseId: courseCS1231.courseId,
      newTimeZone: 'newtimezone',
      selectedFeedbackSessionList: new Set(),
      totalNumberOfSessions: 0,
    };

    const mockModalRef: NgbModalRef = createMockNgbModalRef(
      copyCourseModalComponent,
      Promise.resolve(modalResult),
    );

    const courseCopied = jest.spyOn(component.courseCopied, 'emit');
    const isCopyingCourse = jest.spyOn(component.isCopyingCourse, 'emit');

    const courseSpy: SpyInstance = jest.spyOn(courseService, 'createCopiedCourse').mockImplementation(
      (result: CopyCourseModalResult): Observable<{
        course: Course,
        modified: Record<string, TweakedTimestampData>,
      }> => {
        expect(result).toBe(modalResult);

        return of({
          course: courseCS1231,
          modified: {},
        });
      });

    const feedbackSessionsSpy: SpyInstance = jest.spyOn(
      feedbackSessionsService,
      'getFeedbackSessionsForInstructor',
    ).mockImplementation(
      (courseId?: string): Observable<FeedbackSessions> => {
        expect(courseId).toBe(courseCS1231.courseId);

        return of({
          feedbackSessions: [mockFeedbackSession],
        });
      });

    const modalSpy: SpyInstance = jest.spyOn(modalService, 'open')
      .mockImplementation(() => mockModalRef);

    component.onCopy(courseCS1231.courseId, courseCS1231.courseName, courseCS1231.timeZone);

    return new Promise((done) => {
      setTimeout(() => {
        expect(courseSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(feedbackSessionsSpy).toHaveBeenCalledTimes(1);

        expect(courseCopied).toHaveBeenCalledWith(courseCS1231);
        expect(isCopyingCourse).toHaveBeenCalledWith(false);

        done(null);
      }, 0);
    });
  });
});
