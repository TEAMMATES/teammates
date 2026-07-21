import { Component, OnInit, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import {
  EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES,
  EXAMPLE_SINGLE_STUDENT_RESULT_TABLES,
  EXAMPLE_STUDENT_ATTRIBUTES,
} from './instructor-help-students-data';
import { StudentsSectionQuestions } from './students-section-questions';
import { map } from 'rxjs/operators';
import { ConfigService } from '../../../../services/config.service';
import { Student } from '../../../../types/api-output';
import { CourseRelatedInfoComponent } from '../../../components/course-related-info/course-related-info.component';
import { InstructorCourseStudentEditPageComponent } from '../../../pages-instructor/instructor-course-student-edit-page/instructor-course-student-edit-page.component';
import { InstructorSearchBarComponent } from '../../../pages-instructor/instructor-search-page/instructor-search-bar/instructor-search-bar.component';
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
  imports: [
    InstructorHelpPanelComponent,
    ExampleBoxComponent,
    CourseRelatedInfoComponent,
    InstructorCourseStudentEditPageComponent,
    InstructorSearchBarComponent,
    StudentResultTableComponent,
    NgbCollapse,
  ],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {
  private readonly configService = inject(ConfigService);

  // enums
  StudentsSectionQuestions!: typeof StudentsSectionQuestions;
  Sections!: typeof Sections;

  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });
  readonly exampleStudentAttributes: Student = EXAMPLE_STUDENT_ATTRIBUTES;
  readonly exampleSingleStudentResultTables: SearchStudentsListRowTable[] = EXAMPLE_SINGLE_STUDENT_RESULT_TABLES;
  readonly exampleMultipleStudentResultTables: SearchStudentsListRowTable[] = EXAMPLE_MULTIPLE_STUDENT_RESULT_TABLES;

  readonly questionsOrder: string[] = [
    StudentsSectionQuestions.STUDENT_VIEW_PROFILE,
    StudentsSectionQuestions.STUDENT_EDIT_DETAILS,
    StudentsSectionQuestions.STUDENT_VIEW_RESPONSES,
    StudentsSectionQuestions.STUDENT_SEARCH,
    StudentsSectionQuestions.STUDENT_ACCOUNT_SIGN_IN,
    StudentsSectionQuestions.STUDENT_CHANGE_ACCOUNT_EMAIL,
  ];

  constructor() {
    super();
    this.StudentsSectionQuestions = StudentsSectionQuestions;
    this.Sections = Sections;
  }

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }
}
