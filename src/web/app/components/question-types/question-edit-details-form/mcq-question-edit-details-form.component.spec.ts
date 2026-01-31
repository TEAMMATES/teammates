import { ComponentFixture, TestBed } from '@angular/core/testing';
import { McqQuestionEditDetailsFormComponent } from './mcq-question-edit-details-form.component';

describe('McqQuestionEditDetailsFormComponent', () => {
  let component: McqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<McqQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view i.e. Question options are radio buttons', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when questionDropdownEnabled is true', () => {
    component.triggerQuestionDropdownEnabled(true);
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when questionDropdownEnabled is false', () => {
    component.triggerQuestionDropdownEnabled(false);
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should be false by default when checkbox to show options as a drop-down is unchecked', () => {
    const dropDownCheckBoxNe: any = fixture.debugElement.nativeElement.querySelector('#make-question-dropdown');

    expect(component.model.questionDropdownEnabled).toBeFalsy();
    expect(dropDownCheckBoxNe.checked).toBeFalsy();
  });
});
