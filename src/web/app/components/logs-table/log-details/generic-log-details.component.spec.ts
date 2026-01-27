import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericLogDetailsComponent } from './generic-log-details.component';
import { deepCopy } from '../../../../test-helpers/deep-copy';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';
import { GeneralLogEntry } from '../../../../types/api-output';

type TestData = {
  inputLogValue: GeneralLogEntry,
  expectedLogValue: GeneralLogEntry,
};

describe('GenericLogDetailsComponent', () => {
  let component: GenericLogDetailsComponent;
  let fixture: ComponentFixture<GenericLogDetailsComponent>;

  const generateTestData: () => TestData = () => {
    const inputLogValue = generalLogEntryBuilder().build();
    const expectedLogValue = deepCopy(inputLogValue);

    return ({ inputLogValue, expectedLogValue });
  };

  let inputLogValue: GeneralLogEntry;
  let expectedLogValue: GeneralLogEntry;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLogDetailsComponent);
    component = fixture.componentInstance;

    ({ inputLogValue, expectedLogValue } = generateTestData());
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log value from the input log', () => {
    fixture.componentRef.setInput('log', inputLogValue);
    fixture.detectChanges();

    expect(component.logValue).toEqual(expectedLogValue);
  });
});
