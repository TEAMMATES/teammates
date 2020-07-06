import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';
import { environment } from '../../../../environments/environment';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { StudentsSectionQuestions } from '../instructor-help-students-section/students-section-questions';
import { Sections } from '../sections';
import { CoursesSectionQuestions } from './courses-section-questions';

/**
 * Courses section of the Instructor Help Page
 */
@Component({
  selector: 'tm-instructor-help-courses-section',
  templateUrl: './instructor-help-courses-section.component.html',
  styleUrls: ['./instructor-help-courses-section.component.scss'],
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  StudentsSectionQuestions: typeof StudentsSectionQuestions = StudentsSectionQuestions;
  CoursesSectionQuestions: typeof CoursesSectionQuestions = CoursesSectionQuestions;
  Sections: typeof Sections = Sections;

  readonly supportEmail: string = environment.supportEmail;

  questionsToCollapsed: Record<string, boolean> = {
    [CoursesSectionQuestions.COURSE_ADD_STUDENTS]: false,
    [CoursesSectionQuestions.SIZE_LIMIT]: false,
    [CoursesSectionQuestions.NO_TEAMS]: false,
    [CoursesSectionQuestions.SECTIONS]: false,
    [CoursesSectionQuestions.ENROLL_SECTIONS]: false,
    [CoursesSectionQuestions.COURSE_ADD_INSTRUCTOR]: false,
    [CoursesSectionQuestions.COURSE_EDIT_INSTRUCTOR]: false,
    [CoursesSectionQuestions.COURSE_INSTRUCTOR_ACCESS]: false,
    [CoursesSectionQuestions.PRIVILEGES]: false,
    [CoursesSectionQuestions.COURSE_VIEW_STUDENTS]: false,
    [CoursesSectionQuestions.CHANGE_SECTION]: false,
    [CoursesSectionQuestions.DISAPPEARED_COURSE]: false,
    [CoursesSectionQuestions.DEL_STUDENTS]: false,
    [CoursesSectionQuestions.COURSE_ARCHIVE]: false,
    [CoursesSectionQuestions.COURSE_VIEW_ARCHIVED]: false,
    [CoursesSectionQuestions.COURSE_UNARCHIVE]: false,
    [CoursesSectionQuestions.COURSE_VIEW_DELETED]: false,
    [CoursesSectionQuestions.COURSE_RESTORE]: false,
    [CoursesSectionQuestions.PERM_DEL]: false,
    [CoursesSectionQuestions.RESTORE_ALL]: false,
  };

  @Output() collapseStudentEditDetails: EventEmitter<any> = new EventEmitter();

  constructor(private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  ngOnInit(): void {
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    return false;
  }

  expand(questionId: string): void {
    this.questionsToCollapsed[questionId] = true;
  }
}
