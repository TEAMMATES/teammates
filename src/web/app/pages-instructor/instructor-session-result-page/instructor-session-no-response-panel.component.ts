import { Component, Input, OnChanges, OnInit, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';

/**
 * Instructor sessions results page No Response Panel.
 */
@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss'],
  imports: [TeammatesRouterDirective, PanelChevronComponent, LoadingSpinnerDirective, NgbCollapse],
})
export class InstructorSessionNoResponsePanelComponent implements OnInit, OnChanges {
  private tableComparatorService = inject(TableComparatorService);

  // enum
  FeedbackSessionSubmissionStatus!: typeof FeedbackSessionSubmissionStatus;
  SortBy!: typeof SortBy;
  SortOrder!: typeof SortOrder;

  @Input() isNoResponseStudentsLoaded = false;
  @Input() isDisplayOnly = false;
  @Input() noResponseStudents: Student[] = [];
  @Input() section = '';
  @Input() session: FeedbackSession = {
    feedbackSessionId: '',
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };
  isTabExpanded = false;

  sortBy: SortBy = SortBy.NONE;
  sortOrder: SortOrder = SortOrder.ASC;

  noResponseStudentsInSection: Student[] = [];

  constructor() {
    this.FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
    this.SortBy = SortBy;
    this.SortOrder = SortOrder;
  }

  ngOnInit(): void {
    this.filterStudentsBySection();
  }

  ngOnChanges(): void {
    this.filterStudentsBySection();
  }

  private filterStudentsBySection(): void {
    if (this.section) {
      this.noResponseStudentsInSection = this.noResponseStudents.filter(
        (student: Student) => student.sectionName === this.section,
      );
    } else {
      this.noResponseStudentsInSection = this.noResponseStudents;
    }
  }

  /**
   * Toggles the tab of the no response panel.
   */
  toggleTab(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

  /**
   * Expands the tab of the no response panel.
   */
  expandTab(): void {
    this.isTabExpanded = true;
  }

  /**
   * Collapses the tab of the no response panel.
   */
  collapseTab(): void {
    this.isTabExpanded = false;
  }

  /**
   * Sorts the no response panel.
   */
  sortParticipantsBy(sortBy: SortBy): void {
    this.sortBy = sortBy;
    this.sortOrder = this.sortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;

    this.noResponseStudentsInSection.sort((a: Student, b: Student) => {
      let strA: string;
      let strB: string;
      switch (this.sortBy) {
        case SortBy.TEAM_NAME:
          strA = a.teamName;
          strB = b.teamName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(this.sortBy, this.sortOrder, strA, strB);
    });
  }
}
