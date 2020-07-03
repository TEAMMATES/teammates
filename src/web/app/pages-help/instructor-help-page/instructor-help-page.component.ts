import {AfterViewInit, Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import { environment } from '../../../environments/environment';
import { Sections } from './sections';
import {DOCUMENT} from '@angular/common';
import {PageScrollService} from 'ngx-page-scroll-core';

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
  searchTerm: String = '';
  key: String = '';
  isEditDetailsCollapsed: boolean = false;
  isPeerEvalTipsCollapsed: boolean = false;

  @ViewChild('helpPage') bodyRef ?: ElementRef;

  constructor(private route: ActivatedRoute,
              private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: Document) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.route.queryParams.subscribe((queryParam: Params) => {
      if (queryParam.questionId) {
        setTimeout(() => this.pageScrollService.scroll({
          document: this.document,
          scrollTarget: `#${queryParam.questionId}`,
          scrollOffset: 70,
        }), 500);
      }
    });
  }

  /**
   * Filters the help contents and displays only those that matches the filter.
   */
  search(): void {
    if (this.searchTerm !== '') {
      this.key = this.searchTerm.toLowerCase();
    } else {
      this.clear();
    }
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
  collapseStudentEditDetails(event: boolean): void {
    this.isEditDetailsCollapsed = event;
    this.isEditDetailsCollapsed = Object.assign({}, this.isEditDetailsCollapsed);
  }

  /**
   * Collapses question card on peer evaluation tips in Sessions section.
   */
  collapsePeerEvalTips(event: boolean): void {
    this.isPeerEvalTipsCollapsed = event;
    this.isPeerEvalTipsCollapsed = Object.assign({}, this.isPeerEvalTipsCollapsed);
  }
}
