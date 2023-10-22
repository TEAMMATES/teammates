import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GeneralLogEntry, LogSeverity} from '../../../../types/api-output';

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

  
  it('should set and get log', () => {
    const log: GeneralLogEntry = {
      severity: LogSeverity.INFO,
      trace: 'test_trace',
      insertId: '1',
      resourceIdentifier: {},
      sourceLocation: { file: 'log.txt', line: 42, function: 'test_function' },
      timestamp: Date.now(),
      details: undefined,
    };

    component.log = log;
    expect(component.log).toEqual(log);
  });

});