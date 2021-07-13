import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LogsTableComponent } from './logs-table.component';

describe('LogsTableComponent', () => {
  let component: LogsTableComponent;
  let fixture: ComponentFixture<LogsTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LogsTableComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
