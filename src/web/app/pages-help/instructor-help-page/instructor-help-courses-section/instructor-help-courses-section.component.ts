import { NgIf } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CoursesSectionQuestions } from './courses-section-questions';
import { environment } from '../../../../environments/environment';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
import { TeammatesRouterDirective } from '../../../components/teammates-router/teammates-router.directive';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { Sections } from '../sections';

/**
 * Courses section of the Instructor Help Page
 */
@Component({
  selector: 'tm-instructor-help-courses-section',
  templateUrl: './instructor-help-courses-section.component.html',
  styleUrls: ['./instructor-help-courses-section.component.scss'],
  animations: [collapseAnim],
  imports: [
    NgIf,
    InstructorHelpPanelComponent,
    TeammatesRouterDirective,
  ],
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enums
  CoursesSectionQuestions: typeof CoursesSectionQuestions = CoursesSectionQuestions;
  Sections: typeof Sections = Sections;

  readonly supportEmail: string = environment.supportEmail;

  readonly questionsOrder: string[] = [
    CoursesSectionQuestions.COURSE_ADD_STUDENTS,
    CoursesSectionQuestions.SIZE_LIMIT,
    CoursesSectionQuestions.NO_TEAMS,
    CoursesSectionQuestions.SECTIONS,
    CoursesSectionQuestions.ENROLL_SECTIONS,
    CoursesSectionQuestions.COURSE_ADD_INSTRUCTOR,
    CoursesSectionQuestions.COURSE_EDIT_INSTRUCTOR,
    CoursesSectionQuestions.COURSE_INSTRUCTOR_ACCESS,
    CoursesSectionQuestions.PRIVILEGES,
    CoursesSectionQuestions.COURSE_VIEW_STUDENTS,
    CoursesSectionQuestions.CHANGE_SECTION,
    CoursesSectionQuestions.DISAPPEARED_COURSE,
    CoursesSectionQuestions.DEL_STUDENTS,
    CoursesSectionQuestions.COURSE_VIEW_DELETED,
    CoursesSectionQuestions.COURSE_RESTORE,
    CoursesSectionQuestions.PERM_DEL,
    CoursesSectionQuestions.RESTORE_ALL,
  ];

  @Output() collapseStudentEditDetails: EventEmitter<any> = new EventEmitter();

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }

}
