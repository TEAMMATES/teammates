import { DOCUMENT } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PageScrollService } from 'ngx-page-scroll-core';
import { environment } from '../../../../environments/environment';
import { CourseEditFormMode } from '../../../components/course-edit-form/course-edit-form-model';
import { CoursesSectionQuestions } from '../instructor-help-courses-section/courses-section-questions';
import { QuestionsSectionQuestions } from '../instructor-help-questions-section/questions-section-questions';
import { SessionsSectionQuestions } from '../instructor-help-sessions-section/sessions-section-questions';
import { StudentsSectionQuestions } from '../instructor-help-students-section/students-section-questions';
import { Sections } from '../sections';

/**
 * Getting Started Section for Instructors
 */
@Component({
  selector: 'tm-instructor-help-getting-started',
  templateUrl: './instructor-help-getting-started.component.html',
  styleUrls: ['./instructor-help-getting-started.component.scss'],
})
export class InstructorHelpGettingStartedComponent {

  // enum
  StudentsSectionQuestions: typeof StudentsSectionQuestions = StudentsSectionQuestions;
  CoursesSectionQuestions: typeof CoursesSectionQuestions = CoursesSectionQuestions;
  SessionsSectionQuestions: typeof SessionsSectionQuestions = SessionsSectionQuestions;
  QuestionsSectionQuestions: typeof QuestionsSectionQuestions = QuestionsSectionQuestions;
  CourseEditFormMode: typeof CourseEditFormMode = CourseEditFormMode;
  Sections: typeof Sections = Sections;

  readonly supportEmail: string = environment.supportEmail;
  instructorHelpPath: string = '';

  constructor(private route: ActivatedRoute,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    let r: ActivatedRoute = this.route;
    while (r.firstChild) {
      r = r.firstChild;
    }
    r.data.subscribe((resp: any) => {
      this.instructorHelpPath = resp.instructorHelpPath;
    });
  }

  /**
   * To scroll to a specific HTML id
   */
  jumpTo(target: string): boolean {
    const destination: Element | null = document.getElementById(target);
    if (destination) {
      this.pageScrollService.scroll({
        document: this.document,
        scrollTarget: `#${target}`,
        scrollOffset: 70,
      });
    }
    return false;
  }

}
