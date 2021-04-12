import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AbstractControl, FormArray, FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {
  InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;
  const fb: FormBuilder = new FormBuilder();
  const instructorPrivileges: any = {
    coowner: {
      courseLevel: {
        canmodifycourse: true,
        canmodifyinstructor: true,
        canmodifysession: true,
        canmodifystudent: true,
        canviewstudentinsection: true,
        canviewsessioninsection: true,
        cansubmitsessioninsection: true,
        canmodifysessioncommentinsection: true,
      },
      sectionLevel: {
        firstSection: {
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        },
        secondSection: {
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        },
      },
      sessionLevel: {
        sessionOne: {
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        },
        sessionTwo: {
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        },
      },
    },
    manager: {
      courseLevel: {
        canmodifycourse: false,
        canmodifyinstructor: true,
        canmodifysession: true,
        canmodifystudent: true,
        canviewstudentinsection: true,
        canviewsessioninsection: true,
        cansubmitsessioninsection: true,
        canmodifysessioncommentinsection: true,
      },
      sectionLevel: {
        firstSection: {
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: false,
        },
        secondSection: {
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: true,
        },
      },
      sessionLevel: {
        sessionOne: {
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: false,
        },
        sessionTwo: {
          canviewsessioninsection: true,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: true,
        },
      },
    },
    observer: {
      courseLevel: {
        canmodifycourse: false,
        canmodifyinstructor: false,
        canmodifysession: false,
        canmodifystudent: false,
        canviewstudentinsection: true,
        canviewsessioninsection: true,
        cansubmitsessioninsection: true,
        canmodifysessioncommentinsection: true,
      },
      sectionLevel: {
        firstSection: {
          canviewstudentinsection: true,
          canviewsessioninsection: false,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        },
        secondSection: {
          canviewstudentinsection: false,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        },
      },
      sessionLevel: {
        sessionOne: {
          canviewsessioninsection: false,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: false,
        },
        sessionTwo: {
          canviewsessioninsection: false,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: false,
        },
      },
    },
    tutor: {
      courseLevel: {
        canmodifycourse: false,
        canmodifyinstructor: false,
        canmodifysession: false,
        canmodifystudent: false,
        canviewstudentinsection: false,
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      },
      sectionLevel: {
        firstSection: {
          canviewstudentinsection: false,
          canviewsessioninsection: false,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: false,
        },
        secondSection: {
          canviewstudentinsection: false,
          canviewsessioninsection: false,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: false,
        },
      },
      sessionLevel: {
        sessionOne: {
          canviewsessioninsection: false,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: false,
        },
        sessionTwo: {
          canviewsessioninsection: false,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: false,
        },
      },
    },
    custom: {
      courseLevel: {
        canmodifycourse: false,
        canmodifyinstructor: true,
        canmodifysession: false,
        canmodifystudent: true,
        canviewstudentinsection: false,
        canviewsessioninsection: true,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: true,
      },
      sectionLevel: {
        firstSection: {
          canviewstudentinsection: true,
          canviewsessioninsection: false,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: false,
        },
        secondSection: {
          canviewstudentinsection: false,
          canviewsessioninsection: true,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: true,
        },
      },
      sessionLevel: {
        sessionOne: {
          canviewsessioninsection: false,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: false,
        },
        sessionTwo: {
          canviewsessioninsection: true,
          cansubmitsessioninsection: false,
          canmodifysessioncommentinsection: true,
        },
      },
    },
  };
  const instructorList: any[] = [
    {
      googleId: 'coowner@gmail.com',
      name: 'Ms. Co-Owner',
      email: 'coowner@email.com',
      role: 'Co-owner',
      isDisplayedToStudents: true,
      displayedName: 'Ayush The Co-Owner',
      privileges: instructorPrivileges.coowner,
    },
    {
      googleId: 'manager@gmail.com',
      name: 'Mr. Manager',
      email: 'manager@email.com',
      role: 'Manager',
      isDisplayedToStudents: true,
      displayedName: 'Byush the Manager',
      privileges: instructorPrivileges.manager,
    },
    {
      googleId: 'observer@gmail.com',
      name: 'Ms. Observer',
      email: 'observer@email.com',
      role: 'Observer',
      isDisplayedToStudents: false,
      displayedName: 'Cyush the Invisible Observer',
      privileges: instructorPrivileges.observer,
    },
    {
      name: 'Mr. Tutor',
      email: 'tutor@email.com',
      role: 'Tutor',
      isDisplayedToStudents: true,
      displayedName: 'Dyush the Tutor without Google ID',
      privileges: instructorPrivileges.tutor,
    },
    {
      googleId: 'custom@gmail.com',
      name: 'Ms. Custom',
      email: 'custom@email.com',
      role: 'Custom',
      isDisplayedToStudents: true,
      displayedName: 'Eyush the Custom',
      privileges: instructorPrivileges.custom,
    },
  ];

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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    component.timezone = 'GMT+8';
    component.timezones = ['GMT+8', 'GMT+7', 'GMT+6'];
    component.sectionNames = ['firstSection', 'secondSection'];
    component.feedbackNames = ['sessionOne', 'sessionTwo'];
    component.instructorList = instructorList;
    component.courseToEdit = {
      id: 'CS3281',
      name: 'Thematic Systems',
      timeZone: 'GMT+8',
    };
    component.formAddInstructor = fb.group({
      googleId: [''],
      name: [''],
      email: [''],
      isDisplayedToStudents: [{ value: true }],
      displayedName: ['InstructorNameAdd'],
      role: ['Co-owner'],
      privileges: instructorPrivileges.coowner,
      tunePermissions: fb.group({
        permissionsForCourse: fb.group({
          canmodifycourse: true,
          canmodifyinstructor: true,
          canmodifysession: true,
          canmodifystudent: true,
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        }),
        tuneSectionGroupPermissions: fb.array([]),
      }),
    });
    component.formEditInstructors = fb.group({
      formInstructors: fb.array([]),
    });
    component.instructor = instructorList[0];
    component.formEditCourse = fb.group({
      id: [{ value: component.courseToEdit.id, disabled: true }],
      name: [{ value: component.courseToEdit.name, disabled: true }],
      timeZone: [{ value: component.courseToEdit.timeZone, disabled: true }],
    });

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

});
