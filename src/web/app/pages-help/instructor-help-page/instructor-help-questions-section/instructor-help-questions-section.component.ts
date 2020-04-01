import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { InstructorHelpDataSharingService } from '../../../../services/instructor-help-data-sharing.service';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Questions Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-questions-section',
  templateUrl: './instructor-help-questions-section.component.html',
  styleUrls: ['./instructor-help-questions-section.component.scss'],
})
export class InstructorHelpQuestionsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  isEssayQsCollapsed: boolean = false;
  isMCQSingleCollapsed: boolean = false;
  isMCQMultCollapsed: boolean = false;
  isNumScaleCollapsed: boolean = false;
  isPtsOptionsCollapsed: boolean = false;
  isPtsRecipientsCollapsed: boolean = false;
  isContribQsCollapsed: boolean = false;
  isRubricQsCollapsed: boolean = false;
  isRankOptsCollapsed: boolean = false;
  isRankRcptsCollapsed: boolean = false;

  constructor(private modalService: NgbModal, private data: InstructorHelpDataSharingService) {
    super();
  }

  /**
   * Opens modal window.
   */
  openModal(modal: any): void {
    this.modalService.open(modal);
  }

  ngOnInit(): void {
  }

  /**
   * Collapses questions on tips for peer evaluation.
   */
  collapsePeerEvalTips(): void {
    this.data.collapsePeerEvalTips(true);
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
      if (target === 'tips-for-conducting-peer-eval') {
        this.collapsePeerEvalTips();
      }
    }
    return false;
  }
}
