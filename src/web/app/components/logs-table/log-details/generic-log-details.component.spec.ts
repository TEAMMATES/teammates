import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericLogDetailsComponent } from './generic-log-details.component';
import { generalLogEntryBuilder } from '../../../../test-helpers/log-test-helpers';

describe('GenericLogDetailsComponent', () => {
  let component: GenericLogDetailsComponent;
  let fixture: ComponentFixture<GenericLogDetailsComponent>;
  const expectedLogValue = generalLogEntryBuilder().build();

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('log', expectedLogValue);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set log value from the input log', () => {
    expect(component.logValue).toBe(expectedLogValue);
  });
});
