import { Component, computed, input, viewChild, ViewEncapsulation } from '@angular/core';
import { HotTableComponent, HotTableModule, NON_COMMERCIAL_LICENSE } from '@handsontable/angular-wrapper';
import type Handsontable from 'handsontable';
import { CellValue } from 'handsontable/common';
import { registerAllModules } from 'handsontable/registry';
import { GridSettings } from 'handsontable/settings';
import { mainTheme, registerTheme } from 'handsontable/themes';

registerAllModules();
const theme = registerTheme(mainTheme)
  .setColorScheme('light');

@Component({
  selector: 'tm-data-grid',
  imports: [HotTableModule],
  templateUrl: './data-grid.component.html',
  styleUrls: ['./data-grid.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DataGridComponent {
  hotTable = viewChild.required(HotTableComponent);

  isReadOnly = input(false);
  minRows = input(1);
  data = input<CellValue[][]>([]);
  colHeaders = input<string[]>([]);
  numCols = computed(() => this.colHeaders().length || 0);

  hotData = computed(() => {
    if (this.data().length > 0) {
      return structuredClone(this.data());
    }
    return undefined;
  });

  readonly gridSettings = computed<GridSettings>(() => ({
    licenseKey: NON_COMMERCIAL_LICENSE,
    theme,
    autoWrapRow: true,
    autoWrapCol: true,
    autoColumnSize: true,
    autoRowSize: true,
    contextMenu: this.isReadOnly()
      ? ['copy']
      : ['row_above', 'row_below', 'remove_row', 'undo', 'redo', 'copy', 'cut'],
    colHeaders: this.colHeaders().length > 0 ? this.colHeaders() : true,
    columnSorting: true,
    height: 'auto',
    minCols: this.colHeaders().length ?? 0,
    minRows: this.minRows(),
    maxCols: this.colHeaders().length ?? 0,
    minSpareRows: this.isReadOnly() ? 0 : 1,
    stretchH: 'all',
    readOnly: this.isReadOnly(),
    rowHeaders: true,
    width: '100%',
    wordWrap: true,
    afterChange: (changes, source) => {
      if (source === 'loadData' || !changes) return;
      const rows = [...new Set(changes.map(([row]) => row))];
      this.resetRowStyles(rows);
    },
  }));

  getData(): CellValue[][] {
    const hotInstance = this.getHotInstance();
    return hotInstance?.getData() ?? [];
  }

  addRows(numberOfRows: number): void {
    if (this.isReadOnly()) {
      return;
    }

    const hotInstance = this.getHotInstance();
    if (hotInstance) {
      hotInstance.alter('insert_row_below', hotInstance.countRows(), numberOfRows);
    }
  }

  resetTableStyles(): void {
    const hotInstance = this.getHotInstance();
    if (!hotInstance) {
      return;
    }

    const numCols = hotInstance.countCols();
    const numRows = hotInstance.countRows();

    // Clear all cell meta classNames
    for (let row = 0; row < numRows; row += 1) {
      for (let col = 0; col < numCols; col += 1) {
        hotInstance.removeCellMeta(row, col, 'className');
      }
    }

    hotInstance.render();
  }

  resetRowStyles(rows: number[]): void {
    const hotInstance = this.getHotInstance();
    if (!hotInstance) {
      return;
    }
    const numCols = hotInstance.countCols();
    for (const row of rows) {
      for (let col = 0; col < numCols; col += 1) {
        hotInstance.removeCellMeta(row, col, 'className');
      }
    }
    hotInstance.render();
  }

  styleRows(rowIdxToClass: Record<number, string>): void {
    const hotInstance = this.getHotInstance();
    if (!hotInstance) {
      return;
    }

    const numCols = hotInstance.countCols();

    // Apply new classNames to entire rows
    for (const [rowIdx, className] of Object.entries(rowIdxToClass)) {
      const row = Number(rowIdx);
      for (let col = 0; col < numCols; col += 1) {
        hotInstance.setCellMeta(row, col, 'className', className);
      }
    }

    hotInstance.render();
  }

  private getHotInstance(): Handsontable | null {
    return this.hotTable().hotInstance;
  }
}
