import { Component, Input, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Student, StudentProfile } from '../../../../types/api-output';
import {
  SearchStudentsListRowTable,
} from '../../../pages-instructor/instructor-search-page/student-result-table/student-result-table.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import {
  EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES,
  EXAMPLE_SINGLE_STUDENT_RESULT_TABLES,
  EXAMPLE_STUDENT_ATTRIBUTES,
  EXAMPLE_STUDENT_PROFILE,
} from './instructor-help-students-data';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;
  readonly exampleStudentProfile: StudentProfile = EXAMPLE_STUDENT_PROFILE;
  readonly exampleStudentAttributes: Student = EXAMPLE_STUDENT_ATTRIBUTES;
  readonly exampleSingleStudentResultTables: SearchStudentsListRowTable[] = EXAMPLE_SINGLE_STUDENT_RESULT_TABLES;
  readonly exampleMultipleStudentResultTables: SearchStudentsListRowTable[] = EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES;

  @Input() isEditDetailsCollapsed: boolean = false;
  isViewProfileCollapsed: boolean = false;
  isViewAllResponsesCollapsed: boolean = false;
  isStudentSearchCollapsed: boolean = false;
  isStudentEmailCollapsed: boolean = false;
  isGoogleAccountCollapsed: boolean = false;
  isChangeGoogleIdCollapsed: boolean = false;

  constructor() {
    super();
  }
}
