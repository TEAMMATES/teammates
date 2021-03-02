import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopyCourseModalComponent } from './copy-course-modal.component';
import { By } from '@angular/platform-browser';
// import {
//   Course,
//   FeedbackSession,
//   FeedbackSessionPublishStatus,
//   FeedbackSessionSubmissionStatus,
//   ResponseVisibleSetting,
//   SessionVisibleSetting,
// } from '../../../types/api-output';

describe('CopyCourseModalComponent', () => {
  let component: CopyCourseModalComponent;
  let fixture: ComponentFixture<CopyCourseModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CopyCourseModalComponent ],
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
    fixture = TestBed.createComponent(CopyCourseModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  // const CourseToCopy: Course = {
  //   courseId: "Test01",
  //   courseName: "Test course name",
  //   creationTimestamp: 1557764430000,
  //   deletionTimestamp: 0,
  //   timeZone: 'UTC',
  // };
  // const feedbackSessionToCopy: FeedbackSession = {
  //   courseId: 'Test01',
  //   timeZone: 'Asia/Singapore',
  //   feedbackSessionName: 'Test session',
  //   instructions: 'Answer all',
  //   submissionStartTimestamp: 1555232400,
  //   submissionEndTimestamp: 1555332400,
  //   gracePeriod: 0,
  //   sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
  //   responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
  //   submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
  //   publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
  //   isClosingEmailEnabled: true,
  //   isPublishedEmailEnabled: true,
  //   createdAtTimestamp: 1554967204,
  // };

  it('should snap with some course id', () => {
    component.newCourseId = "Test02";
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();

  });

  it('should enable copy button after new courseId is provided', () => {
    component.newCourseId = "Test02";
    fixture.detectChanges();
    const copyButton: any = fixture.debugElement.query(By.css('button.btn.btn-primary'));
    expect(copyButton.nativeElement.disabled).toBeFalsy();
  });

  it('should disable copy if courseId is empty', () => {
    component.newCourseId = "";
    fixture.detectChanges();
    const copyButton: any = fixture.debugElement.query(By.css('button.btn.btn-primary'));
    expect(copyButton.nativeElement.disabled).toBeTruthy();
  });

});
