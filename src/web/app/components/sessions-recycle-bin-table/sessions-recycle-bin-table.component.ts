import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
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
export class SessionsRecycleBinTableComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  isRecycleBinExpanded: boolean = false;

  @Input()
  recycleBinFeedbackSessionRowModels: RecycleBinFeedbackSessionRowModel[] = [];

  @Input()
  recycleBinFeedbackSessionRowModelsSortBy: SortBy = SortBy.NONE;

  @Input()
  recycleBinFeedbackSessionRowModelsSortOrder: SortOrder = SortOrder.ASC;

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

  constructor() { }

  /**
   * Sorts the list of deleted feedback session row
   */
  sortRecycleBinFeedbackSessionRows(by: SortBy): void {
    this.sortRecycleBinFeedbackSessionRowsEvent.emit(by);
  }

  ngOnInit(): void {
  }

}
