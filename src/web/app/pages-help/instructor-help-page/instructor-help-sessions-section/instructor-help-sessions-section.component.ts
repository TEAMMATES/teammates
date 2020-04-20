import { DOCUMENT } from '@angular/common';
import { Component, Inject, Input, OnInit } from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Sessions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-sessions-section',
  templateUrl: './instructor-help-sessions-section.component.html',
  styleUrls: ['./instructor-help-sessions-section.component.scss'],
})
export class InstructorHelpSessionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  @Input() isPeerEvalTipsCollapsed: boolean = false;
  isNewFeedbackSessionCollapsed: boolean = false;
  isAddQuestionsCollapsed: boolean = false;
  isPreviewCollapsed: boolean = false;
  isCannotSubmitCollapsed: boolean = false;
  isViewResultsCollapsed: boolean = false;
  isViewAllResponsesCollapsed: boolean = false;
  isAddCommentCollapsed: boolean = false;
  isEditDelCommentCollapsed: boolean = false;
  isSearchCollapsed: boolean = false;
  isViewDeletedCollapsed: boolean = false;
  isRestoreSessionCollapsed: boolean = false;
  isDelSessionCollapsed: boolean = false;
  isRestoreDelAllCollapsed: boolean = false;

  constructor(private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
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

}
