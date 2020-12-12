import { DOCUMENT } from '@angular/common';
import { AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { PageScrollService } from 'ngx-page-scroll-core';
import { environment } from '../../../environments/environment';
import { InstructorHelpCoursesSectionComponent } from './instructor-help-courses-section/instructor-help-courses-section.component';
import { InstructorHelpQuestionsSectionComponent } from './instructor-help-questions-section/instructor-help-questions-section.component';
import { InstructorHelpSessionsSectionComponent } from './instructor-help-sessions-section/instructor-help-sessions-section.component';
import { SessionsSectionQuestions } from './instructor-help-sessions-section/sessions-section-questions';
import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section/instructor-help-students-section.component';
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
export class InstructorHelpPageComponent implements OnInit, AfterViewInit {
  // enum
  Sections: typeof Sections = Sections;
  readonly supportEmail: string = environment.supportEmail;
  instructorGettingStartedPath: string = '';
  searchTerm: String = '';
  key: String = '';
  matchFound: number = 0;

  questionIdToExpand: string = '';
  section: string = '';

  @ViewChild('helpPage') bodyRef ?: ElementRef;
  @ViewChild('studentsHelpSection') studentsHelpSection?: InstructorHelpStudentsSectionComponent;
  @ViewChild('coursesHelpSection') coursesHelpSection?: InstructorHelpCoursesSectionComponent;
  @ViewChild('sessionsHelpSection') sessionsHelpSection?: InstructorHelpSessionsSectionComponent;
  @ViewChild('questionsHelpSection') questionsHelpSection?: InstructorHelpQuestionsSectionComponent;

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

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.route.queryParams.subscribe((queryParam: Params) => {
      if (queryParam.questionId && queryParam.section) {
        this.questionIdToExpand = queryParam.questionId;
        this.section = queryParam.section;
        this.scrollTo(queryParam.questionId);
      }
    });
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
    if (this.bodyRef) {
      const el: any = Array.prototype.slice
          .call(this.bodyRef.nativeElement.childNodes).find((x: any) => x.id === section);
      if (el) {
        el.scrollIntoView();
        window.scrollBy(0, -50);
      }
    }
  }

  scrollTo(target: string, timeout?: number): void {
    this.expandQuestionTab();
    setTimeout(() => this.pageScrollService.scroll({
      document: this.document,
      duration: 500,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    }), timeout ? timeout : 500);
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
