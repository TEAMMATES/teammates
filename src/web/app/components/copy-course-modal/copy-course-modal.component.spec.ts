import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { CopyCourseModalComponent } from './copy-course-modal.component';

describe('CopyCourseModalComponent', () => {
  let component: CopyCourseModalComponent;
  let fixture: ComponentFixture<CopyCourseModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CopyCourseModalComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyCourseModalComponent);
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
      isClosingEmailEnabled: true,
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
});
