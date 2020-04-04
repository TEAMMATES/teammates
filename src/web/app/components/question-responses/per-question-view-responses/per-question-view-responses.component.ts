import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { ResponseOutput } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';

/**
 * Component to display list of responses for one question.
 */
@Component({
  selector: 'tm-per-question-view-responses',
  templateUrl: './per-question-view-responses.component.html',
  styleUrls: ['./per-question-view-responses.component.scss'],
})
export class PerQuestionViewResponsesComponent implements OnInit, OnChanges {

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input() questionId: string = '';
  @Input() questionDetails: any = {};
  @Input() responses: any[] = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
  @Input() showGiver: boolean = true;
  @Input() showRecipient: boolean = true;
  @Input() session: any = {};

  responsesToShow: any[] = [];
  sortBy: SortBy = SortBy.NONE;
  sortOrder: SortOrder = SortOrder.ASC;

  constructor() { }

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

  sortResponses(by: SortBy): void {
    if (this.sortBy === by) {
      this.sortOrder = this.sortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;
    } else {
      this.sortBy = by;
      this.sortOrder = SortOrder.ASC;
    }
    switch (by) {
      case SortBy.GIVER_TEAM:
        this.responsesToShow
          .sort((a: ResponseOutput, b: ResponseOutput) => a.giverTeam > b.giverTeam ? 1 : -1);
        break;
      case SortBy.GIVER_NAME:
        this.responsesToShow
          .sort((a: ResponseOutput, b: ResponseOutput) => a.giver > b.giver ? 1 : -1);
        break;
      case SortBy.RECIPIENT_TEAM:
        this.responsesToShow
          .sort((a: ResponseOutput, b: ResponseOutput) => a.recipientTeam > b.recipientTeam ? 1 : -1);
        break;
      case SortBy.RECIPIENT_NAME:
        this.responsesToShow
          .sort((a: ResponseOutput, b: ResponseOutput) => a.recipient > b.recipient ? 1 : -1);
        break;
      default:
    }
    if (this.sortOrder === SortOrder.DESC) {
      this.responsesToShow.reverse();
    }
  }

}
