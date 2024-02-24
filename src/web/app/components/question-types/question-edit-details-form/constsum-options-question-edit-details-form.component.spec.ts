import { DragDropModule } from '@angular/cdk/drag-drop';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ConstsumOptionsFieldComponent } from './constsum-options-field/constsum-options-field.component';
import {
  ConstsumOptionsQuestionEditDetailsFormComponent,
} from './constsum-options-question-edit-details-form.component';

describe('ConstsumOptionsQuestionEditDetailsFormComponent', () => {
  let component: ConstsumOptionsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        DragDropModule,
      ],
      declarations: [
        ConstsumOptionsQuestionEditDetailsFormComponent,
        ConstsumOptionsFieldComponent,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should prevent alphabetical character inputs in onIntegerInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: 'b',
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
      key: '7',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).not.toHaveBeenCalled();
  });

  it('should allow number input with less than or equal to 9 digits', () => {
    const inputElement = fixture.debugElement.query(By.css('#max-point')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '12345';
    component.restrictIntegerInputLength(inputEvent, 'points');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('12345');
  });

  it('should restrict number input with more than 9 digits to 9 digits', () => {
    const inputElement = fixture.debugElement.query(By.css('#max-point')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '123456789012345';
    component.restrictIntegerInputLength(inputEvent, 'points');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('123456789');
  });
});
