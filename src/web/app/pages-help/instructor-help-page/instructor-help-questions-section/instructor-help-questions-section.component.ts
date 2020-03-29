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
