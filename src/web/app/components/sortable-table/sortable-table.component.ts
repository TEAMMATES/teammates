import { Component, Input, OnInit, Type } from '@angular/core';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';

/**
 * Column data for sortable table
 */
export interface ColumnData {
  header: string;
  headerToolTip?: string;
  sortBy?: SortBy; // optional if the column is not sortable
  component?: Type<any>; // Must use in conjunction with CustomTableCellData in rows
}

/**
 * Data provided for custom component used in table cell
 * Must use in conjunction with component in ColumnData
 */
export interface CustomTableCellData {
  value?: any; // Optional value used for sorting with sortBy provided in ColumnData
  inputData?: Record<string, any>; // @Input values for customized component specified in ColumnData
}

/**
 * Displays a sortable table, sorting by clicking on the header
 * Optional sortBy option provided for each column
 * Columns and rows provided must be aligned
 */
@Component({
  selector: 'tm-sortable-table',
  templateUrl: './sortable-table.component.html',
  styleUrls: ['./sortable-table.component.scss'],
})
export class SortableTableComponent implements OnInit {

  // enum
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  columns: ColumnData[] = [];

  // Default to use supplied value for both sorting and displaying
  // Use CustomTableCellData if value used for sorting is different from displaying
  @Input()
  rows: any[][] | CustomTableCellData[][] = [];

  columnToSortBy: string = '';
  sortOrder: SortOrder = SortOrder.ASC;
  tableRows: any[][] | CustomTableCellData[][] = [];

  constructor(private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.tableRows = this.rows.slice(); // Shallow clone to avoid reordering original array
  }

  ngOnChanges(): void {
    this.tableRows = this.rows.slice(); // Shallow clone to avoid reordering original array
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

    this.tableRows.sort((row1: any[], row2: any[]) => {
      let row1Value: any;
      let row2Value: any;
      if (this.columns[columnIndex].component) {
        row1Value = row1[columnIndex].value;
        row2Value = row2[columnIndex].value;
      } else {
        row1Value = row1[columnIndex];
        row2Value = row2[columnIndex];
      }

      return this.tableComparatorService.compare(
          sortBy, this.sortOrder, String(row1Value), String(row2Value));
    });
  }

}
