import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
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
  let activeModal: NgbActiveModal;

  beforeEach(waitForAsync(() => {
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
    // activeModal = TestBed.inject(NgbActiveModal);
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
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const courseSessionIn: Course = {
    courseId: 'Test01',
    courseName: 'Sample Course 101',
    institute: 'Test Institute',
    creationTimestamp: 1557764430000,
    deletionTimestamp: 0,
    timeZone: 'UTC',
  };

  const courseCopyTo: Course = {
    courseId: 'Test02',
    courseName: 'Sample Course 202',
    institute: 'Test Institute',
    creationTimestamp: 1557850830000,
    deletionTimestamp: 0,
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

    const options: DebugElement[] = fixture.debugElement.queryAll(By.css('input[type="checkbox"]'));
    const secondOption: any = options[1];
    secondOption.triggerEventHandler('click', { target: secondOption.nativeElement });
    fixture.detectChanges();

    const copyButton: any = fixture.debugElement.query(By.css('button.btn.btn-primary'));
    expect(copyButton.nativeElement.disabled).toBeFalsy();
  });






  it('should close the modal with the correct data', () => {
    // Set test data for the component's properties
    component.newFeedbackSessionName = 'Test Feedback Session';
    component.sessionToCopyCourseId = 'TestCourseID';
    component.copyToCourseSet.add('Course1');
    component.copyToCourseSet.add('Course2');

    // Spy on the NgbActiveModal's close method
    const closeSpy = jest.spyOn(activeModal, 'close');

    // Call the copy method
    component.copy();

    // Expect the NgbActiveModal's close method to have been called with the correct data
    expect(closeSpy).toHaveBeenCalledWith({
      newFeedbackSessionName: 'Test Feedback Session',
      sessionToCopyCourseId: 'TestCourseID',
      copyToCourseList: ['Course1', 'Course2'],
    });
  });




  it('should add a courseId to copyToCourseSet when it is not already present', () => {
    const courseId = 'Course1';

    // Ensure the set doesn't contain the courseId initially
    expect(component.copyToCourseSet.has(courseId)).toBe(false);

    // Call the select method to add the courseId
    component.select(courseId);

    // Expect the set to contain the courseId after calling select
    expect(component.copyToCourseSet.has(courseId)).toBe(true);
  });

  it('should remove a courseId from copyToCourseSet when it is already present', () => {
    const courseId = 'Course1';

    // Add courseId to the set initially
    component.copyToCourseSet.add(courseId);

    // Ensure the set contains the courseId before calling select
    expect(component.copyToCourseSet.has(courseId)).toBe(true);

    // Call the select method to remove the courseId
    component.select(courseId);

    // Expect the set not to contain the courseId after calling select
    expect(component.copyToCourseSet.has(courseId)).toBe(false);
  });

  it('should toggle courseId in copyToCourseSet', () => {
    const courseId = 'Course1';

    // Initially, the set doesn't contain the courseId
    expect(component.copyToCourseSet.has(courseId)).toBe(false);

    // Call select to add it
    component.select(courseId);

    // Now it should be in the set
    expect(component.copyToCourseSet.has(courseId)).toBe(true);

    // Call select again to remove it
    component.select(courseId);

    // It should be removed from the set
    expect(component.copyToCourseSet.has(courseId)).toBe(false);
  });

  it('should create', () => {
    var constructComponent = new CopySessionModalComponent(activeModal);
    expect(constructComponent).toBeTruthy();
  });

  it('should have NgbActiveModal injected', () => {
    var constructComponent = new CopySessionModalComponent(activeModal);
    expect(constructComponent.activeModal).toBe(activeModal);
  });

});
