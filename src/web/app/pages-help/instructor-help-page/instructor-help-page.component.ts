import { AfterViewInit, Component, ViewChild, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, Params } from '@angular/router';
import { InstructorHelpCoursesSectionComponent } from './instructor-help-courses-section/instructor-help-courses-section.component';
import { InstructorHelpGeneralSectionComponent } from './instructor-help-general-section/instructor-help-general-section.component';
import { InstructorHelpQuestionsSectionComponent } from './instructor-help-questions-section/instructor-help-questions-section.component';
import { InstructorHelpSessionsSectionComponent } from './instructor-help-sessions-section/instructor-help-sessions-section.component';
import { SessionsSectionQuestions } from './instructor-help-sessions-section/sessions-section-questions';
import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section/instructor-help-students-section.component';
import { StudentsSectionQuestions } from './instructor-help-students-section/students-section-questions';
import { Sections } from './sections';
import { map } from 'rxjs/operators';
import { ConfigService } from '../../../services/config.service';
import { PageScrollService } from '../../../services/page-scroll.service';
import { RouterLink } from '@angular/router';

/**
 * Instructor help page.
 */
@Component({
  selector: 'tm-instructor-help-page',
  templateUrl: './instructor-help-page.component.html',
  styleUrls: ['./instructor-help-page.component.scss'],
  imports: [
    RouterLink,
    FormsModule,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpCoursesSectionComponent,
    InstructorHelpSessionsSectionComponent,
    InstructorHelpQuestionsSectionComponent,
    InstructorHelpGeneralSectionComponent,
  ],
})
export class InstructorHelpPageComponent implements AfterViewInit {
  private readonly route = inject(ActivatedRoute);
  private readonly pageScrollService = inject(PageScrollService);
  private readonly configService = inject(ConfigService);

  // enum
  Sections!: typeof Sections;
  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });
  instructorGettingStartedPath = '';
  searchTerm = '';
  key = '';
  matchFound = 0;

  questionIdToExpand = '';
  section = '';

  @ViewChild('studentsHelpSection') studentsHelpSection?: InstructorHelpStudentsSectionComponent;
  @ViewChild('coursesHelpSection') coursesHelpSection?: InstructorHelpCoursesSectionComponent;
  @ViewChild('sessionsHelpSection') sessionsHelpSection?: InstructorHelpSessionsSectionComponent;
  @ViewChild('questionsHelpSection') questionsHelpSection?: InstructorHelpQuestionsSectionComponent;
  @ViewChild('generalHelpSection') generalHelpSection?: InstructorHelpGeneralSectionComponent;

  constructor() {
    this.Sections = Sections;
    let r: ActivatedRoute = this.route;
    while (r.firstChild) {
      r = r.firstChild;
    }
    r.data.subscribe((resp: Data) => {
      this.instructorGettingStartedPath = resp['instructorGettingStartedPath'];
    });
  }

  ngAfterViewInit(): void {
    let target = '';
    this.route.queryParams.subscribe((queryParam: Params) => {
      if (queryParam['section']) {
        this.section = queryParam['section'];
        target = this.section;
        if (queryParam['questionId']) {
          this.questionIdToExpand = queryParam['questionId'];
          target = this.questionIdToExpand;
          this.expandQuestionTab();
        }
      }
    });
    if (target) {
      this.scrollTo(target);
    }
  }

  expandQuestionTab(): void {
    if (this.section === Sections.students && this.studentsHelpSection) {
      this.studentsHelpSection.expand(this.questionIdToExpand);
    } else if (this.section === Sections.courses && this.coursesHelpSection) {
      this.coursesHelpSection.expand(this.questionIdToExpand);
    } else if (this.section === Sections.sessions && this.sessionsHelpSection) {
      this.sessionsHelpSection.expand(this.questionIdToExpand);
    } else if (this.section === Sections.questions && this.questionsHelpSection) {
      this.questionsHelpSection.expand(this.questionIdToExpand);
    } else if (this.section === Sections.general && this.generalHelpSection) {
      this.generalHelpSection.expand(this.questionIdToExpand);
    }
  }

  /**
   * Filters the help contents and displays only those that matches the filter.
   */
  search(): void {
    if (this.searchTerm === '') {
      this.clear();
      return;
    }
    const nxtKey: string = this.searchTerm.toLowerCase();
    if (nxtKey !== this.key) {
      this.matchFound = 0;
    }
    this.key = nxtKey;
  }

  /**
   * Scrolls to the section passed in
   */
  scroll(section: string): void {
    this.pageScrollService.scrollToAnchor(section);
  }

  scrollTo(target: string, timeout?: number): void {
    setTimeout(() => this.pageScrollService.scrollToAnchor(target), timeout ?? 500);
  }

  /**
   * Clears the filter used for search.
   */
  clear(): void {
    this.searchTerm = '';
    this.key = '';
  }

  /**
   * Collapses question card on student edit details in Students section.
   */
  collapseStudentEditDetails(): void {
    this.questionIdToExpand = StudentsSectionQuestions.STUDENT_EDIT_DETAILS;
    this.section = Sections.students;
    this.scrollTo(StudentsSectionQuestions.STUDENT_EDIT_DETAILS, 100);
  }

  /**
   * Collapses question card on peer evaluation tips in Sessions section.
   */
  collapsePeerEvalTips(): void {
    this.questionIdToExpand = SessionsSectionQuestions.TIPS_FOR_CONDUCTION_PEER_EVAL;
    this.section = Sections.sessions;
    this.scrollTo(SessionsSectionQuestions.TIPS_FOR_CONDUCTION_PEER_EVAL, 100);
  }

  updateMatchFoundNumber(n: number): void {
    this.matchFound += n;
  }
}
