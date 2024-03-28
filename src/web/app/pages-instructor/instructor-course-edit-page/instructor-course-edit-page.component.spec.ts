import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import {
  CopyInstructorsFromOtherCoursesModalComponent,
} from './copy-instructors-from-other-courses-modal/copy-instructors-from-other-courses-modal.component';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {
  InstructorEditPanel,
  InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { instructorBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Course, Instructor, InstructorPermissionRole, JoinState } from '../../../types/api-output';
import { InstructorCreateRequest } from '../../../types/api-request';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CourseEditFormComponent } from '../../components/course-edit-form/course-edit-form.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { SimpleModalModule } from '../../components/simple-modal/simple-modal.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

const testCourse: Course = {
  courseId: 'exampleId',
  courseName: 'Example Course',
  institute: 'Test Institute',
  timeZone: 'UTC (UTC)',
  creationTimestamp: 0,
  deletionTimestamp: 1000,
};

const testInstructor1: Instructor = instructorBuilder.email('instructor1@gmail.com').name('Instructor 1').build();

const testInstructor2 = instructorBuilder
  .email('instructor2@gmail.com')
  .joinState(JoinState.NOT_JOINED)
  .name('Instructor 2')
  .build();

const testInstructor3 = instructorBuilder
  .email('instructor3@gmail.com')
  .joinState(JoinState.NOT_JOINED)
  .name('Instructor 3')
  .build();

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
  let simpleModalService: SimpleModalService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseEditPageComponent,
        InstructorEditPanelComponent,
        ViewRolePrivilegesModalComponent,
        CustomPrivilegeSettingPanelComponent,
        CopyInstructorsFromOtherCoursesModalComponent,
        CourseEditFormComponent,
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
        PanelChevronModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEditPageComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    instructorService = TestBed.inject(InstructorService);
    simpleModalService = TestBed.inject(SimpleModalService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load correct course details for given API output', () => {
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));

    component.loadCourseInfo();

    expect(component.courseFormModel.course.courseId).toBe('exampleId');
    expect(component.courseFormModel.course.courseName).toBe('Example Course');
    expect(component.courseFormModel.course.timeZone).toBe('UTC (UTC)');
    expect(component.courseFormModel.course.creationTimestamp).toBe(0);
    expect(component.courseFormModel.course.deletionTimestamp).toBe(1000);
    expect(component.hasCourseLoadingFailed).toBeFalsy();
  });

  it('should not change course details if CANCEL is requested', () => {
    component.courseFormModel.course = testCourse;
    component.isCourseLoading = false;
    component.courseFormModel.originalCourse = { ...component.courseFormModel.course };
    fixture.detectChanges();

    component.courseFormModel.isEditing = true;
    component.courseFormModel.course.courseName = 'Example Course Changed';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-cancel-course');
    button.click();

    expect(component.courseFormModel.isEditing).toBeFalsy();
    expect(component.courseFormModel.course.courseName).toBe('Example Course');
  });

  it('should update course details if SAVE is requested', () => {
    component.courseFormModel.course = testCourse;
    component.isCourseLoading = false;
    fixture.detectChanges();

    component.courseFormModel.isEditing = true;
    component.courseFormModel.course.courseName = 'Example Course Changed';
    fixture.detectChanges();

    jest.spyOn(courseService, 'updateCourse').mockReturnValue(of({
      courseId: 'exampleId',
      courseName: 'Example Course Changed',
      isCourseDeleted: false,
      timeZone: 'UTC (UTC)',
      institute: 'Test institute',
      creationTimestamp: 0,
      deletionTimestamp: 1000,
    }));

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-save-course');
    button.click();

    expect(component.courseFormModel.isEditing).toBeFalsy();
    expect(component.courseFormModel.course.courseName).toBe('Example Course Changed');
  });

  it('should update instructor details if SAVE is requested', () => {
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of({
      instructors: [testInstructor1],
    }));

    component.loadCourseInstructors();

    component.isInstructorsLoading = false;
    fixture.detectChanges();

    component.instructorDetailPanels[0].editPanel.isEditing = true;
    component.instructorDetailPanels[0].editPanel.name = 'Example Instructor Changed';
    fixture.detectChanges();

    jest.spyOn(instructorService, 'updateInstructor').mockReturnValue(of({
      courseId: 'exampleId',
      email: 'instructor1@gmail.com',
      joinState: JoinState.JOINED,
      name: 'Example Instructor Changed',
    }));

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-save-instructor-1');
    button.click();

    expect(component.instructorDetailPanels[0].editPanel.isEditing).toBeFalsy();
    expect(component.instructorDetailPanels[0].editPanel.name).toBe('Example Instructor Changed');
  });

  it('should load correct instructors details for given API output', () => {
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of({
      instructors: [testInstructor1, testInstructor2],
    }));

    component.loadCourseInstructors();

    expect(component.instructorDetailPanels[0].originalInstructor).toEqual(testInstructor1);
    expect(component.instructorDetailPanels[1].originalInstructor).toEqual(testInstructor2);
    expect(component.isInstructorsLoading).toBeFalsy();
  });

  it('should not add instructor if CANCEL is requested', () => {
    component.courseFormModel.course = testCourse;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...testInstructor1 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor1),
        editPanel: component.getInstructorEditPanelModel(testInstructor1),
      },
      {
        originalInstructor: { ...testInstructor2 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor2),
        editPanel: component.getInstructorEditPanelModel(testInstructor2),
      },
    ];
    component.isAddingNewInstructor = true;
    component.newInstructorPanel = component.getInstructorEditPanelModel(testInstructor3);
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

    component.courseFormModel.course = testCourse;
    component.courseId = testCourse.courseId;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...testInstructor1 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor1),
        editPanel: component.getInstructorEditPanelModel(testInstructor1),
      },
      {
        originalInstructor: { ...testInstructor2 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor2),
        editPanel: component.getInstructorEditPanelModel(testInstructor2),
      },
    ];
    component.isAddingNewInstructor = true;
    component.newInstructorPanel = component.getInstructorEditPanelModel(testInstructor3);
    component.newInstructorPanel.isEditing = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-save-instructor-${component.instructorDetailPanels.length + 1}`);
    button.click();

    expect(component.isAddingNewInstructor).toBeFalsy();
    expect(component.isSavingNewInstructor).toBeFalsy();
    expect(component.instructorDetailPanels.length).toBe(3);
    expect(component.instructorDetailPanels[2].originalInstructor).toEqual(testInstructor3);
    expect(component.newInstructorPanel).toEqual(emptyInstructorPanel);
  });

  it('should re-order if instructor is deleted', async () => {
    jest.spyOn(instructorService, 'deleteInstructor').mockReturnValue(of({}));

    jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef());

    component.courseFormModel.course = testCourse;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...testInstructor1 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor1),
        editPanel: component.getInstructorEditPanelModel(testInstructor1),
      },
      {
        originalInstructor: { ...testInstructor2 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor2),
        editPanel: component.getInstructorEditPanelModel(testInstructor2),
      },
    ];

    component.deleteInstructor(0);
    fixture.detectChanges();

    await fixture.whenStable().then(() => {
      expect(component.instructorDetailPanels.length).toBe(1);
      expect(component.instructorDetailPanels[0].originalInstructor).toEqual(testInstructor2);
    });
  });

  it('should re-send reminder email for new instructors', () => {
    const mockReminderFunction: jest.MockedFunction<any> = jest.fn((_: string, email: string) => of({
      message: `An email has been sent to ${email}`,
    }));
    jest.spyOn(courseService, 'remindInstructorForJoin').mockImplementation(mockReminderFunction);

    jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef());

    component.courseFormModel.course = testCourse;
    component.isCourseLoading = false;
    component.instructorDetailPanels = [
      {
        originalInstructor: { ...testInstructor1 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor1),
        editPanel: component.getInstructorEditPanelModel(testInstructor1),
      },
      {
        originalInstructor: { ...testInstructor2 },
        originalPanel: component.getInstructorEditPanelModel(testInstructor2),
        editPanel: component.getInstructorEditPanelModel(testInstructor2),
      },
    ];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement
        .querySelector(`#btn-resend-invite-${component.instructorDetailPanels.length}`);
    button.click();

    expect(mockReminderFunction).toHaveBeenCalledWith(testCourse.courseId, testInstructor2.email);
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with course details', () => {
    component.courseFormModel.course = testCourse;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when editing course details', () => {
    component.courseFormModel.isEditing = true;

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
