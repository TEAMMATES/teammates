import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalService } from '../../../services/simple-modal.service';
import {
  Course,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { CopySessionModalResult } from '../copy-session-modal/copy-session-modal-model';
import { CopySessionModalComponent } from '../copy-session-modal/copy-session-modal.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableTableCellData,
} from '../sortable-table/sortable-table.component';
import { FormatDateBriefPipe } from '../teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../teammates-common/format-date-detail.pipe';
import { PublishStatusNamePipe } from '../teammates-common/publish-status-name.pipe';
import { SubmissionStatusNamePipe } from '../teammates-common/submission-status-name.pipe';
import { SubmissionStatusTooltipPipe } from '../teammates-common/submission-status-tooltip.pipe';
import { CellWithToolTipComponent } from './cell-with-tooltip.component';
import { GroupButtonsComponent } from './group-buttons.component';
import { PublishStatusTooltipPipe } from './publish-status-tooltip.pipe';
import { ResponseRateComponent } from './response-rate.component';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableHeaderColorScheme,
  SessionsTableRowModel,
} from './sessions-table-model';

/**
 * A table to display a list of feedback sessions.
 */
@Component({
  selector: 'tm-sessions-table',
  templateUrl: './sessions-table.component.html',
  styleUrls: ['./sessions-table.component.scss'],
})
export class SessionsTableComponent implements OnInit {
  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  SessionsTableColumn: typeof SessionsTableColumn = SessionsTableColumn;
  FeedbackSessionSubmissionStatus: typeof FeedbackSessionSubmissionStatus =
    FeedbackSessionSubmissionStatus;
  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus =
    FeedbackSessionPublishStatus;
  SessionsTableHeaderColorScheme: typeof SessionsTableHeaderColorScheme =
    SessionsTableHeaderColorScheme;

  // variable
  rowClicked: number = -1;

  @Input()
  sessionsTableRowModels: SessionsTableRowModel[] = [];

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  columnsToShow: SessionsTableColumn[] = [SessionsTableColumn.COURSE_ID];

  @Input()
  sessionsTableRowModelsSortBy: SortBy = SortBy.NONE;

  @Input()
  sessionsTableRowModelsSortOrder: SortOrder = SortOrder.ASC;

  @Input()
  headerColorScheme: SessionsTableHeaderColorScheme =
    SessionsTableHeaderColorScheme.BLUE;

  @Input()
  isSendReminderLoading: boolean = false;

  @Output()
  sortSessionsTableRowModelsEvent: EventEmitter<SortBy> = new EventEmitter();

  @Output()
  loadResponseRateEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  moveSessionToRecycleBinEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  copySessionEvent: EventEmitter<CopySessionResult> = new EventEmitter();

  @Output()
  submitSessionAsInstructorEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  publishSessionEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  unpublishSessionEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  resendResultsLinkToStudentsEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  downloadSessionResultsEvent: EventEmitter<number> = new EventEmitter();

  @Output()
  sendRemindersToAllNonSubmittersEvent: EventEmitter<number> =
    new EventEmitter();

  @Output()
  sendRemindersToSelectedNonSubmittersEvent: EventEmitter<number> =
    new EventEmitter();

  tableId = 'sessions-table';
  columnsData: ColumnData[] = [
    { header: 'Session Name', sortBy: SortBy.SESSION_NAME },
    { header: 'Start Date', sortBy: SortBy.SESSION_START_DATE },
    { header: 'End Date', sortBy: SortBy.SESSION_END_DATE },
    { header: 'Submissions' },
    { header: 'Responses' },
    {
      header: 'Response Rate',
      headerToolTip: 'Number of students submitted / Class size',
    },
    {
      header: 'Action(s)',
      alignment: 'center',
    },
  ];
  rowsData: SortableTableCellData[][] = [];

  constructor(
    private ngbModal: NgbModal,
    private simpleModalService: SimpleModalService,
    private formatDateDetailPipe: FormatDateDetailPipe,
    private formatDateBriefPipe: FormatDateBriefPipe,
    private publishStatusName: PublishStatusNamePipe,
    private publishStatusTooltip: PublishStatusTooltipPipe,
    private submissionStatusTooltip: SubmissionStatusTooltipPipe,
    private submissionStatusName: SubmissionStatusNamePipe,
  ) {}

  ngOnInit(): void {
    this.setRowData();
  }

  setRowData(): void {
    this.rowsData = this.sessionsTableRowModels.map(
      (sessionTableRowModel: SessionsTableRowModel, idx: number) => {
        const { feedbackSession } = sessionTableRowModel;
        const {
          submissionStatus,
          submissionStartTimestamp,
          submissionEndTimestamp,
          timeZone,
          publishStatus,
        } = feedbackSession;

        const deadlines = this.getDeadlines(sessionTableRowModel);

        return [
          {
            value: sessionTableRowModel.feedbackSession.feedbackSessionName,
          },
          this.createDateCellWithToolTip(submissionStartTimestamp, timeZone),
          this.createDateCellWithToolTip(submissionEndTimestamp, timeZone),
          this.createCellWithToolTip(
            this.submissionStatusTooltip.transform(submissionStatus, deadlines),
            this.submissionStatusName.transform(submissionStatus, deadlines),
          ),
          this.createCellWithToolTip(
            this.publishStatusTooltip.transform(publishStatus),
            this.publishStatusName.transform(publishStatus),
          ),
          this.createResponseRateComponent(sessionTableRowModel, idx),
          this.createGroupButtonsComponent(sessionTableRowModel, idx),
        ];
      },
    );
  }

  private createGroupButtonsComponent(
    sessionTableRowModel: SessionsTableRowModel,
    idx: number,
  ): any {
    const { feedbackSession, instructorPrivilege } = sessionTableRowModel;

    return {
      customComponent: {
        component: GroupButtonsComponent,
        componentData: {
          courseId: feedbackSession.courseId,
          fsName: feedbackSession.feedbackSessionName,
          instructorPrivileges: instructorPrivilege,
          idx,
          submissionStatus: feedbackSession.submissionStatus,
          publishStatus: feedbackSession.publishStatus,
          onSubmitSessionAsInstructor: () =>
            this.submitSessionAsInstructorEvent.emit(idx),
          isSendReminderLoading: this.isSendReminderLoading,
          rowClicked: this.rowClicked,
          copySession: () => this.copySession(idx),
          moveSessionToRecycleBin: () => this.moveSessionToRecycleBin(idx),
          unpublishSession: () => this.unpublishSession(idx),
          publishSession: () => this.publishSession(idx),
          remindResultsLinkToStudent: () =>
            this.remindResultsLinkToStudent(idx),
          downloadSessionResults: () => this.downloadSessionResults(idx),
          sendRemindersToAllNonSubmitters: () =>
            this.sendRemindersToAllNonSubmitters(idx),
          sendRemindersToSelectedNonSubmitters: () =>
            this.sendRemindersToSelectedNonSubmitters(idx),
          setRowClicked: () => this.setRowClicked(idx),
        },
      },
    };
  }

  private createResponseRateComponent(
    sessionTableRowModel: SessionsTableRowModel,
    idx: number,
  ): any {
    const { responseRate, isLoadingResponseRate } = sessionTableRowModel;
    return {
      customComponent: {
        component: ResponseRateComponent,
        componentData: {
          responseRate,
          empty: responseRate === '',
          isLoading: isLoadingResponseRate,
          onClick: () => {
            this.loadResponseRateEvent.emit({
              idx,
              rowData: this.rowsData[idx],
              columnsData: this.columnsData,
            });
          },
          idx,
        },
      },
    };
  }

  private createCellWithToolTip(toolTip: string, value: string): any {
    return {
      customComponent: {
        component: CellWithToolTipComponent,
        componentData: {
          toolTip,
          value,
        },
      },
    };
  }

  private createDateCellWithToolTip(timestamp: number, timeZone: string): any {
    return {
      value: String(timestamp),
      customComponent: {
        component: CellWithToolTipComponent,
        componentData: {
          toolTip: this.formatDateDetailPipe.transform(timestamp, timeZone),
          value: this.formatDateBriefPipe.transform(timestamp, timeZone),
        },
      },
    };
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortSessionsTableRowModels(by: SortBy): void {
    this.sortSessionsTableRowModelsEvent.emit(by);
  }

  getAriaSort(by: SortBy): String {
    if (by !== this.sessionsTableRowModelsSortBy) {
      return 'none';
    }
    return this.sessionsTableRowModelsSortOrder === SortOrder.ASC
      ? 'ascending'
      : 'descending';
  }

  /**
   * Moves the feedback session to the recycle bin.
   */
  moveSessionToRecycleBin(rowIndex: number): void {
    const modalContent: string =
      'Session will be moved to the recycle bin. '
      + 'This action can be reverted by going to the "Sessions" tab and restoring the desired session(s).';
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Delete session <strong>${this.sessionsTableRowModels[rowIndex].feedbackSession.feedbackSessionName}</strong>?`,
      SimpleModalType.WARNING,
      modalContent,
    );
    modalRef.result.then(
      () => {
        this.moveSessionToRecycleBinEvent.emit(rowIndex);
      },
      () => {},
    );
  }

  /**
   * Copies the feedback session.
   */
  copySession(rowIndex: number): void {
    const modalRef: NgbModalRef = this.ngbModal.open(CopySessionModalComponent);
    const model: SessionsTableRowModel = this.sessionsTableRowModels[rowIndex];
    modalRef.componentInstance.newFeedbackSessionName =
      model.feedbackSession.feedbackSessionName;
    modalRef.componentInstance.courseCandidates = this.courseCandidates;
    modalRef.componentInstance.sessionToCopyCourseId =
      model.feedbackSession.courseId;

    modalRef.result.then(
      (result: CopySessionModalResult) => {
        this.copySessionEvent.emit({
          ...result,
          sessionToCopyRowIndex: rowIndex,
        });
      },
      () => {},
    );
  }

  /**
   * Publishes a feedback session.
   */
  publishSession(rowIndex: number): void {
    const model: SessionsTableRowModel = this.sessionsTableRowModels[rowIndex];

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Publish session <strong>${model.feedbackSession.feedbackSessionName}</strong>?`,
      SimpleModalType.WARNING,
      'An email will be sent to students to inform them that the responses are ready for viewing.',
    );

    modalRef.result.then(
      () => {
        this.publishSessionEvent.emit(rowIndex);
      },
      () => {},
    );
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSession(rowIndex: number): void {
    const model: SessionsTableRowModel = this.sessionsTableRowModels[rowIndex];
    const modalContent: string = `An email will be sent to students to inform them that the session 
      has been unpublished and the session responses will no longer be viewable by students.`;

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Unpublish session <strong>${model.feedbackSession.feedbackSessionName}</strong>?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.unpublishSessionEvent.emit(rowIndex);
      },
      () => {},
    );
  }

  /**
   * Resend links to students to view results.
   */
  remindResultsLinkToStudent(rowIndex: number): void {
    this.resendResultsLinkToStudentsEvent.emit(rowIndex);
  }

  /**
   * Sends e-mails to remind all students and instructors who have not submitted their feedback.
   */
  sendRemindersToAllNonSubmitters(rowIndex: number): void {
    this.sendRemindersToAllNonSubmittersEvent.emit(rowIndex);
  }

  /**
   * Sends e-mails to remind selected students and instructors who have not submitted their feedback.
   */
  sendRemindersToSelectedNonSubmitters(rowIndex: number): void {
    this.sendRemindersToSelectedNonSubmittersEvent.emit(rowIndex);
  }

  /**
   * Triggers the download of session results as a CSV file.
   */
  downloadSessionResults(rowIndex: number): void {
    this.downloadSessionResultsEvent.emit(rowIndex);
  }

  /**
   * Set row number of button clicked.
   */
  setRowClicked(rowIndex: number): void {
    this.rowClicked = rowIndex;
  }

  /**
   * Get the deadlines for student and instructors.
   */
  getDeadlines(model: SessionsTableRowModel): {
    studentDeadlines: Record<string, number>,
    instructorDeadlines: Record<string, number>,
  } {
    return {
      studentDeadlines: model.feedbackSession.studentDeadlines,
      instructorDeadlines: model.feedbackSession.instructorDeadlines,
    };
  }
}
