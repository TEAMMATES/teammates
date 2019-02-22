import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Instructor } from '../../../Instructor';
import {
  CourseLevelPrivileges, DefaultPrivileges, Privileges, Role, SectionLevelPrivileges, SessionLevelPrivileges,
} from '../instructor-privileges-model';
import { InstructorEditFormMode, InstructorEditFormModel } from './instructor-edit-form-model';
import {
  InstructorEditSectionPrivilegesFormModel, InstructorEditSessionPrivilegesFormModel,
} from "./instructor-edit-section-privileges-form/instructor-edit-section-privileges-form-model";

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
    courseLevel: DefaultPrivileges.COOWNER.value.courseLevel,
    instructorEditSectionPrivilegesFormModels: [],

    isEditable: false,
    isSaving: false,
  };

  @Input()
  modelNumber: number = 1;

  @Input()
  canModifyInstructor: boolean = false;

  @Input()
  sectionNames: string[] = [];

  @Input()
  sessionNames: string[] = [];

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
    //initialise variables and constants
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
   * Triggers the change of a model course level privilege.
   */
  triggerModelChangeForCourseLevelPrivileges(privilege: string, hasPrivilege: boolean): void {
    const coursePrivileges: CourseLevelPrivileges = this.model.courseLevel;
    coursePrivileges[privilege] = hasPrivilege;

    // check for specific value changes
    if (privilege == 'canviewsessioninsection' && !hasPrivilege) {
      coursePrivileges.canmodifysessioncommentinsection = false;
    }

    if (privilege == 'canmodifysessioncommentinsection' && hasPrivilege) {
      coursePrivileges.canviewsessioninsection = true;
    }

    this.triggerModelChange('courseLevel', coursePrivileges);
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
   * Tracks the instructor edit form by instructor google id.
   */
  trackInstructorEditSectionPrivilegesFormByFn(_: any, item: InstructorEditSectionPrivilegesFormModel): any {
    return item.sections;
  }

  /**
   * Handles delete section privileges button click event.
   */
  deleteSectionPrivilegesFormHandler(index: number): void {
    const sectionPrivileges: InstructorEditSectionPrivilegesFormModel[] =
        this.model.instructorEditSectionPrivilegesFormModels;

    sectionPrivileges.splice(index, 1);
    this.triggerModelChange('instructorEditSectionPrivilegesFormModels', sectionPrivileges);
  }

  /**
   * Adds a new instructor edit section privileges model.
   */
  addEditSectionPrivilegesModel(): void {
    const sectionPrivileges: InstructorEditSectionPrivilegesFormModel[] =
        this.model.instructorEditSectionPrivilegesFormModels;

    const courseLevelAsSectionLevel: SectionLevelPrivileges = {
      canviewstudentinsection: this.model.courseLevel.canviewstudentinsection,
      canviewsessioninsection: this.model.courseLevel.canviewsessioninsection,
      cansubmitsessioninsection: this.model.courseLevel.cansubmitsessioninsection,
      canmodifysessioncommentinsection: this.model.courseLevel.canmodifysessioncommentinsection,
    };

    const defaultUncheckedSectionsMap: { [section: string]: boolean } = {};
    this.sectionNames.forEach((section: string) => {
      defaultUncheckedSectionsMap[section] = false;
    });

    const instructorEditSectionPrivilegesFormModel: InstructorEditSectionPrivilegesFormModel = {
      sections: defaultUncheckedSectionsMap,
      sectionLevel: courseLevelAsSectionLevel,
      instructorEditSessionPrivilegesFormModels: this.getInstructorEditSessionPrivilegesFormModels(),

      isSessionPrivilegesVisible: true,
    };

    sectionPrivileges.push(instructorEditSectionPrivilegesFormModel);

    this.triggerModelChange('instructorEditSectionPrivilegesFormModels', sectionPrivileges);
  }

  /**
   * Converts an instructor's privileges to a session privilege form model.
   */
  private getInstructorEditSessionPrivilegesFormModels(): InstructorEditSessionPrivilegesFormModel[] {
    let instructorEditSessionPrivilegesFormModels: InstructorEditSessionPrivilegesFormModel[] = [];

    this.sessionNames.forEach((session: string) => {
      const courseLevelAsSessionLevel: SessionLevelPrivileges = {
        canviewsessioninsection: this.model.courseLevel.canviewsessioninsection,
        cansubmitsessioninsection: this.model.courseLevel.cansubmitsessioninsection,
        canmodifysessioncommentinsection: this.model.courseLevel.canmodifysessioncommentinsection,
      };

      const instructorEditSessionPrivilegesFormModel: InstructorEditSessionPrivilegesFormModel = {
        sessionName: session,
        sessionLevel: courseLevelAsSessionLevel,
      };
      instructorEditSessionPrivilegesFormModels.push(instructorEditSessionPrivilegesFormModel);
    });

    return instructorEditSessionPrivilegesFormModels;
  }

}
