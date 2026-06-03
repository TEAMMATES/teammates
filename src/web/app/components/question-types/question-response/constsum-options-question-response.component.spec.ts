import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumOptionsQuestionResponseComponent } from './constsum-options-question-response.component';

describe('ConstsumOptionsQuestionResponseComponent', () => {
  let component: ConstsumOptionsQuestionResponseComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionResponseComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
