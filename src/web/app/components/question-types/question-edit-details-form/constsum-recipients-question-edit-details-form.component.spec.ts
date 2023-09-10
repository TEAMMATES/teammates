import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import {
  ConstsumRecipientsQuestionEditDetailsFormComponent,
} from './constsum-recipients-question-edit-details-form.component';

describe('ConstsumRecipientsQuestionEditDetailsFormComponent', () => {
  let component: ConstsumRecipientsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionEditDetailsFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should prevent alphabetical character inputs in onPointsInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: 'a',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onPointsInput(event);
    expect(eventSpy).toHaveBeenCalled();
  });

  it('should prevent decimal point inputs in onPointsInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: '.',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onPointsInput(event);
    expect(eventSpy).toHaveBeenCalled();
  });

  it('should allow digit inputs in onPointsInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: '6',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onPointsInput(event);
    expect(eventSpy).not.toHaveBeenCalled();
  });
});
