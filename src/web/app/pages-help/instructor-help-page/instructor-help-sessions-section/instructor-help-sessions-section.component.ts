import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
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

  @Input() collapsePeerEvalTipsInChild: Subject<boolean> = new Subject<boolean>();
  isPeerEvalTipsCollapsed: boolean = false;
  isNewFSessionCollapsed: boolean = false;
  isAddQsCollapsed: boolean = false;
  isPreviewCollapsed: boolean = false;
  isCannotSubmitCollapsed: boolean = false;
  isViewResCollapsed: boolean = false;
  isViewAllRespCollapsed: boolean = false;
  isAddCmntCollapsed: boolean = false;
  isEditDelCmntCollapsed: boolean = false;
  isSearchCollapsed: boolean = false;
  isViewDelCollapsed: boolean = false;
  isRestoreSnCollapsed: boolean = false;
  isDelSnCollapsed: boolean = false;
  isRestoreDelAllCollapsed: boolean = false;

  constructor() {
    super();
  }

  ngOnInit(): void {
    this.collapsePeerEvalTipsInChild.subscribe(
        (isPeerEvalTipsCollapsed: boolean) => this.isPeerEvalTipsCollapsed = isPeerEvalTipsCollapsed);
  }

  /**
   * To scroll to a specific HTML id
   */
  jumpTo(target: string): boolean {
    const destination: Element | null = document.getElementById(target);
    if (destination) {
      destination.scrollIntoView();
      // to prevent the navbar from covering the text
      window.scrollTo(0, window.pageYOffset - 50);
    }
    return false;
  }

}
