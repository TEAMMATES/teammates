import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { CommentTableModalComponent } from '../../comment-box/comment-table-modal/comment-table-modal.component';
import { FeedbackResponseCommentModel } from '../../comment-box/comment-table/comment-table-model';
/**
 * Component to display list of responses for one question.
 */
@Component({
  selector: 'tm-per-question-view-responses',
  templateUrl: './per-question-view-responses.component.html',
  styleUrls: ['./per-question-view-responses.component.scss'],
})
export class PerQuestionViewResponsesComponent implements OnInit, OnChanges {

  @Input() questionDetails: any = {};
  @Input() responses: any[] = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
  @Input() showGiver: boolean = true;
  @Input() showRecipient: boolean = true;

  @Output() commentsChangeInResponse: EventEmitter<any> = new EventEmitter();

  responsesToShow: any[] = [];

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    const responsesToShow: any[] = [];
    for (const response of this.responses) {
      if (this.section) {
        let shouldDisplayBasedOnSection: boolean = true;
        switch (this.sectionType) {
          case InstructorSessionResultSectionType.EITHER:
            shouldDisplayBasedOnSection =
                response.giverSection === this.section || response.recipientSection === this.section;
            break;
          case InstructorSessionResultSectionType.GIVER:
            shouldDisplayBasedOnSection = response.giverSection === this.section;
            break;
          case InstructorSessionResultSectionType.EVALUEE:
            shouldDisplayBasedOnSection = response.recipientSection === this.section;
            break;
          case InstructorSessionResultSectionType.BOTH:
            shouldDisplayBasedOnSection =
                response.giverSection === this.section && response.recipientSection === this.section;
            break;
          default:
        }
        if (!shouldDisplayBasedOnSection) {
          continue;
        }
      }
      responsesToShow.push(response);
    }
    this.responsesToShow = responsesToShow;
  }

  /**
   * Opens the comments table modal.
   */
  triggerShowCommentTableEvent(response: any): void {
    const modalRef: NgbModalRef = this.modalService.open(CommentTableModalComponent);
    modalRef.componentInstance.comments = response.allComments;
    modalRef.componentInstance.response = response;
    modalRef.componentInstance.questionDetails = this.questionDetails;
    modalRef.componentInstance.commentsChange.subscribe((comments: FeedbackResponseCommentModel[]) => {
      // TODO propagate change to parent
      response.allComments = comments;
      modalRef.componentInstance.comments = response.allComments;
    });
  }
}
