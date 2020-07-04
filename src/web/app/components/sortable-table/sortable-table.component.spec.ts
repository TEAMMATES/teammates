import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DynamicComponentModule } from 'ng-dynamic-component';
import { SortableTableComponent } from './sortable-table.component';

describe('SortableTableComponent', () => {
  let component: SortableTableComponent;
  let fixture: ComponentFixture<SortableTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SortableTableComponent],
      imports: [NgbModule, DynamicComponentModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SortableTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
