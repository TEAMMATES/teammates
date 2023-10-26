import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericLogLineComponent } from './generic-log-line.component';

import { GeneralLogEntry } from '../../../../types/api-output';

describe('GenericLogLineComponent', () => {
  let component: GenericLogLineComponent;
  let fixture: ComponentFixture<GenericLogLineComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogLineComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLogLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

   // Test to check if the log setter updates the summary with the log message
    it('should set summary to log message if it exists', () => {
      const logData: GeneralLogEntry = {
        message: 'Test Message',
        // ... other properties
      };
      component.log = logData;
      expect(component.summary).toBe('Test Message');
    });

    // Test to check if the log setter updates the summary with the details message
    it('should set summary to log details message if log message does not exist', () => {
      const logData: GeneralLogEntry = {
        details: {
          message: 'Details Message',
          // ... other properties
        },
        // ... other properties
      };
      component.log = logData;
      expect(component.summary).toBe('Details Message');
    });

    // Test to check if the log setter sets the summary to an empty string if neither log message nor details message exists
    it('should set summary to an empty string if neither log message nor details message exists', () => {
      const logData: GeneralLogEntry = {};
      component.log = logData;
      expect(component.summary).toBe('');
    });

});
