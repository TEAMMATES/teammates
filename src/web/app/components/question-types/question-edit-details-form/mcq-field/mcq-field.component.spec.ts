import { ComponentFixture, TestBed } from '@angular/core/testing';

import { McqFieldComponent } from './mcq-field.component';

describe('McqFieldComponent', () => {
  let component: McqFieldComponent;
  let fixture: ComponentFixture<McqFieldComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(McqFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false by default for isQuestionDropdownEnabled', () => {
    expect(component.isQuestionDropdownEnabled).toBeFalsy();
  });
});
