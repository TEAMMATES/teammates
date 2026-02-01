import { Component, OnInit } from '@angular/core';
import {
  EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES,
  EXAMPLE_SINGLE_STUDENT_RESULT_TABLES,
  EXAMPLE_STUDENT_ATTRIBUTES,
} from './instructor-help-students-data';
import { StudentsSectionQuestions } from './students-section-questions';
import { environment } from '../../../../environments/environment';
import { Student } from '../../../../types/api-output';
import { CourseRelatedInfoComponent } from '../../../components/course-related-info/course-related-info.component';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
import {
  InstructorCourseStudentEditPageComponent,
} from '../../../pages-instructor/instructor-course-student-edit-page/instructor-course-student-edit-page.component';
import {
  InstructorSearchBarComponent,
} from '../../../pages-instructor/instructor-search-page/instructor-search-bar/instructor-search-bar.component';
import {
  SearchStudentsListRowTable,
  StudentResultTableComponent,
} from '../../../pages-instructor/instructor-search-page/student-result-table/student-result-table.component';
import { ExampleBoxComponent } from '../example-box/example-box.component';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { Sections } from '../sections';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
  animations: [collapseAnim],
  imports: [
    InstructorHelpPanelComponent,
    ExampleBoxComponent,
    CourseRelatedInfoComponent,
    InstructorCourseStudentEditPageComponent,
    InstructorSearchBarComponent,
    StudentResultTableComponent,
  ],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enums
  StudentsSectionQuestions: typeof StudentsSectionQuestions = StudentsSectionQuestions;
  Sections: typeof Sections = Sections;

  readonly supportEmail: string = environment.supportEmail;
  readonly exampleStudentAttributes: Student = EXAMPLE_STUDENT_ATTRIBUTES;
  readonly exampleSingleStudentResultTables: SearchStudentsListRowTable[] = EXAMPLE_SINGLE_STUDENT_RESULT_TABLES;
  readonly exampleMultipleStudentResultTables: SearchStudentsListRowTable[] = EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES;

  readonly questionsOrder: string[] = [
    StudentsSectionQuestions.STUDENT_VIEW_PROFILE,
    StudentsSectionQuestions.STUDENT_EDIT_DETAILS,
    StudentsSectionQuestions.STUDENT_VIEW_RESPONSES,
    StudentsSectionQuestions.STUDENT_SEARCH,
    StudentsSectionQuestions.STUDENT_GOOGLE_ACCOUNT,
    StudentsSectionQuestions.STUDENT_CHANGE_ID,
  ];

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }

}
