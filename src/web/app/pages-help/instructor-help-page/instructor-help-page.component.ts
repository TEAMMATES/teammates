import { DOCUMENT } from '@angular/common';
import { AfterViewInit, Component, Inject, ViewChild } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { PageScrollService } from 'ngx-page-scroll-core';
import { environment } from '../../../environments/environment';
import {
  InstructorHelpCoursesSectionComponent,
} from './instructor-help-courses-section/instructor-help-courses-section.component';
import {
  InstructorHelpGeneralSectionComponent,
} from './instructor-help-general-section/instructor-help-general-section.component';
import {
  InstructorHelpQuestionsSectionComponent,
} from './instructor-help-questions-section/instructor-help-questions-section.component';
import {
  InstructorHelpSessionsSectionComponent,
} from './instructor-help-sessions-section/instructor-help-sessions-section.component';
import { SessionsSectionQuestions } from './instructor-help-sessions-section/sessions-section-questions';
import {
  InstructorHelpStudentsSectionComponent,
} from './instructor-help-students-section/instructor-help-students-section.component';
import { StudentsSectionQuestions } from './instructor-help-students-section/students-section-questions';
import { Sections } from './sections';

/**
 * Instructor help page.
 */
@Component({
  selector: 'tm-instructor-help-page',
  templateUrl: './instructor-help-page.component.html',
  styleUrls: ['./instructor-help-page.component.scss'],
})
export class InstructorHelpPageComponent implements AfterViewInit {
  // enum
  Sections: typeof Sections = Sections;
  readonly supportEmail: string = environment.supportEmail;
  instructorGettingStartedPath: string = '';
  searchTerm: String = '';
  key: String = '';
  matchFound: number = 0;

  questionIdToExpand: string = '';
  section: string = '';

  @ViewChild('studentsHelpSection') studentsHelpSection?: InstructorHelpStudentsSectionComponent;
  @ViewChild('coursesHelpSection') coursesHelpSection?: InstructorHelpCoursesSectionComponent;
  @ViewChild('sessionsHelpSection') sessionsHelpSection?: InstructorHelpSessionsSectionComponent;
  @ViewChild('questionsHelpSection') questionsHelpSection?: InstructorHelpQuestionsSectionComponent;
  @ViewChild('generalHelpSection') generalHelpSection?: InstructorHelpGeneralSectionComponent;

  constructor(private route: ActivatedRoute,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: Document) {
    let r: ActivatedRoute = this.route;
    while (r.firstChild) {
      r = r.firstChild;
    }
    r.data.subscribe((resp: any) => {
      this.instructorGettingStartedPath = resp.instructorGettingStartedPath;
    });
  }

  ngAfterViewInit(): void {
    let target: string = '';
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
    this.scrollTo(target);
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
    const nxtKey: String = this.searchTerm.toLowerCase();
    if (nxtKey !== this.key) {
      this.matchFound = 0;
    }
    this.key = nxtKey;
  }

  /**
   * Scrolls to the section passed in
   */
  scroll(section: string): void {
    this.pageScrollService.scroll({
      document: this.document,
      duration: 500,
      scrollTarget: `#${section}`,
      scrollOffset: 70,
    });
  }

  scrollTo(target: string, timeout?: number): void {
    setTimeout(() => this.pageScrollService.scroll({
      document: this.document,
      duration: 500,
      scrollTarget: `#${target}`,
      scrollOffset: 60,
    }), timeout || 500);
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
