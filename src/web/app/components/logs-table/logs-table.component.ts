import { Component, Input, OnInit } from '@angular/core';
import { LogsTableRowModel } from './logs-table-model';

/**
 * A table to display logs.
 */
@Component({
  selector: 'tm-logs-table',
  templateUrl: './logs-table.component.html',
  styleUrls: ['./logs-table.component.scss'],
})
export class LogsTableComponent implements OnInit {

  @Input()
  logs: LogsTableRowModel[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  expandDetails(logsTableRowModel: LogsTableRowModel) {
    logsTableRowModel.isDetailsExpanded
      ? logsTableRowModel.isDetailsExpanded = false
      : logsTableRowModel.isDetailsExpanded = true;
  }
}
