import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { Course, Instructor, InstructorPermissionRole, JoinState } from '../../../types/api-output';
import { InstructorCreateRequest } from '../../../types/api-request';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { SimpleModalModule } from '../../components/simple-modal/simple-modal.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { CustomPrivilegeSettingPanelComponent } from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {
    InstructorEditPanel,
    InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';

const testCourse: Course = {
  courseId: 'exampleId',
  courseName: 'Example Course',
  institute: 'Test Institute',
  timeZone: 'UTC (UTC)',
  creationTimestamp: 0,
  deletionTimestamp: 1000,
};

const testInstructor1: Instructor = {
  courseId: 'exampleId',
  email: 'instructor1@gmail.com',
  joinState: JoinState.JOINED,
  name: 'Instructor 1',
};

const testInstructor2: Instructor = {
  courseId: 'exampleId',
  email: 'instructor2@gmail.com',
  joinState: JoinState.NOT_JOINED,
  name: 'Instructor 2',
};

const testInstructor3: Instructor = {
  courseId: 'exampleId',
  email: 'instructor3@gmail.com',
  joinState: JoinState.NOT_JOINED,
  name: 'Instructor 3',
};

const emptyInstructorPanel: InstructorEditPanel = {
  googleId: '',
  courseId: '',
  email: '',
  isDisplayedToStudents: true,
  displayedToStudentsAs: '',
  name: '',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  joinState: JoinState.NOT_JOINED,

  permission: {
    privilege: {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    },
    sectionLevel: [],
  },

  isEditing: true,
  isSavingInstructorEdit: false,
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
        SimpleModalModule,
        BrowserAnimationsModule,
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

  it('should not add instructor if CANCEL is requested', () => {
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
    component.isAddingNewInstructor = true;
    component.newInstructorPanel = component.getInstructorEditPanelModel(testInstructor3);
    component.newInstructorPanel.isEditing = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-cancel-instructor-${ component.instructorDetailPanels.length + 1 }`);
    button.click();

    expect(component.isAddingNewInstructor).toBeFalsy();
  });

  it('should add instructor details', () => {
    spyOn(instructorService, 'createInstructor').and
      .callFake(({ courseId, requestBody }: { courseId: string, requestBody: InstructorCreateRequest }) => of({
        courseId,
        email: requestBody.email,
        joinState: JoinState.NOT_JOINED,
        name: requestBody.name,
      }));

    component.course = testCourse;
    component.courseId = testCourse.courseId;
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
    component.isAddingNewInstructor = true;
    component.newInstructorPanel = component.getInstructorEditPanelModel(testInstructor3);
    component.newInstructorPanel.isEditing = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-save-instructor-${ component.instructorDetailPanels.length + 1 }`);
    button.click();

    expect(component.isAddingNewInstructor).toBeFalsy();
    expect(component.isSavingNewInstructor).toBeFalsy();
    expect(component.instructorDetailPanels.length).toBe(3);
    expect(component.instructorDetailPanels[2].originalInstructor).toEqual(testInstructor3);
    expect(component.newInstructorPanel).toEqual(emptyInstructorPanel);
  });

  it('should re-order if instructor is deleted', () => {
    spyOn(instructorService, 'deleteInstructor').and.returnValue(of({}));

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

    component.deleteInstructor(0);
    fixture.detectChanges();

    // using document instead of fixture as modal gets added into the dom outside the viewRef
    const button: any = document.getElementsByClassName('modal-btn-ok').item(0);
    button.click();
    fixture.detectChanges();

    expect(component.instructorDetailPanels.length).toBe(1);
    expect(component.instructorDetailPanels[0].originalInstructor).toEqual(testInstructor2);
  });

  it('should re-send reminder email for new instructors', () => {
    const mockReminderFunction: jest.MockedFunction<any> = jest.fn((_: string, email: string) => of({
      message: `An email has been sent to ${email}`,
    }));
    spyOn(courseService, 'remindInstructorForJoin').and.callFake(mockReminderFunction);

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

    let button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-resend-invite-${ component.instructorDetailPanels.length }`);
    button.click();

    // using document instead of fixture as modal gets added into the dom outside the viewRef
    button = document.getElementsByClassName('modal-btn-ok').item(0);
    button.click();

    expect(mockReminderFunction).toBeCalledWith(testCourse.courseId, testInstructor2.email);
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
