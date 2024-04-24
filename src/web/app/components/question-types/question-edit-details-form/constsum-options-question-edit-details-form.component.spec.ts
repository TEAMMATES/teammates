import { DragDropModule } from '@angular/cdk/drag-drop';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ConstsumOptionsFieldComponent } from './constsum-options-field/constsum-options-field.component';
import {
  ConstsumOptionsQuestionEditDetailsFormComponent,
} from './constsum-options-question-edit-details-form.component';
import { FeedbackConstantSumDistributePointsType } from '../../../../types/api-output';

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

  it('onIntegerInput: should prevent alphabetical character inputs when input is entered', () => {
    const event = new KeyboardEvent('keypress', {
      key: 'b',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).toHaveBeenCalled();
  });

  it('onIntegerInput: should prevent decimal point inputs when input is entered', () => {
    const event = new KeyboardEvent('keypress', {
      key: '.',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).toHaveBeenCalled();
  });

  it('onIntegerInput: should allow digit inputs when input is entered', () => {
    const event = new KeyboardEvent('keypress', {
      key: '7',
    });

    const eventSpy = jest.spyOn(event, 'preventDefault');
    component.onIntegerInput(event);
    expect(eventSpy).not.toHaveBeenCalled();
  });

  it('restrictIntegerInputLength: should allow number input with less than or equal to 9 digits when a number is inputted', () => {
    const inputElement = fixture.debugElement.query(By.css('#max-point')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '12345';
    component.restrictIntegerInputLength(inputEvent, 'points');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('12345');
  });

  it('restrictIntegerInputLength: should restrict number input with more than 9 digits to 9 digits when a number is inputted', () => {
    const inputElement = fixture.debugElement.query(By.css('#max-point')).nativeElement as HTMLInputElement;
    const inputEvent = new InputEvent('input');
    inputElement.dispatchEvent(inputEvent);
    (inputEvent.target as HTMLInputElement).value = '123456789012345';
    component.restrictIntegerInputLength(inputEvent, 'points');
    expect((inputEvent.target as HTMLInputElement).value).toEqual('123456789');
  });

  it('increaseNumberOfConstsumOptions: should increase the number of constSumOptions by 1 and add an empty string as the new option when wanting to increase constSumOptions', () => {
    const initialOptions = ['Option 1'];
    component.model = { ...component.model, constSumOptions: initialOptions };
    fixture.detectChanges();
    component.increaseNumberOfConstsumOptions();
    fixture.detectChanges();
    expect(component.model.constSumOptions.length).toBe(initialLength + 1, 'Number of options did not increase by 1');
    expect(component.model.constSumOptions[component.model.constSumOptions.length - 1]).toBe('', 'The new option is not an empty string');
  });

  it('onConstsumOptionDeleted: should not delete a constSumOption if doing so leaves fewer than 2 options', () => {
    component.model = { ...component.model, constSumOptions: ['Options 1', 'Option 2'] };
    fixture.detectChanges();

    component.onConstsumOptionDeleted(0);
    fixture.detectChanges();

    expect(component.model.constSumOptions.length).toBe(2);
  });

  it('onConstsumOptionDeleted: should successfully delete a constSumOption when more than 2 options exist', () => {
    component.model = { ...component.model, constSumOptions: ['Option 1', 'Option 2', 'Option 3'] };
    fixture.detectChanges();
    
    component.onConstsumOptionDeleted(1);
    fixture.detectChanges();
    
    expect(component.model.constSumOptions).toEqual(['Option 1', 'Option 3']);
  });

  it('onConstsumOptionEntered: should update the value of a constSumOption when the specified index is entered', () => {
    component.model = { ...component.model, constSumOptions: ['Options 1', 'Option 2', 'Option 3'] };
    fixture.detectChanges();

    const updatedOption = 'Updated Option 2';
    component.onConstsumOptionEntered(updatedOption, 1);
    fixture.detectChanges();

    expect(component.model.constSumOptions[1]).toBe(updatedOption, 'The constSumOption was not updated correctly');
    expect(component.model.constSumOptions).toEqual(['Option 1', updatedOption, 'Option 3']);
  });

  it('onForceUnevenDistribution: should enable force uneven distribution and set distributePointsFor accordingly when event is true', () => {
    component.model = {
      ...component.model,
      forceUnevenDistribution: false,
      distributePointsFor: FeedbackConstantSumDistributePointsType.NONE
    };
    fixture.detectChanges();

    component.onForceUnevenDistribution(true);
    fixture.detectChanges();

    component.onForceUnevenDistribution(true);
    fixture.detectChanges();

    expect(component.model.forceUnevenDistribution).toBe(true);
    expect(component.model.distributePointsFor).toEqual(FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY);
  });

  it('onForceUnevenDistribution: should disable force uneven distribution and set distributePointsFor to NONE when event is false', () => {
    component.model = {
      ...component.model,
      forceUnevenDistribution: true,
      distributePointsFor: FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY
    };
    fixture.detectChanges();

    component.onForceUnevenDistribution(false);
    fixture.detectChanges();

    expect(component.model.forceUnevenDistribution).toBe(false);
    expect(component.model.distributePointsFor).toEqual(FeedbackConstantSumDistributePointsType.NONE);
  });

  it('resetMaxPoint: should reset maxPoint to 0 when event is true', () => {
    component.resetMaxPoint(true);
    fixture.detectChanges();

    expect(component.model.maxPoint).toBe(0);
  });

  it('resetMaxPoint: should set maxPoint to undefined when event is false', () => {
    component.model.maxPoint = 10;
    fixture.detectChanges();

    component.resetMaxPoint(false);
    fixture.detectChanges();

    expect(component.model.maxPoint).toBeUndefined();
  });

  it('detectChanges: should set minPoint to 0 when event is true', () => {
    component.resetMinPoint(true);
    fixture.detectChanges();

    expect(component.model.minPoint).toBe(0);
  });
 
  it('resetMinPoint: should set minPoint to undefined when event is false', () => {
    component.model.minPoint = 10;
    fixture.detectChanges();

    component.resetMinPoint(false);
    fixture.detectChanges();

    expect(component.model.minPoint).toBeUndefined();
  });
});
