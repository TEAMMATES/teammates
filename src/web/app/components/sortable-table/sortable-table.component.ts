import { Component, Input, OnInit } from '@angular/core';
import { SortOrder } from '../../../types/sort-properties';

/**
 * Column data for sortable table
 */
export interface ColumnData<T> {
  header: string;
  headerToolTip?: string;
  sortBy?(item1: T, item2: T): number; // Don't provide this field if you don't want this column to be sortable
}

/**
 * Displays a sortable table, sorting by clicking on the header
 * Optional sorting function to be inputted provided for each column
 * Columns and rows provided must be aligned
 */
@Component({
  selector: 'tm-sortable-table',
  templateUrl: './sortable-table.component.html',
  styleUrls: ['./sortable-table.component.scss'],
})
export class SortableTableComponent implements OnInit {

  @Input()
  columns: ColumnData<any>[] = [];

  @Input()
  rows: any[][] = [];

  columnToSortBy: string = '';
  sortOrder: SortOrder = SortOrder.ASC;
  tableRows: any[][] = [];

  // enum
  SortOrder: typeof SortOrder = SortOrder;

  constructor() { }

  ngOnInit(): void {
    this.tableRows = this.rows;
  }

  ngOnChanges(): void {
    this.tableRows = this.rows;
    this.sortRows();
  }

  onClickHeader(columnHeader: string): void {
    this.sortOrder = (this.columnToSortBy === columnHeader) ?
        this.sortOrder === SortOrder.ASC ?
            SortOrder.DESC :
            SortOrder.ASC :
        SortOrder.ASC;
    this.columnToSortBy = columnHeader;
    this.sortRows();
  }

  sortRows(): void {
    if (!this.columnToSortBy) {
      return;
    }
    const columnIndex: number = this.columns.findIndex(
        (column: ColumnData<any>) => column.header === this.columnToSortBy);
    if (columnIndex < 0) {
      return;
    }
    const sortFn: ((item1: any, item2: any) => number) | undefined = this.columns[columnIndex].sortBy;
    if (!sortFn) {
      return;
    }

    this.tableRows.sort((row1: any[], row2: any[]) =>
        sortFn(this.sortOrder === SortOrder.ASC ? row1[columnIndex] : row2[columnIndex],
            this.sortOrder === SortOrder.ASC ? row2[columnIndex] : row1[columnIndex]));
  }

}
