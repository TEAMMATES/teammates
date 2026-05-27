import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SortableTableCellData, SortableTableComponent } from './sortable-table.component';

describe('SortableTableComponent', () => {
  let component: SortableTableComponent;
  let fixture: ComponentFixture<SortableTableComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(SortableTableComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should render table id when tableId is provided', () => {
    component.tableId = 'test-table-id';
    component.columns = [{ header: 'Name' }];
    component.rows = [[{ value: 'Alice' }]];
    fixture.detectChanges();

    const table: HTMLTableElement = fixture.nativeElement.querySelector('table');
    expect(table.getAttribute('id')).toBe('test-table-id');
  });

  it('should apply row id and row class when row getters are provided', () => {
    component.columns = [{ header: 'Name' }];
    component.rows = [[{ value: 'Alice' }], [{ value: 'Bob' }]];
    component.rowIdGetter = (_: SortableTableCellData[], idx: number): string => `row-${idx}`;
    component.rowClassGetter = (_: SortableTableCellData[], idx: number): string | undefined =>
      idx === 1 ? 'highlighted-row' : undefined;
    fixture.detectChanges();

    const rows: NodeListOf<HTMLTableRowElement> = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(rows[0].getAttribute('id')).toBe('row-0');
    expect(rows[1].getAttribute('id')).toBe('row-1');
    expect(rows[0].classList.contains('highlighted-row')).toBe(false);
    expect(rows[1].classList.contains('highlighted-row')).toBe(true);
  });
});
