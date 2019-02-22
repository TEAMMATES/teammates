import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {
  InstructorEditSectionPrivilegesFormModel,
  InstructorEditSessionPrivilegesFormModel
} from './instructor-edit-section-privileges-form-model';
import {
  defaultSectionLevelPrivileges,
  SectionLevelPrivileges, SessionLevelPrivileges,
} from '../../instructor-privileges-model';

/**
 * Form to edit instructor section and session level privileges.
 */
@Component({
  selector: 'tm-instructor-edit-section-privileges-form',
  templateUrl: './instructor-edit-section-privileges-form.component.html',
  styleUrls: ['./instructor-edit-section-privileges-form.component.scss']
})
export class InstructorEditSectionPrivilegesFormComponent implements OnInit {

  @Input()
  set formModel(model: InstructorEditSectionPrivilegesFormModel) {
    this.model = model;
  }

  model: InstructorEditSectionPrivilegesFormModel = {
    sections: {},
    sectionLevel: defaultSectionLevelPrivileges,
    instructorEditSessionPrivilegesFormModels: [],

    isSessionPrivilegesVisible: false,
  };

  @Input()
  sectionNames: string[] = []

  @Input()
  sessionNames: string[] = [];

  //event emission
  @Output()
  formModelChange: EventEmitter<InstructorEditSectionPrivilegesFormModel> = new EventEmitter();

  @Output()
  deleteSectionPrivilegesFormEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  ngOnInit(): void {
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
   * Marks a section as having custom section/session level privileges in the model.
   */
  triggerModelChangeForSections(section: string, isSectionSelected: boolean): void {
    const sections: { [section: string]: boolean } = this.model.sections;
    sections[section] = isSectionSelected;

    this.triggerModelChange('sections', sections);
  }

  /**
   * Triggers the change of a model section level privilege.
   */
  triggerModelChangeForSectionLevelPrivileges(privilege: string, hasPrivilege: boolean): void {
    const sectionPrivileges: SectionLevelPrivileges = this.model.sectionLevel;
    sectionPrivileges[privilege] = hasPrivilege;

    // check for specific value changes
    if (privilege == 'canviewsessioninsection' && !hasPrivilege) {
      sectionPrivileges.canmodifysessioncommentinsection = false;
    }

    if (privilege == 'canmodifysessioncommentinsection' && hasPrivilege) {
      sectionPrivileges.canviewsessioninsection = true;
    }
    this.triggerModelChange('sectionLevel', sectionPrivileges);
  }

  /**
   * Triggers the change of a model session level privilege.
   */
  triggerModelChangeForSessionLevelPrivileges(privilege: string, hasPrivilege: boolean, index: number): void {
    const allSessionPrivileges: InstructorEditSessionPrivilegesFormModel[] =
        this.model.instructorEditSessionPrivilegesFormModels;
    const sessionPrivileges: SessionLevelPrivileges = allSessionPrivileges[index].sessionLevel;

    sessionPrivileges[privilege] = hasPrivilege;

    // check for specific value changes
    if (privilege == 'canviewsessioninsection' && !hasPrivilege) {
      sessionPrivileges.canmodifysessioncommentinsection = false;
    }

    if (privilege == 'canmodifysessioncommentinsection' && hasPrivilege) {
      sessionPrivileges.canviewsessioninsection = true;
    }

    allSessionPrivileges[index].sessionLevel = sessionPrivileges;
    this.triggerModelChange('instructorEditSessionPrivilegesFormModels', allSessionPrivileges);
  }

  /**
   * Tracks the instructor edit session privileges form by the session name.
   */
  trackInstructorEditSessionPrivilegesFormByFn(_: any, item: InstructorEditSessionPrivilegesFormModel): any {
    return item.sessionName;
  }

  /**
   * Handles delete section privileges button click event.
   */
  deleteSectionPrivilegesFormHandler(): void {
    this.deleteSectionPrivilegesFormEvent.emit();
  }
}
