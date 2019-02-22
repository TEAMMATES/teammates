import { SectionLevelPrivileges, SessionLevelPrivileges } from '../../instructor-privileges-model';

/**
 * The form model of an instructor edit section privileges form.
 */
export interface InstructorEditSectionPrivilegesFormModel {
  sections: { [section: string]: boolean };
  sectionLevel: SectionLevelPrivileges;
  instructorEditSessionPrivilegesFormModels: InstructorEditSessionPrivilegesFormModel[];

  isSessionPrivilegesVisible: boolean;
}

/**
 * The form model of an instructor edit session privileges form.
 */
export interface InstructorEditSessionPrivilegesFormModel {
  sessionName: string;
  sessionLevel: SessionLevelPrivileges;
}
