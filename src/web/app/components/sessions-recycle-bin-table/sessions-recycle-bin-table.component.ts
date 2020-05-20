import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackSession } from "../../../types/api-output";
import { SortBy, SortOrder } from "../../../types/sort-properties";

interface RecycleBinFeedbackSessionRowModel {
  feedbackSession: FeedbackSession;
}

@Component({
  selector: 'tm-sessions-recycle-bin-table',
  templateUrl: './sessions-recycle-bin-table.component.html',
  styleUrls: ['./sessions-recycle-bin-table.component.scss']
})
export class SessionsRecycleBinTableComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  isTableExpanded: boolean = false;

  @Input()
  recycleBinFeedbackSessionRowModels: RecycleBinFeedbackSessionRowModel[] = [];

  @Input()
  recycleBinFeedbackSessionRowModelsSortBy: SortBy = SortBy.NONE

  @Input()
  recycleBinFeedbackSessionRowModelsSortOrder: SortOrder = SortOrder.ASC;

  @Output() restoreSessionEvent: EventEmitter<RecycleBinFeedbackSessionRowModel> = new EventEmitter();

  @Output() permanentlyDeleteSessionEvent: EventEmitter<RecycleBinFeedbackSessionRowModel> = new EventEmitter();

  @Output() sortRecycleBinFeedbackSessionRowsEvent: EventEmitter<SortBy> = new EventEmitter();

  constructor() { }

  sortRecycleBinFeedbackSessionRows(by: SortBy): void {
    this.sortRecycleBinFeedbackSessionRowsEvent.emit(by);
  }

  ngOnInit() {
  }

}
