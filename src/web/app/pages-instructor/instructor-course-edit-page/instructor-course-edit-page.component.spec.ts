import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {of} from 'rxjs';
import {CourseService} from '../../../services/course.service';
import {Course, Instructor, JoinState} from '../../../types/api-output';
import {AjaxLoadingModule} from '../../components/ajax-loading/ajax-loading.module';
import {LoadingRetryModule} from '../../components/loading-retry/loading-retry.module';
import {LoadingSpinnerModule} from '../../components/loading-spinner/loading-spinner.module';
import {TeammatesCommonModule} from '../../components/teammates-common/teammates-common.module';
import {TeammatesRouterModule} from '../../components/teammates-router/teammates-router.module';
import {CustomPrivilegeSettingPanelComponent,} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import {InstructorCourseEditPageComponent} from './instructor-course-edit-page.component';
import {InstructorEditPanelComponent,} from './instructor-edit-panel/instructor-edit-panel.component';
import {ViewRolePrivilegesModalComponent} from './view-role-privileges-modal/view-role-privileges-modal.component';
import {InstructorService} from "../../../services/instructor.service";

const testCourse: Course = {
  courseId: 'exampleId',
  courseName: 'Example Course',
  timeZone: 'UTC (UTC)',
  creationTimestamp: 0,
  deletionTimestamp: 1000,
};

const testInstructor1: Instructor = {
  courseId: 'exampleId',
  email: 'instructor1@gmail.com',
  joinState: JoinState.NOT_JOINED,
  name: 'Instructor 1',
};

const testInstructor2: Instructor = {
  courseId: 'exampleId',
  email: 'instructor2@gmail.com',
  joinState: JoinState.JOINED,
  name: 'Instructor 2',
};

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;
  let courseService: CourseService;
  let instructorService: InstructorService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseEditPageComponent,
        InstructorEditPanelComponent,
        ViewRolePrivilegesModalComponent,
        CustomPrivilegeSettingPanelComponent,
      ],
      imports: [
        NgbModule,
        FormsModule,
        AjaxLoadingModule,
        TeammatesCommonModule,
        RouterTestingModule,
        HttpClientTestingModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEditPageComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    instructorService = TestBed.inject(InstructorService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load correct course details for given API output', () => {
    spyOn(courseService, 'getCourseAsInstructor').and.returnValue(of(testCourse));

    component.loadCourseInfo();

    expect(component.course.courseId).toBe('exampleId');
    expect(component.course.courseName).toBe('Example Course');
    expect(component.course.timeZone).toBe('UTC (UTC)');
    expect(component.course.creationTimestamp).toBe(0);
    expect(component.course.deletionTimestamp).toBe(1000);
    expect(component.hasCourseLoadingFailed).toBeFalsy();
  });

  it('should not change course details if CANCEL is requested', () => {
    component.course = testCourse;
    component.isCourseLoading = false;
    component.originalCourse = Object.assign({}, component.course);
    fixture.detectChanges();

    component.isEditingCourse = true;
    component.course.courseName = 'Example Course Changed';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-cancel-course');
    button.click();

    expect(component.isEditingCourse).toBeFalsy();
    expect(component.course.courseName).toBe('Example Course');
  });

  it('should update course details if SAVE is requested', () => {
    component.course = testCourse;
    component.isCourseLoading = false;
    fixture.detectChanges();

    component.isEditingCourse = true;
    component.course.courseName = 'Example Course Changed';
    fixture.detectChanges();

    spyOn(courseService, 'updateCourse').and.returnValue(of({
      courseId: 'exampleId',
      courseName: 'Example Course Changed',
      timeZone: 'UTC (UTC)',
      creationTimestamp: 0,
      deletionTimestamp: 1000,
    }));

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-save-course');
    button.click();

    expect(component.isEditingCourse).toBeFalsy();
    expect(component.course.courseName).toBe('Example Course Changed');
  });

  it('should load correct instructors details for given API output', () => {
    spyOn(instructorService, 'loadInstructors').and.returnValue(of({
      instructors: [testInstructor1, testInstructor2],
    }));

    component.loadCourseInstructors();

    expect(component.instructorDetailPanels[0].originalInstructor).toEqual(testInstructor1);
    expect(component.instructorDetailPanels[1].originalInstructor).toEqual(testInstructor2);
    expect(component.isInstructorsLoading).toBeFalsy();
  });

  it('should re-order if instructor is deleted', () => {
    component.course = testCourse;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: Object.assign({}, testInstructor1),
        originalPanel: component.getInstructorEditPanelModel(testInstructor1),
        editPanel: component.getInstructorEditPanelModel(testInstructor1),
      },
      {
        originalInstructor: Object.assign({}, testInstructor2),
        originalPanel: component.getInstructorEditPanelModel(testInstructor2),
        editPanel: component.getInstructorEditPanelModel(testInstructor2),
      },
    ];
    fixture.detectChanges();

    component.deleteInstructor(1);
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-save-course');
    button.click();






  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with course details', () => {
    component.course = testCourse;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when editing course details', () => {
    component.isEditingCourse = true;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some instructor details', () => {
    const instructor: Instructor = {
      name: 'Instructor A',
      email: 'instructora@example.com',
      courseId: component.courseId,
      joinState: JoinState.JOINED,
    };

    component.instructorDetailPanels = [
      {
        originalInstructor: instructor,
        originalPanel: component.getInstructorEditPanelModel(instructor),
        editPanel: component.getInstructorEditPanelModel(instructor),
      },
    ];

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });
});
