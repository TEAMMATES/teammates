import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackSession } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * Model for a row of recycle bin feedback session
 */
export interface RecycleBinFeedbackSessionRowModel {
  feedbackSession: FeedbackSession;
}

/**
 * A table to display a list of deleted feedback sessions
 */
@Component({
  selector: 'tm-sessions-recycle-bin-table',
  templateUrl: './sessions-recycle-bin-table.component.html',
  styleUrls: ['./sessions-recycle-bin-table.component.scss'],
  animations: [collapseAnim],
})
export class SessionsRecycleBinTableComponent {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  // variable
  rowClicked: number = -1;

  @Input()
  isRecycleBinExpanded: boolean = false;

  @Input()
  recycleBinFeedbackSessionRowModels: RecycleBinFeedbackSessionRowModel[] = [];

  @Input()
  recycleBinFeedbackSessionRowModelsSortBy: SortBy = SortBy.NONE;

  @Input()
  recycleBinFeedbackSessionRowModelsSortOrder: SortOrder = SortOrder.ASC;

  @Input()
  isPermanentDeleteLoading: boolean = false;

  @Output()
  restoreSessionEvent: EventEmitter<RecycleBinFeedbackSessionRowModel> = new EventEmitter();

  @Output()
  restoreAllRecycleBinFeedbackSessionEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  permanentlyDeleteSessionEvent: EventEmitter<RecycleBinFeedbackSessionRowModel> = new EventEmitter();

  @Output()
  permanentDeleteAllSessionsEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  sortRecycleBinFeedbackSessionRowsEvent: EventEmitter<SortBy> = new EventEmitter();

  @Output()
  recycleBinExpandEvent: EventEmitter<any> = new EventEmitter<any>();

  /**
   * Sorts the list of deleted feedback session row
   */
  sortRecycleBinFeedbackSessionRows(by: SortBy): void {
    this.sortRecycleBinFeedbackSessionRowsEvent.emit(by);
  }

  getAriaSort(by: SortBy): String {
    if (by !== this.recycleBinFeedbackSessionRowModelsSortBy) {
      return 'none';
    }
    return this.recycleBinFeedbackSessionRowModelsSortOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  /**
   * Set row number of button clicked.
   */
  setRowClicked(rowIndex: number): void {
    this.rowClicked = rowIndex;
  }

}
