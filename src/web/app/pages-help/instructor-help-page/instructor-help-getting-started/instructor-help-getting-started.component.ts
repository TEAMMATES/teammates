import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs/operators';
import { CourseEditFormMode } from '../../../components/course-edit-form/course-edit-form-model';
import { CourseEditFormComponent } from '../../../components/course-edit-form/course-edit-form.component';
import { ConfigService } from '../../../../services/config.service';
import { PageScrollService } from '../../../../services/page-scroll.service';
import { RouterLink } from '@angular/router';
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
  imports: [RouterLink, ExampleBoxComponent, CourseEditFormComponent],
})
export class InstructorHelpGettingStartedComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly pageScrollService = inject(PageScrollService);
  private readonly configService = inject(ConfigService);

  // enum
  StudentsSectionQuestions!: typeof StudentsSectionQuestions;
  CoursesSectionQuestions!: typeof CoursesSectionQuestions;
  SessionsSectionQuestions!: typeof SessionsSectionQuestions;
  QuestionsSectionQuestions!: typeof QuestionsSectionQuestions;
  CourseEditFormMode!: typeof CourseEditFormMode;
  Sections!: typeof Sections;

  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });
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
    r.data.subscribe((resp) => {
      this.instructorHelpPath = resp['instructorHelpPath'];
    });
  }

  /**
   * To scroll to a specific HTML id
   */
  jumpTo(target: string): boolean {
    const destination: Element | null = document.getElementById(target);
    if (destination) {
      this.pageScrollService.scrollToAnchor(target);
    }
    return false;
  }
}
