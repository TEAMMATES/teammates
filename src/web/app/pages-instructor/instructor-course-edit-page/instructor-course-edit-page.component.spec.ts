import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { Instructor, InstructorPermissionRole, JoinState } from '../../../types/api-output';
import { InstructorCreateRequest } from '../../../types/api-request';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { SimpleModalModule } from '../../components/simple-modal/simple-modal.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {
  InstructorEditPanel,
  InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';
import TestCourses from '../../test-resources/courses';
import TestInstructors from '../../test-resources/instructors';

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

  beforeEach(waitForAsync(() => {
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
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(TestCourses.cs101));

    component.loadCourseInfo();

    expect(component.course.courseId).toBe(TestCourses.cs101.courseId);
    expect(component.course.courseName).toBe(TestCourses.cs101.courseName);
    expect(component.course.timeZone).toBe(TestCourses.cs101.timeZone);
    expect(component.course.creationTimestamp).toBe(TestCourses.cs101.creationTimestamp);
    expect(component.course.deletionTimestamp).toBe(TestCourses.cs101.deletionTimestamp);
    expect(component.hasCourseLoadingFailed).toBeFalsy();
  });

  it('should not change course details if CANCEL is requested', () => {
    component.course = TestCourses.cs101;
    component.isCourseLoading = false;
    component.originalCourse = { ...component.course };
    fixture.detectChanges();

    component.isEditingCourse = true;
    component.course.courseName = 'Example Course Changed';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-cancel-course');
    button.click();

    expect(component.isEditingCourse).toBeFalsy();
    expect(component.course.courseName).toBe('Introduction to CS');
  });

  it('should update course details if SAVE is requested', () => {
    component.course = TestCourses.cs101;
    component.isCourseLoading = false;
    fixture.detectChanges();

    component.isEditingCourse = true;
    component.course.courseName = 'Example Course Changed';
    fixture.detectChanges();

    jest.spyOn(courseService, 'updateCourse').mockReturnValue(of({
      courseId: TestCourses.cs101.courseId,
      courseName: 'Example Course Changed',
      isCourseDeleted: false,
      timeZone: TestCourses.cs101.timeZone,
      institute: TestCourses.cs101.institute,
      creationTimestamp: TestCourses.cs101.creationTimestamp,
      deletionTimestamp: TestCourses.cs101.deletionTimestamp,
    }));

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-save-course');
    button.click();

    expect(component.isEditingCourse).toBeFalsy();
    expect(component.course.courseName).toBe('Example Course Changed');
  });

  it('should load correct instructors details for given API output', () => {
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of({
      instructors: [TestInstructors.hock, TestInstructors.hodor],
    }));

    component.loadCourseInstructors();

    expect(component.instructorDetailPanels[0].originalInstructor).toEqual(TestInstructors.hock);
    expect(component.instructorDetailPanels[1].originalInstructor).toEqual(TestInstructors.hodor);
    expect(component.isInstructorsLoading).toBeFalsy();
  });

  it('should not add instructor if CANCEL is requested', () => {
    component.course = TestCourses.cs101;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...TestInstructors.hock },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
      },
      {
        originalInstructor: { ...TestInstructors.hodor },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hodor),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hodor),
      },
    ];
    component.isAddingNewInstructor = true;
    component.newInstructorPanel = component.getInstructorEditPanelModel(TestInstructors.jane);
    component.newInstructorPanel.isEditing = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-cancel-instructor-${component.instructorDetailPanels.length + 1}`);
    button.click();

    expect(component.isAddingNewInstructor).toBeFalsy();
  });

  it('should add instructor details', () => {
    jest.spyOn(instructorService, 'createInstructor')
      .mockImplementation((params: { courseId: string, requestBody: InstructorCreateRequest }) => of({
        courseId: params.courseId,
        email: params.requestBody.email,
        joinState: JoinState.NOT_JOINED,
        name: params.requestBody.name,
      }));

    component.course = TestCourses.cs101;
    component.courseId = TestCourses.cs101.courseId;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...TestInstructors.hock },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
      },
      {
        originalInstructor: { ...TestInstructors.hodor },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hodor),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hodor),
      },
    ];
    component.isAddingNewInstructor = true;
    component.newInstructorPanel = component.getInstructorEditPanelModel(TestInstructors.jane);
    component.newInstructorPanel.isEditing = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-save-instructor-${component.instructorDetailPanels.length + 1}`);
    button.click();

    expect(component.isAddingNewInstructor).toBeFalsy();
    expect(component.isSavingNewInstructor).toBeFalsy();
    expect(component.instructorDetailPanels.length).toBe(3);
    expect(component.instructorDetailPanels[2].originalInstructor).toEqual(TestInstructors.jane);
    expect(component.newInstructorPanel).toEqual(emptyInstructorPanel);
  });

  it('should re-order if instructor is deleted', () => {
    jest.spyOn(instructorService, 'deleteInstructor').mockReturnValue(of({}));

    component.course = TestCourses.cs101;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...TestInstructors.hock },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
      },
      {
        originalInstructor: { ...TestInstructors.hodor },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hodor),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hodor),
      },
    ];

    component.deleteInstructor(0);
    fixture.detectChanges();

    // using document instead of fixture as modal gets added into the dom outside the viewRef
    const button: any = document.getElementsByClassName('modal-btn-ok').item(0);
    button.click();
    fixture.detectChanges();

    expect(component.instructorDetailPanels.length).toBe(1);
    expect(component.instructorDetailPanels[0].originalInstructor).toEqual(TestInstructors.hodor);
  });

  it('should re-send reminder email for new instructors', () => {
    const mockReminderFunction: jest.MockedFunction<any> = jest.fn((_: string, email: string) => of({
      message: `An email has been sent to ${email}`,
    }));
    jest.spyOn(courseService, 'remindInstructorForJoin').mockImplementation(mockReminderFunction);

    component.course = TestCourses.cs101;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...TestInstructors.hock },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.hock),
      },
      {
        originalInstructor: { ...TestInstructors.jane },
        originalPanel: component.getInstructorEditPanelModel(TestInstructors.jane),
        editPanel: component.getInstructorEditPanelModel(TestInstructors.jane),
      },
    ];
    fixture.detectChanges();

    let button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-resend-invite-${component.instructorDetailPanels.length}`);
    button.click();

    // using document instead of fixture as modal gets added into the dom outside the viewRef
    button = document.getElementsByClassName('modal-btn-ok').item(0);
    button.click();

    expect(mockReminderFunction).toBeCalledWith(TestCourses.cs101.courseId, TestInstructors.jane.email);
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with course details', () => {
    component.course = TestCourses.cs101;

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
