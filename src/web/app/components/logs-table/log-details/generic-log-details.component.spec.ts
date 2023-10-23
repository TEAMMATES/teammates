import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GeneralLogEntry, LogSeverity } from '../../../../types/api-output';

import { GenericLogDetailsComponent } from './generic-log-details.component';

describe('GenericLogDetailsComponent', () => {
  let component: GenericLogDetailsComponent;
  let fixture: ComponentFixture<GenericLogDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should assign and retrieve a valid GeneralLogEntry', () => {
    const log: GeneralLogEntry = {
      severity: LogSeverity.INFO,
      trace: 'trace_id_123',
      insertId: 'unique_insert_id_456',
      resourceIdentifier: {},
      sourceLocation: { file: 'test_log.txt', line: 123, function: '' },
      timestamp: Date.now(),
      details: undefined,
    };

    component.log = log;
    expect(component.log).toEqual(log);
  });

});
