import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { CourseTabModel, InstructorToCopyCandidateModel } from './copy-instructors-from-other-courses-modal-model';
import { CopyInstructorsFromOtherCoursesModalComponent } from './copy-instructors-from-other-courses-modal.component';
import { InstructorService } from '../../../../services/instructor.service';
import {
  Instructor,
  InstructorPermissionRole, Instructors,
  JoinState,
} from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../../components/teammates-common/teammates-common.module';

describe('CopyInstructorsFromOtherCoursesModalComponent', () => {

  const testInstructor1: Instructor = {
    googleId: 'googleIdOfIns1',
    courseId: 'FAN0001',
    email: 'ins1@fan.tmt',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'abcdefg',
    name: 'Instructor Cat',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
    key: 'ujg2l5hj5l7jrfo28fyosfklh2892',
    institute: 'A',
  };

  const testInstructor2: Instructor = {
    googleId: 'googleIdOfIns2',
    courseId: 'FAN0001',
    email: 'ins2@fan.tmt',
    isDisplayedToStudents: false,
    displayedToStudentsAs: '',
    name: 'Instructor Dog',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM,
    joinState: JoinState.JOINED,
    key: '1343jbcl3iru2yct0897goji',
    institute: 'A',
  };

  const testInstructor3: Instructor = {
    googleId: 'googleIdOfIns3',
    courseId: 'FAN0002',
    email: 'ins1@fan.tmt',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'QWQWQWQ',
    name: 'King',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER,
    joinState: JoinState.NOT_JOINED,
    key: 'pjkjbnc523iosdk2389nfbfib2',
    institute: 'A',
  };

  const testInstructor4: Instructor = {
    googleId: 'googleIdOfIns4',
    courseId: 'FAN0002',
    email: 'ins4@fan.tmt',
    isDisplayedToStudents: false,
    displayedToStudentsAs: 'oldname',
    name: 'Nothing',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR,
    joinState: JoinState.JOINED,
    key: 'qdnjvbi47t928cjqnk1o8ry2o',
    institute: 'A',
  };

  const testInstructorCandidate1: InstructorToCopyCandidateModel = {
    instructor: testInstructor1,
    isSelected: false,
  };

  const testInstructorCandidate2: InstructorToCopyCandidateModel = {
    instructor: testInstructor2,
    isSelected: false,
  };

  const testInstructorCandidate3: InstructorToCopyCandidateModel = {
    instructor: testInstructor3,
    isSelected: false,
  };

  const testInstructorCandidate4: InstructorToCopyCandidateModel = {
    instructor: testInstructor4,
    isSelected: false,
  };

  const testCourseTab1: CourseTabModel = {
    courseId: 'FAN0002',
    courseName: 'Test Course 1',
    creationTimestamp: new Date('2022-07-26T01:00:15Z').getTime(),
    isArchived: true,
    instructorCandidates: [],
    instructorCandidatesSortBy: SortBy.NONE,
    instructorCandidatesSortOrder: SortOrder.ASC,
    hasInstructorsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  const testCourseTab2: CourseTabModel = {
    courseId: 'FAN0001',
    courseName: 'Test Course 2',
    creationTimestamp: new Date('2022-02-22T22:22:22Z').getTime(),
    isArchived: false,
    instructorCandidates: [],
    instructorCandidatesSortBy: SortBy.NONE,
    instructorCandidatesSortOrder: SortOrder.ASC,
    hasInstructorsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  const testCourseTab3: CourseTabModel = {
    courseId: 'CS2103T',
    courseName: 'XXX Software Engineering',
    creationTimestamp: new Date('2022-06-21T07:51:20Z').getTime(),
    isArchived: false,
    instructorCandidates: [],
    instructorCandidatesSortBy: SortBy.NONE,
    instructorCandidatesSortOrder: SortOrder.ASC,
    hasInstructorsLoaded: false,
    isTabExpanded: false,
    hasLoadingFailed: false,
  };

  let component: CopyInstructorsFromOtherCoursesModalComponent;
  let fixture: ComponentFixture<CopyInstructorsFromOtherCoursesModalComponent>;
  let instructorService: InstructorService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CopyInstructorsFromOtherCoursesModalComponent],
      imports: [
        CommonModule,
        FormsModule,
        NgbTooltipModule,
        TeammatesCommonModule,
        PanelChevronModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        AjaxLoadingModule,
        HttpClientTestingModule,
      ],
      providers: [
        NgbActiveModal,
        InstructorService,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyInstructorsFromOtherCoursesModalComponent);
    instructorService = TestBed.inject(InstructorService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with courses', () => {
    component.courses = [testCourseTab1, testCourseTab2];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when instructors are loading', () => {
    testCourseTab1.isTabExpanded = true;
    testCourseTab1.hasLoadingFailed = false;
    testCourseTab1.hasInstructorsLoaded = false;
    testCourseTab1.instructorCandidates = [];
    component.courses = [testCourseTab1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when instructors are loaded', () => {
    testCourseTab1.isTabExpanded = true;
    testCourseTab1.hasLoadingFailed = false;
    testCourseTab1.hasInstructorsLoaded = true;
    testCourseTab1.instructorCandidates = [testInstructorCandidate3, testInstructorCandidate4];
    component.courses = [testCourseTab1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback questions failed to load', () => {
    testCourseTab1.isTabExpanded = true;
    testCourseTab1.hasLoadingFailed = true;
    testCourseTab1.hasInstructorsLoaded = false;
    testCourseTab1.instructorCandidates = [];
    component.courses = [testCourseTab1];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load instructors', () => {
    const instructors: Instructors = {
      instructors: [testInstructor1, testInstructor2],
    };
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    component.courses = [testCourseTab2];

    component.loadInstructors(testCourseTab2);

    expect(component.courses[0].hasInstructorsLoaded).toBeTruthy();
    expect(component.courses[0].hasLoadingFailed).toBeFalsy();
    expect(component.courses[0].instructorCandidates.length).toBe(2);
    expect(component.courses[0].instructorCandidates[0].instructor.googleId)
      .toBe(testInstructor1.googleId);
    expect(component.courses[0].instructorCandidates[1].instructor.googleId)
      .toBe(testInstructor2.googleId);
  });

  it('should not allow copying when no instructors are selected', () => {
    testCourseTab2.instructorCandidates = [testInstructorCandidate1, testInstructorCandidate2];
    testCourseTab1.instructorCandidates = [testInstructorCandidate3, testInstructorCandidate4];
    component.courses = [testCourseTab2, testCourseTab1];
    fixture.detectChanges();

    const instructors: Instructor[] = component.getSelectedInstructors();
    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-instructor');

    expect(component.isAnyInstructorCandidatesSelected).toBeFalsy();
    expect(instructors.length).toBe(0);
    expect(button.disabled).toBeTruthy();
  });

  it('should trigger copy button clicked event with selected instructors', () => {
    testInstructorCandidate1.isSelected = true;
    testInstructorCandidate3.isSelected = true;
    testInstructorCandidate4.isSelected = true;
    testCourseTab2.instructorCandidates = [testInstructorCandidate1, testInstructorCandidate2];
    testCourseTab1.instructorCandidates = [testInstructorCandidate3, testInstructorCandidate4];
    component.courses = [testCourseTab2, testCourseTab1];
    fixture.detectChanges();

    const instructors: Instructor[] = [testInstructor1, testInstructor3, testInstructor4];
    const buttonConfirmCopy: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-instructor');
    jest.spyOn(component.copyClickedEvent, 'emit');

    expect(component.isAnyInstructorCandidatesSelected).toBeTruthy();
    expect(buttonConfirmCopy.disabled).toBeFalsy();

    buttonConfirmCopy.click();
    expect(component.copyClickedEvent.emit).toHaveBeenCalledTimes(1);
    expect(component.copyClickedEvent.emit).toHaveBeenCalledWith(instructors);
  });

  it('should disable buttons when copying instructors', () => {
    testInstructorCandidate1.isSelected = true;
    testCourseTab2.instructorCandidates = [testInstructorCandidate1];
    component.courses = [testCourseTab2];
    fixture.detectChanges();

    const buttonConfirmCopy: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm-copy-instructor');
    const buttonCancelCopy: any = fixture.debugElement.nativeElement.querySelector('#btn-cancel-copy-instructor');
    const buttonCloseModal: any = fixture.debugElement.nativeElement.querySelector('#btn-close-modal');

    buttonConfirmCopy.click();
    fixture.detectChanges();

    expect(buttonConfirmCopy.disabled).toBeTruthy();
    expect(buttonCancelCopy.disabled).toBeTruthy();
    expect(buttonCloseModal.disabled).toBeTruthy();
  });

  it('should sort courses by their IDs', () => {
    component.courses = [testCourseTab2, testCourseTab3, testCourseTab1];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-id');
    button.click();
    expect(component.courses[0].courseId).toEqual('CS2103T');
    expect(component.courses[1].courseId).toEqual('FAN0001');
    expect(component.courses[2].courseId).toEqual('FAN0002');
  });

  it('should sort courses by their names', () => {
    component.courses = [testCourseTab2, testCourseTab3, testCourseTab1];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-name');
    button.click();
    expect(component.courses[0].courseId).toEqual('FAN0002');
    expect(component.courses[1].courseId).toEqual('FAN0001');
    expect(component.courses[2].courseId).toEqual('CS2103T');
  });

  it('should sort courses by their creation dates', () => {
    component.courses = [testCourseTab2, testCourseTab3, testCourseTab1];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-creation-date');
    button.click();
    expect(component.courses[0].courseId).toEqual('FAN0002');
    expect(component.courses[1].courseId).toEqual('CS2103T');
    expect(component.courses[2].courseId).toEqual('FAN0001');
  });
});
