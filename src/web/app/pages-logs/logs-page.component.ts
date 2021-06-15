import { Component, OnInit } from '@angular/core';
import { ColumnData, SortableTableCellData } from '../components/sortable-table/sortable-table.component';

/**
 * Model for searching of logs
 */
 interface SearchLogsFormModel {
  logsType: string;
  logsFrom: number;
}

/**
 * Model for displaying of search result
 */
interface LogResultModel {
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
}

@Component({
  selector: 'tm-logs-page',
  templateUrl: './logs-page.component.html',
  styleUrls: ['./logs-page.component.scss']
})
export class LogsPageComponent implements OnInit {

  hours: number[] = [];

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
    this.hours = new Array(24).fill(0).map((_value: number, index:number) => index + 1);
  }
}
