import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericLogDetailsComponent } from './generic-log-details.component';
import {
  GeneralLogEntry,
  LogEvent,
  LogSeverity,
} from '../../../../types/api-output';

describe('GenericLogDetailsComponent', () => {
  let component: GenericLogDetailsComponent;
  let fixture: ComponentFixture<GenericLogDetailsComponent>;
  const expectedLogValue: GeneralLogEntry = {
    severity: LogSeverity.DEFAULT,
    trace: '0123456789abcdef',
    insertId: '0123456789abcdef',
    resourceIdentifier: {
      module_id: 'mock',
      version_id: '1-0-0',
      project_id: 'mock-project',
      zone: 'mock-zone-1',
    },
    sourceLocation: {
      file: 'com.mock.Mock',
      line: 100,
      function: 'handle',
    },
    timestamp: 1000,
    message: 'Test general log message',
    details: {
      event: LogEvent.DEFAULT_LOG,
      message: 'Test general log details message',
    },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(waitForAsync(() => {
    fixture = TestBed.createComponent(GenericLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('log', expectedLogValue);
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set the input log as log value', () => {
    expect(component.logValue).toEqual(expectedLogValue);
  });
});
