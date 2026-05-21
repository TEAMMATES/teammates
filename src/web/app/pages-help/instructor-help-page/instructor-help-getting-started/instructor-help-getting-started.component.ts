import { Component, DOCUMENT, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PageScrollService } from 'ngx-page-scroll-core';
import { environment } from '../../../../environments/environment';
import { CourseEditFormMode } from '../../../components/course-edit-form/course-edit-form-model';
import { CourseEditFormComponent } from '../../../components/course-edit-form/course-edit-form.component';
import { TeammatesRouterDirective } from '../../../components/teammates-router/teammates-router.directive';
import { ExampleBoxComponent } from '../example-box/example-box.component';
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
  imports: [TeammatesRouterDirective, ExampleBoxComponent, CourseEditFormComponent],
})
export class InstructorHelpGettingStartedComponent {
  private route = inject(ActivatedRoute);
  private pageScrollService = inject(PageScrollService);
  private document = inject(DOCUMENT);

  // enum
  StudentsSectionQuestions!: typeof StudentsSectionQuestions;
  CoursesSectionQuestions!: typeof CoursesSectionQuestions;
  SessionsSectionQuestions!: typeof SessionsSectionQuestions;
  QuestionsSectionQuestions!: typeof QuestionsSectionQuestions;
  CourseEditFormMode!: typeof CourseEditFormMode;
  Sections!: typeof Sections;

  readonly supportEmail: string = environment.supportEmail;
  instructorHelpPath = '';

  constructor() {
    this.StudentsSectionQuestions = StudentsSectionQuestions;
    this.CoursesSectionQuestions = CoursesSectionQuestions;
    this.SessionsSectionQuestions = SessionsSectionQuestions;
    this.QuestionsSectionQuestions = QuestionsSectionQuestions;
    this.CourseEditFormMode = CourseEditFormMode;
    this.Sections = Sections;

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
        duration: 500,
        scrollTarget: `#${target}`,
        scrollOffset: 70,
      });
    }
    return false;
  }
}
