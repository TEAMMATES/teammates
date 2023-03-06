import { Component, Input, OnChanges, OnInit, Type } from '@angular/core';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';

/**
 * Column data for sortable table
 */
export interface ColumnData {
  header: string;
  headerToolTip?: string;
  sortBy?: SortBy; // optional if the column is not sortable
}

/**
 * Data provided for each table cell
 * Priority of display
 * 1. customComponent
 * 2. displayValue
 * 3. value
 */
export interface SortableTableCellData {
  value?: any; // Optional value used for sorting with sortBy provided in ColumnData
  displayValue?: string; // Raw string to be display in the cell
  style?: string; // Optional value used to set style of data
  customComponent?: {
    component: Type<any>,
    componentData: Record<string, any>, // @Input values for component
  };
}

/**
 * Displays a sortable table, sorting by clicking on the header
 * Optional sortBy option provided for each column
 * Columns and rows provided must be aligned
 * Remember to register new dynamic components using the withComponents method under sortable-table-module
 */
@Component({
  selector: 'tm-sortable-table',
  templateUrl: './sortable-table.component.html',
  styleUrls: ['./sortable-table.component.scss'],
})
export class SortableTableComponent implements OnInit, OnChanges {

  // enum
  SortOrder: typeof SortOrder = SortOrder;

  @Input()
  columns: ColumnData[] = [];

  @Input()
  rows: SortableTableCellData[][] = [];

  @Input()
  initialSortBy: SortBy = SortBy.NONE;

  columnToSortBy: string = '';
  sortOrder: SortOrder = SortOrder.ASC;
  tableRows: SortableTableCellData[][] = [];

  constructor(private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.tableRows = this.rows.slice(); // Shallow clone to avoid reordering original array
    this.initialSort(); // Performs an initial sort on the table
  }

  ngOnChanges(): void {
    this.tableRows = this.rows.slice(); // Shallow clone to avoid reordering original array
    this.sortRows();
  }

  onClickHeader(columnHeader: string): void {
    this.sortOrder = this.columnToSortBy === columnHeader && this.sortOrder === SortOrder.ASC
        ? SortOrder.DESC : SortOrder.ASC;
    this.columnToSortBy = columnHeader;
    this.sortRows();
  }

  getAriaSort(column: String): String {
    if (column !== this.columnToSortBy) {
      return 'none';
    }
    return this.sortOrder === SortOrder.ASC ? 'ascending' : 'descending';
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
      return this.tableComparatorService.compare(
          sortBy, this.sortOrder, String(row1[columnIndex].value), String(row2[columnIndex].value));
    });
  }

  /**
   * Sorts the table with an initial SortBy
   */
  initialSort(): void {
    const indexOfColumnToSort: number =
        this.columns.findIndex((column: ColumnData) => column.sortBy === this.initialSortBy);
    if (indexOfColumnToSort < 0) {
      return;
    }

    this.columnToSortBy = this.columns[indexOfColumnToSort].header;
    this.sortRows();
  }

  getStyle(cellData: SortableTableCellData): string | undefined {
    return cellData.style;
  }
}
