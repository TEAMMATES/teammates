import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
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

  it('should prevent alphabetical character inputs in onIntegerInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: 'a',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).toHaveBeenCalled();
  });

  it('should prevent decimal point inputs in onIntegerInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: '.',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).toHaveBeenCalled();
  });

  it('should allow digit inputs in onIntegerInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: '6',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).not.toHaveBeenCalled();
  });

  it('should allow number input with less than or equal to 9 digits', () => {
    const inputElement = fixture.debugElement.query(By.css('#total-points')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '12345';
    component.restrictIntegerInputLength(inputEvent, 'points');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('12345');
  });

  it('should restrict number input with more than 9 digits to 9 digits', () => {
    const inputElement = fixture.debugElement.query(By.css('#total-points')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '123456789012345';
    component.restrictIntegerInputLength(inputEvent, 'points');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('123456789');
  });

  it('should have default questionNumber value of 0', () => {
    expect(component.questionNumber).toBe(0);
  });

  it('should update questionNumber from input', () => {
    component.questionNumber = 4;
    component.ngOnChanges();
    expect(component.questionNumber).toBe(4);
    expect(component.pointsRadioGroupName).toBe('constsum-recipients-4');
  });

  it('should set radio group name based on questionNumber', () => {
    component.questionNumber = 6;
    component.ngOnChanges();
    expect(component.pointsRadioGroupName).toBe('constsum-recipients-6');
  });

  it('should update radio group name when questionNumber changes', () => {
    component.questionNumber = 1;
    component.ngOnChanges();
    expect(component.pointsRadioGroupName).toBe('constsum-recipients-1');
    component.questionNumber = 2;
    component.ngOnChanges();
    expect(component.pointsRadioGroupName).toBe('constsum-recipients-2');
  });

  it('should maintain independent radio selection across components', waitForAsync(async () => {
    const fixtureA = TestBed.createComponent(ConstsumRecipientsQuestionEditDetailsFormComponent);
    const compA = fixtureA.componentInstance;
    compA.questionNumber = 1;
    compA.ngOnChanges();
    fixtureA.detectChanges();
    await fixtureA.whenStable();

    const fixtureB = TestBed.createComponent(ConstsumRecipientsQuestionEditDetailsFormComponent);
    const compB = fixtureB.componentInstance;
    compB.questionNumber = 2;
    compB.ngOnChanges();
    fixtureB.detectChanges();
    await fixtureB.whenStable();

    const radioA = fixtureA.debugElement.query(By.css('#per-option-points-radio')).nativeElement as HTMLInputElement;
    radioA.click();
    fixtureA.detectChanges();
    await fixtureA.whenStable();

    const radioB = fixtureB.debugElement.query(By.css('#total-points-radio')).nativeElement as HTMLInputElement;
    radioB.click();
    fixtureB.detectChanges();
    await fixtureB.whenStable();

    expect(radioA.checked).toBe(true);
    expect(radioB.checked).toBe(true);
  }));
});
