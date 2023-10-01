import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NumScaleQuestionEditDetailsFormComponent } from './num-scale-question-edit-details-form.component';

describe('NumScaleQuestionEditDetailsFormComponent', () => {
  let component: NumScaleQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<NumScaleQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionEditDetailsFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should prevent alphabetical character inputs in onPointsInput', () => {
    const event = new KeyboardEvent('keypress', {
      key: 'e',
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
      key: '3',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onPointsInput(event);
    expect(eventSpy).not.toHaveBeenCalled();
  });

  it('should allow number input with less than or equal to 9 digits', () => {
    const inputElement = fixture.debugElement.query(By.css('#max-value')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '12345';
    component.restrictPointsLength(inputEvent, 'minScale');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('12345');
  });

  it('should restrict number input with more than 9 digits to 9 digits', () => {
    const inputElement = fixture.debugElement.query(By.css('#max-value')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '123456789012345';
    component.restrictPointsLength(inputEvent, 'minScale');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('123456789');
  });

});
