import { Component, OnInit } from '@angular/core';
import { InstructorHelpDataSharingService } from '../../../../services/instructor-help-data-sharing.service';
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

  constructor(private data: InstructorHelpDataSharingService) {
    super();
  }

  ngOnInit(): void {
    this.data.currPeerEvalTips.subscribe((isCurrPeerEvalTipsCollapsed: boolean) =>
        this.isPeerEvalTipsCollapsed = isCurrPeerEvalTipsCollapsed,
    );
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
