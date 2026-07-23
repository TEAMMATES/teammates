import { Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CoursesSectionQuestions } from './courses-section-questions';
import { RouterLink } from '@angular/router';
import { map } from 'rxjs/operators';
import { ConfigService } from '../../../../services/config.service';
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
  imports: [InstructorHelpPanelComponent, RouterLink],
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {
  private readonly configService = inject(ConfigService);

  // enums
  CoursesSectionQuestions!: typeof CoursesSectionQuestions;
  Sections!: typeof Sections;

  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });

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

  @Output() collapseStudentEditDetails: EventEmitter<boolean> = new EventEmitter();

  constructor() {
    super();
    this.CoursesSectionQuestions = CoursesSectionQuestions;
    this.Sections = Sections;
  }

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }
}
