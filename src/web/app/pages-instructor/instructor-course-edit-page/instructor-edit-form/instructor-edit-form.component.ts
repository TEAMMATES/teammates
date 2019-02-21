import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Instructor } from '../../../Instructor';
import { DefaultPrivileges, Privileges, Role } from '../instructor-privileges-model';
import { InstructorEditFormMode, InstructorEditFormModel } from './instructor-edit-form-model';

/**
 * Form to add/edit instructors in a course.
 */
@Component({
  selector: 'tm-instructor-edit-form',
  templateUrl: './instructor-edit-form.component.html',
  styleUrls: ['./instructor-edit-form.component.scss']
})
export class InstructorEditFormComponent implements OnInit {

  // enum
  InstructorEditFormMode: typeof InstructorEditFormMode = InstructorEditFormMode;
  Role: typeof Role = Role;

  DefaultPrivileges: typeof DefaultPrivileges = DefaultPrivileges;

  displayedNamePlaceholder: string = '';

  @Input()
  set formModel(model: InstructorEditFormModel) {
    this.model = model;
  }

  model: InstructorEditFormModel = {
    googleId: '',
    name: '',
    email: '',
    role: Role.COOWNER,
    isDisplayedToStudents: false,
    displayedName: '',
    privileges: DefaultPrivileges.COOWNER.value,

    isEditable: false,
    isSaving: false,
  };

  @Input()
  modelNumber: number = 1;

  @Input()
  canModifyInstructor: boolean = false;

  @Input()
  courseInstructors: Instructor[] = [];

  // event emission
  @Output()
  formModelChange: EventEmitter<InstructorEditFormModel> = new EventEmitter();

  @Output()
  resendReminderEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  viewPrivilegesEvent: EventEmitter<any> = new EventEmitter<any>();

  @Output()
  deleteInstructorEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  editInstructorEvent: EventEmitter<InstructorEditFormModel> = new EventEmitter<InstructorEditFormModel>();

  @Output()
  addNewInstructorEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  ngOnInit(): void {
    this.displayedNamePlaceholder = this.model.isDisplayedToStudents ? 'E.g.Co-lecturer, Teaching Assistant'
        : '(This instructor will NOT be displayed to students)';
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.formModelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChangeBatch(obj: {[key: string]: any}): void {
    this.formModelChange.emit({
      ...this.model,
      ...obj,
    });
  }

  /**
   * Triggers the change of the model course level privileges.
   */
  triggerModelChangeForCourseLevelPrivileges(privilege: string, data: any): void {
    const currentPrivileges: Privileges = this.model.privileges;
    currentPrivileges.courseLevel[privilege] = data;

    // listen for specific value changes
    if (currentPrivileges.courseLevel.canviewsessioninsection) {
      currentPrivileges.courseLevel.canmodifysessioncommentinsection = false;
    }

    if (currentPrivileges.courseLevel.canmodifysessioncommentinsection) {
      currentPrivileges.courseLevel.canviewsessioninsection = true;
    }

    this.formModelChange.emit({
      ...this.model,
      privileges: currentPrivileges,
    });
  }

  /**
   * Handles resend reminder email button click event.
   */
  resendReminderHandler(): void {
    this.resendReminderEvent.emit();
  }

  /**
   * Handles view instructor privileges link click event.
   */
  viewPrivilegesHandler(role: string, privileges: Privileges): void {
    this.viewPrivilegesEvent.emit({role: role, privileges: privileges});
  }

  /**
   * Handles delete instructor button click event.
   */
  deleteInstructorHandler(): void {
    this.deleteInstructorEvent.emit();
  }

  /**
   * Handles save edit instructor button click event.
   */
  editInstructorHandler(): void {
    this.editInstructorEvent.emit(this.model);
  }

  /**
   * Changes the displayed instructor name if it is/is not displayed to other students.
   */
  onChangeIsDisplayedToStudents(isDisplayed: boolean): void {
    if (isDisplayed) {
      this.triggerModelChange('isDisplayedToStudents', true);
      this.displayedNamePlaceholder = 'E.g.Co-lecturer, Teaching Assistant';
    } else {
      this.triggerModelChangeBatch({
        isDisplayedToStudents: false,
        displayedName: '',
      });
      this.displayedNamePlaceholder = '(This instructor will NOT be displayed to students)';
    }
  }

  /**
  private initEditInstructorsForm(): void {
    this.formEditInstructors = this.fb.group({ formInstructors: [] });

    const control: FormArray = this.fb.array([]);
    this.instructorList.forEach((instructor: InstructorAttributes, index: number) => {
      const defaultPrivileges: Privileges = instructor.privileges;
      const instructorEmail: string = instructor.email ? instructor.email : '';
      const instructorDisplayedName: string = instructor.isDisplayedToStudents ? instructor.displayedName : '';

      const instructorForm: FormGroup = this.fb.group({
        googleId: [{ value: instructor.googleId, disabled: true }],
        name: [{ value: instructor.name, disabled: true }],
        email: [{ value: instructorEmail, disabled: true }],
        isDisplayedToStudents: [{ value: instructor.isDisplayedToStudents, disabled: true }],
        displayedName: [{ value: instructorDisplayedName, disabled: true }],
        role: [{ value: instructor.role }],
        privileges: [{ value: defaultPrivileges }],
        tunePermissions: this.fb.group({
          permissionsForCourse: this.fb.group(instructor.privileges.courseLevel),
          tuneSectionGroupPermissions: this.fb.array([]),
        }),
      });

      (instructorForm.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions =
          this.initSectionGroupPermissions(instructor);

      // Listen for changes to custom privileges
      const roleControl: (AbstractControl | null) = instructorForm.get('role');
      const permissionsControl: (FormGroup | null) = instructorForm.get('tunePermissions') as FormGroup;

      if (roleControl != null && permissionsControl != null) {
        roleControl.valueChanges.subscribe((selectedRole: string) => {
          const panelId: string = `tune-permissions-${index}`;
          const panel: (HTMLElement | null) = document.getElementById(panelId);

          if (selectedRole === 'Custom' && panel != null) {
            panel.style.display = 'block';
            permissionsControl.controls.permissionsForCourse.reset(this.instructorList[index].privileges.courseLevel);
            permissionsControl.controls.tuneSectionGroupPermissions =
                this.initSectionGroupPermissions(this.instructorList[index]);
          } else if (panel != null) {
            panel.style.display = 'none';
          }
        });
      }

      control.push(instructorForm);
    });

    this.formEditInstructors.controls.formInstructors = control;
  }

  /**
   * Initialises section permissions for section group panels.
  private initSectionGroupPermissions(instructor: InstructorAttributes): FormArray {
    const tuneSectionGroupPermissions: FormArray = this.fb.array([]);

    // Initialise section level privileges for each section group
    Object.keys(instructor.privileges.sectionLevel).forEach((sectionName: string) => {
      const sectionPrivileges: { [key: string]: SectionLevelPrivileges } = instructor.privileges.sectionLevel;
      const sectionPrivilegesForSection: SectionLevelPrivileges = sectionPrivileges[sectionName];
      const specialSectionPermissions: FormGroup = this.fb.group({
        permissionsForSection: this.fb.group(sectionPrivilegesForSection),
        permissionsForSessions: this.fb.group({}),
      });

      specialSectionPermissions.addControl(sectionName, this.fb.control(true));

      // Initialise remaining controls for non-special sections
      this.sectionNames.forEach((section: string) => {
        if (section !== sectionName) {
          specialSectionPermissions.addControl(section, this.fb.control(false));
        }
      });

      // Listen for specific section value changes
      const sectionLevel: FormGroup = specialSectionPermissions.controls.permissionsForSection as FormGroup;

      sectionLevel.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToView: boolean) => {
        if (!isAbleToView) {
          sectionLevel.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      sectionLevel.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
        if (isAbleToModify) {
          sectionLevel.controls.canviewsessioninsection.setValue(true);
        }
      });

      // Initialise session level privileges for each section
      const sessionPrivilegesForSection: { [session: string]: SessionLevelPrivileges } =
          instructor.privileges.sessionLevel[sectionName];

      this.feedbackNames.forEach((feedback: string) => {
        let sessionPrivileges: FormGroup;
        if (sessionPrivilegesForSection != null && sessionPrivilegesForSection[feedback] != null) {
          sessionPrivileges = this.fb.group(sessionPrivilegesForSection[feedback]);
        } else {
          sessionPrivileges = this.fb.group({
            cansubmitsessioninsection: false,
            canviewsessioninsection: false,
            canmodifysessioncommentinsection: false,
          });
        }

        // Listen for specific session value changes
        sessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
          if (!isAbleToSubmit) {
            sessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
          }
        });

        sessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
            .subscribe((isAbleToModify: boolean) => {
              if (isAbleToModify) {
                sessionPrivileges.controls.canviewsessioninsection.setValue(true);
              }
            });

        (specialSectionPermissions.controls.permissionsForSessions as FormGroup)
            .addControl(feedback, sessionPrivileges);
      });

      tuneSectionGroupPermissions.push(specialSectionPermissions);
    });

    return tuneSectionGroupPermissions;
  }
  **/
}
