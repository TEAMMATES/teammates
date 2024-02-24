import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { GroupButtonsComponent } from './cell-with-group-buttons.component';
import { ResponseRateComponent } from './cell-with-response-rate.component';
import { CellWithToolTipComponent } from './cell-with-tooltip.component';
import { PublishStatusTooltipPipe } from './publish-status-tooltip.pipe';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableColumnData,
  SessionsTableRowData,
  SessionsTableRowModel,
} from './sessions-table-model';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { Course, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { CopySessionModalResult } from '../copy-session-modal/copy-session-modal-model';
import { CopySessionModalComponent } from '../copy-session-modal/copy-session-modal.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableEvent,
  SortableTableCellData,
  SortableTableHeaderColorScheme,
} from '../sortable-table/sortable-table.component';
import { FormatDateBriefPipe } from '../teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../teammates-common/format-date-detail.pipe';
import { PublishStatusNamePipe } from '../teammates-common/publish-status-name.pipe';
import { SubmissionStatusNamePipe } from '../teammates-common/submission-status-name.pipe';
import { SubmissionStatusTooltipPipe } from '../teammates-common/submission-status-tooltip.pipe';

export type MutateEvent = {
  idx: number,
  rowData: SortableTableCellData[],
  columnsData: ColumnData[],
};

export type Index = number;

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
  FeedbackSessionSubmissionStatus: typeof FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;
  SortableTableHeaderColorScheme: typeof SortableTableHeaderColorScheme = SortableTableHeaderColorScheme;

  // variable
  rowClicked: number = -1;

  @Input()
  initialSortBy: SortBy = SortBy.COURSE_ID;

  @Input()
  sortOrder: SortOrder = SortOrder.ASC;

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  columnsToShow: SessionsTableColumn[] = [SessionsTableColumn.COURSE_ID];

  @Input()
  headerColorScheme: SortableTableHeaderColorScheme = SortableTableHeaderColorScheme.BLUE;

  @Input()
  isSendReminderLoading: boolean = false;

  @Output()
  sortSessionsTableRowModelsEvent: EventEmitter<SortableEvent> = new EventEmitter();

  @Output()
  loadResponseRateEvent: EventEmitter<Index> = new EventEmitter();

  @Output()
  moveSessionToRecycleBinEvent: EventEmitter<Index> = new EventEmitter();

  @Output()
  copySessionEvent: EventEmitter<CopySessionResult> = new EventEmitter();

  @Output()
  submitSessionAsInstructorEvent: EventEmitter<Index> = new EventEmitter();

  @Output()
  publishSessionEvent: EventEmitter<MutateEvent> = new EventEmitter();

  @Output()
  unpublishSessionEvent: EventEmitter<MutateEvent> = new EventEmitter();

  @Output()
  resendResultsLinkToStudentsEvent: EventEmitter<Index> = new EventEmitter();

  @Output()
  downloadSessionResultsEvent: EventEmitter<Index> = new EventEmitter();

  @Output()
  sendRemindersToAllNonSubmittersEvent: EventEmitter<Index> = new EventEmitter();

  @Output()
  sendRemindersToSelectedNonSubmittersEvent: EventEmitter<Index> = new EventEmitter();

  @Input() set sessionsTableRowModels(rowModels: SessionsTableRowModel[]) {
    this.sessionsTableRowModelsVar = rowModels;
    this.setRowData();
  }

  get sessionsTableRowModels(): SessionsTableRowModel[] {
    return this.sessionsTableRowModelsVar;
  }

  private sessionsTableRowModelsVar: SessionsTableRowModel[] = [];
  rowsData: SortableTableCellData[][] = [];
  columnsData: ColumnData[] = [];

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
    this.setColumnData();
    this.setRowData();
  }

  /**
   * Creates the column data for the table.
   *
   * @param config Contains the information to create a column data.
   * @returns An array containing a column's data for the table
   *  if the column is to be shown, otherwise an empty array.
   */
  createColumnData(config: SessionsTableColumnData): ColumnData[] {
    if (!(config?.columnType === undefined) && !this.columnsToShow.includes(config.columnType!)) {
      return [];
    }

    const columnData: ColumnData = {
      header: config.header,
      ...(config.sortBy && { sortBy: config.sortBy }),
      ...(config.headerToolTip && { headerToolTip: config.headerToolTip }),
      ...(config.alignment && { alignment: config.alignment }),
      ...(config.headerClass && { headerClass: config.headerClass }),
    };

    return [columnData];
  }

  /**
   * Creates the row data for the table.
   *
   * @param config Contains the information to create a row data.
   * @returns An array containing a row's data for the table if the row is to be shown,
   * otherwise an empty array.
   */
  createRowData(config: SessionsTableRowData): SortableTableCellData[] {
    if (!(config?.columnType === undefined) && !this.columnsToShow.includes(config.columnType!)) {
      return [];
    }

    const rowData: SortableTableCellData = {
      ...(config.value && { value: config.value }),
      ...(config.displayValue && { displayValue: config.displayValue }),
      ...(config.customComponent && { customComponent: config.customComponent }),
      ...(config.style && { style: config.style }),
    };

    return [rowData];
  }

  setColumnData(): void {
    this.columnsData = [
      ...this.createColumnData({
        columnType: SessionsTableColumn.COURSE_ID,
        header: 'Course ID',
        sortBy: SortBy.COURSE_ID,
        headerClass: 'sort-course-id',
      }),
      ...this.createColumnData({
        header: 'Session Name',
        sortBy: SortBy.SESSION_NAME,
        headerClass: 'sort-session-name',
      }),
      ...this.createColumnData({
        columnType: SessionsTableColumn.START_DATE,
        header: 'Start Date',
        sortBy: SortBy.SESSION_START_DATE,
      }),
      ...this.createColumnData({
        columnType: SessionsTableColumn.END_DATE,
        header: 'End Date',
        sortBy: SortBy.SESSION_END_DATE,
      }),
      ...this.createColumnData({ header: 'Submissions' }),
      ...this.createColumnData({ header: 'Responses' }),
      ...this.createColumnData({
        header: 'Response Rate',
        headerToolTip: 'Number of students submitted / Class size',
      }),
      ...this.createColumnData({
        header: 'Action(s)',
        alignment: 'center',
      }),
    ];
  }

  setRowData(): void {
    this.rowsData = this.sessionsTableRowModelsVar.map((sessionTableRowModel: SessionsTableRowModel) => {
      const { feedbackSession } = sessionTableRowModel;
      const { submissionStatus, submissionStartTimestamp, submissionEndTimestamp, timeZone, publishStatus } =
        feedbackSession;

      const deadlines = this.getDeadlines(sessionTableRowModel);

      return [
        ...this.createRowData({
          columnType: SessionsTableColumn.COURSE_ID,
          value: sessionTableRowModel.feedbackSession.courseId,
        }),
        ...this.createRowData({
          value: sessionTableRowModel.feedbackSession.feedbackSessionName,
        }),
        ...this.createRowData({
          columnType: SessionsTableColumn.START_DATE,
          ...this.createDateCellWithToolTip(submissionStartTimestamp, timeZone),
        }),
        ...this.createRowData({
          columnType: SessionsTableColumn.END_DATE,
          ...this.createDateCellWithToolTip(submissionEndTimestamp, timeZone),
        }),
        ...this.createRowData(
          this.createCellWithToolTip(
            this.submissionStatusTooltip.transform(submissionStatus, deadlines),
            this.submissionStatusName.transform(submissionStatus, deadlines),
          ),
        ),
        ...this.createRowData(
          this.createCellWithToolTip(
            this.publishStatusTooltip.transform(publishStatus),
            this.publishStatusName.transform(publishStatus),
          ),
        ),
        ...this.createRowData(this.createCellWithResponseRateComponent(sessionTableRowModel)),
        ...this.createRowData(this.createCellWithGroupButtonsComponent(sessionTableRowModel)),
      ];
    });
  }

  private createCellWithGroupButtonsComponent(sessionTableRowModel: SessionsTableRowModel): SortableTableCellData {
    const { feedbackSession, instructorPrivilege } = sessionTableRowModel;
    const { courseId, feedbackSessionName, submissionStatus, publishStatus } = feedbackSession;

    return {
      customComponent: {
        component: GroupButtonsComponent,
        componentData: (idx: number) => {
          return {
            idx,
            courseId,
            fsName: feedbackSessionName,
            rowClicked: this.rowClicked,
            publishStatus,
            submissionStatus,
            instructorPrivileges: instructorPrivilege,
            isSendReminderLoading: this.isSendReminderLoading,
            copySession: () => this.copySession(idx),
            setRowClicked: () => this.setRowClicked(idx),
            downloadSessionResults: () => this.downloadSessionResults(idx),
            moveSessionToRecycleBin: () => this.moveSessionToRecycleBin(idx),
            remindResultsLinkToStudent: () => this.remindResultsLinkToStudent(idx),
            sendRemindersToAllNonSubmitters: () => this.sendRemindersToAllNonSubmitters(idx),
            onSubmitSessionAsInstructor: () => this.submitSessionAsInstructorEvent.emit(idx),
            publishSession: () => this.publishSession(idx, this.rowsData[idx], this.columnsData),
            unpublishSession: () => this.unpublishSession(idx, this.rowsData[idx], this.columnsData),
            sendRemindersToSelectedNonSubmitters: () => this.sendRemindersToSelectedNonSubmitters(idx),
          };
        },
      },
    };
  }

  private createCellWithResponseRateComponent(sessionTableRowModel: SessionsTableRowModel): SortableTableCellData {
    const { responseRate, isLoadingResponseRate } = sessionTableRowModel;
    return {
      customComponent: {
        component: ResponseRateComponent,
        componentData: (idx: number) => {
          return {
            idx,
            responseRate,
            empty: responseRate === '',
            isLoading: isLoadingResponseRate,
            onClick: () => {
              this.loadResponseRateEvent.emit(idx);
            },
          };
        },
      },
    };
  }

  private createCellWithToolTip(toolTip: string, value: string): SortableTableCellData {
    return {
      customComponent: {
        component: CellWithToolTipComponent,
        componentData: () => {
          return {
            toolTip,
            value,
          };
        },
      },
    };
  }

  private createDateCellWithToolTip(timestamp: number, timeZone: string): SortableTableCellData {
    return {
      value: String(timestamp),
      customComponent: {
        component: CellWithToolTipComponent,
        componentData: () => {
          return {
            toolTip: this.formatDateDetailPipe.transform(timestamp, timeZone),
            value: this.formatDateBriefPipe.transform(timestamp, timeZone),
          };
        },
      },
    };
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortSessionsTableRowModelsEventHandler(event: { sortBy: SortBy, sortOrder: SortOrder }): void {
    this.sortSessionsTableRowModelsEvent.emit(event);
  }

  /**
   * Moves the feedback session to the recycle bin.
   */
  moveSessionToRecycleBin(idx: number): void {
    const modalContent: string = 'Session will be moved to the recycle bin. '
        + 'This action can be reverted by going to the "Sessions" tab and restoring the desired session(s).';
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete session <strong>${this.sessionsTableRowModels[idx].feedbackSession.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING, modalContent);

    modalRef.result.then(
      () => {
        this.moveSessionToRecycleBinEvent.emit(idx);
        this.rowsData = this.rowsData.filter((_, index) => index !== idx);
        this.sessionsTableRowModelsVar = this.sessionsTableRowModelsVar.filter((_, index) => index !== idx);
        this.setRowData();
      },
      () => {},
    );
  }

  /**
   * Copies the feedback session.
   */
  copySession(rowIndex: number): void {
    const modalRef: NgbModalRef = this.ngbModal.open(CopySessionModalComponent);
    const model: SessionsTableRowModel = this.sessionsTableRowModelsVar[rowIndex];
    modalRef.componentInstance.newFeedbackSessionName = model.feedbackSession.feedbackSessionName;
    modalRef.componentInstance.courseCandidates = this.courseCandidates;
    modalRef.componentInstance.sessionToCopyCourseId = model.feedbackSession.courseId;

    modalRef.result.then((result: CopySessionModalResult) => {
      this.copySessionEvent.emit({
        ...result,
        sessionToCopyRowIndex: rowIndex,
      });
    }, () => {});
  }

  /**
   * Publishes a feedback session.
   */
  publishSession(rowIndex: number, rowData: SortableTableCellData[], columnsData: ColumnData[]): void {
    const model: SessionsTableRowModel = this.sessionsTableRowModelsVar[rowIndex];
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Publish session <strong>${model.feedbackSession.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING,
        'An email will be sent to students to inform them that the responses are ready for viewing.');

    modalRef.result.then(() => {
      this.publishSessionEvent.emit({ idx: rowIndex, rowData, columnsData });
    }, () => {});
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSession(rowIndex: number, rowData: SortableTableCellData[], columnsData: ColumnData[]): void {
    const model: SessionsTableRowModel = this.sessionsTableRowModelsVar[rowIndex];
    const modalContent: string = `An email will be sent to students to inform them that the session 
      has been unpublished and the session responses will no longer be viewable by students.`;

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Unpublish session <strong>${model.feedbackSession.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING, modalContent);

    modalRef.result.then(() => {
      this.unpublishSessionEvent.emit({ idx: rowIndex, rowData, columnsData });
    }, () => {});
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
    studentDeadlines: Record<string, number>, instructorDeadlines: Record<string, number>,
  } {
    return {
      studentDeadlines: model.feedbackSession.studentDeadlines,
      instructorDeadlines: model.feedbackSession.instructorDeadlines,
    };
  }
}
