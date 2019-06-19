import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DebugElement } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { CopySessionModalComponent } from './copy-session-modal.component';

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

  it('should snap with some session and courses candidates', () => {
    component.newFeedbackSessionName = feedbackSessionToCopy.feedbackSessionName;
    component.courseCandidates = [courseSessionIn, courseCopyTo];
    component.sessionToCopyCourseId = courseSessionIn.courseId;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should enable copy button after course to copy to is selected', () => {
    component.newFeedbackSessionName = feedbackSessionToCopy.feedbackSessionName;
    component.courseCandidates = [courseSessionIn, courseCopyTo];
    component.sessionToCopyCourseId = courseSessionIn.courseId;
    fixture.detectChanges();

    const options: DebugElement[] = fixture.debugElement.queryAll(By.css('input[type="radio"]'));
    const secondOption: any = options[1];
    secondOption.triggerEventHandler('change', { target: secondOption.nativeElement });
    fixture.detectChanges();

    const copyButton: any = fixture.debugElement.query(By.css('button.btn.btn-primary'));
    expect(copyButton.nativeElement.disabled).toBeFalsy();
  });

});
