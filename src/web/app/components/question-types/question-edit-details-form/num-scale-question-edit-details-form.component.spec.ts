import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
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

    const eventSpy = jest.spyOn(event, 'preventDefault')
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

});
