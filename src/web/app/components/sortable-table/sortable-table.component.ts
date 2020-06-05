import { Component, Input, OnInit } from '@angular/core';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';

/**
 * Column data for sortable table
 */
export interface ColumnData {
  header: string;
  headerToolTip?: string;
  sortBy?: SortBy; // Don't provide this field if you don't want this column to be sortable
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
  columns: ColumnData[] = [];

  @Input()
  rows: any[][] = [];

  columnToSortBy: string = '';
  sortOrder: SortOrder = SortOrder.ASC;
  tableRows: any[][] = [];

  // enum
  SortOrder: typeof SortOrder = SortOrder;

  constructor(private tableComparatorService: TableComparatorService) { }

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
        (column: ColumnData) => column.header === this.columnToSortBy);
    if (columnIndex < 0) {
      return;
    }
    const sortBy: SortBy | undefined = this.columns[columnIndex].sortBy;
    if (!sortBy) {
      return;
    }

    this.tableRows.sort((row1: any[], row2: any[]) =>
        this.tableComparatorService.compare(
            sortBy, this.sortOrder, String(row1[columnIndex]), String(row2[columnIndex])));
  }

}
