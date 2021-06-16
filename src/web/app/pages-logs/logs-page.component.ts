import { Component, OnInit } from '@angular/core';
import { ColumnData, SortableTableCellData } from '../components/sortable-table/sortable-table.component';

/**
 * Model for searching of logs.
 */
interface SearchLogsFormModel {
  logsType: string;
  logsFrom: number;
}

/**
 * Model for displaying of search result.
 */
interface LogResultModel {
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
}

/**
 * Admin and senior developer logs page.
 */
@Component({
  selector: 'tm-logs-page',
  templateUrl: './logs-page.component.html',
  styleUrls: ['./logs-page.component.scss'],
})
export class LogsPageComponent implements OnInit {

  hoursList: number[] = [];

  formModel: SearchLogsFormModel = {
    logsType: '',
    logsFrom: 0,
  };
  searchResults: LogResultModel[] = [];
  isLoading: boolean = false;
  isSearching: boolean = false;
  hasResult: boolean = true;

  constructor() { }

  ngOnInit(): void {
    for (let i: number = 1; i < 25; i += 1) {
      this.hoursList.push(i);
    }
  }

  getPreviousPageLogs(): void {
    // TODO
  }

  getNextPageLogs(): void {
    // TODO
  }
}
