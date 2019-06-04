import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopySessionModalComponent } from './copy-session-modal.component';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting
} from "../../../types/api-output";
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";

describe('CopySessionModalComponent', () => {
  let component: CopySessionModalComponent;
  let fixture: ComponentFixture<CopySessionModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopySessionModalComponent],
      imports: [
        FormsModule,
      ],
      providers: [
        NgbActiveModal,
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopySessionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  const feedbackSessionToCopy: FeedbackSession = {
    courseId: 'Test01',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'Test session',
    instructions: 'Answer all',
    submissionStartTimestamp: 1555232400,
    submissionEndTimestamp: 1555332400,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 1554967204,
  };

  const courseSessionIn: Course = {
    courseId: 'Test01',
    courseName: 'Sample Course 101',
    creationDate: '13 Mar 2019',
    deletionDate: 'Not Applicable',
    timeZone: 'UTC',
  };

  const courseCopyTo: Course = {
    courseId: 'Test02',
    courseName: 'Sample Course 202',
    creationDate: '14 Mar 2019',
    deletionDate: 'Not Applicable',
    timeZone: 'UTC',
  };

  it('should bind fields correctly', fakeAsync(() => {
    component.newFeedbackSessionName = feedbackSessionToCopy.feedbackSessionName;
    component.courseCandidates = [courseSessionIn, courseCopyTo];
    component.sessionToCopyCourseId = courseSessionIn.courseId;
    fixture.detectChanges();
    tick();

    const nameInput: any = fixture.debugElement.query(By.css('.form-control'));
    const sessionInCourseSpan: any = fixture.debugElement.query(By.css('span.text-danger'));
    const copyButton: any = fixture.debugElement.query(By.css('button.btn.btn-primary'));

    expect(nameInput.nativeElement.value).toEqual('Test session');
    expect(component.courseCandidates.length).toEqual(2);
    expect(sessionInCourseSpan.nativeElement.textContent).toEqual('Test01');
    expect(copyButton.nativeElement.disabled).toBeTruthy();
  }));

  it('should set the course to copy to', fakeAsync(() => {
    component.newFeedbackSessionName = feedbackSessionToCopy.feedbackSessionName;
    component.courseCandidates = [courseSessionIn, courseCopyTo];
    component.sessionToCopyCourseId = courseSessionIn.courseId;
    fixture.detectChanges();
    tick();

    const options: DebugElement[] = fixture.debugElement.queryAll(By.css('input[type="radio"]'));
    const secondOption: any = options[1];
    secondOption.triggerEventHandler('change', { target: secondOption.nativeElement });
    fixture.detectChanges();
    tick();

    const nameInput: any = fixture.debugElement.query(By.css('.form-control'));
    const sessionInCourseSpan: any = fixture.debugElement.query(By.css('span.text-danger'));
    const copyButton: any = fixture.debugElement.query(By.css('button.btn.btn-primary'));

    expect(nameInput.nativeElement.value).toEqual('Test session');
    expect(component.courseCandidates.length).toEqual(2);
    expect(sessionInCourseSpan.nativeElement.textContent).toEqual('Test01');
    expect(copyButton.nativeElement.disabled).toBeFalsy();
  }));

  it('should snap with some session and courses candidates', fakeAsync(() => {
    component.newFeedbackSessionName = feedbackSessionToCopy.feedbackSessionName;
    component.courseCandidates = [courseSessionIn, courseCopyTo];
    component.sessionToCopyCourseId = courseSessionIn.courseId;
    fixture.detectChanges();
    tick();

    expect(fixture).toMatchSnapshot();
  }));

  it('should snap with course to copy to is set', fakeAsync(() => {
    component.newFeedbackSessionName = feedbackSessionToCopy.feedbackSessionName;
    component.courseCandidates = [courseSessionIn, courseCopyTo];
    component.sessionToCopyCourseId = courseSessionIn.courseId;
    fixture.detectChanges();
    tick();

    const options: DebugElement[] = fixture.debugElement.queryAll(By.css('input[type="radio"]'));
    const secondOption: any = options[1];
    secondOption.triggerEventHandler('change', { target: secondOption.nativeElement });
    fixture.detectChanges();
    tick();

    expect(fixture).toMatchSnapshot();
  }));

});
