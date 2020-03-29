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

  isCollapsed: boolean = false;
  isCollapsed1: boolean = false;
  isCollapsed2: boolean = false;
  isCollapsed3: boolean = false;
  isCollapsed4: boolean = false;
  isCollapsed5: boolean = false;
  isCollapsed6: boolean = false;
  isCollapsed7: boolean = false;
  isCollapsed8: boolean = false;
  isCollapsed9: boolean = false;
  isCollapsed10: boolean = false;
  isCollapsed11: boolean = false;
  isCollapsed12: boolean = false;
  isCollapsed13: boolean = false;

  constructor(private data: InstructorHelpDataSharingService) {
    super();
  }

  ngOnInit(): void {
    this.data.currPeerEvalTips.subscribe((isCollapsedCurrPeerEvalTips: boolean) =>
        this.isCollapsed = isCollapsedCurrPeerEvalTips,
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
