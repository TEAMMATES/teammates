import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumOptionsQuestionAdditionalInfoComponent } from './constsum-options-question-additional-info.component';

describe('ConstsumOptionsQuestionAdditionalInfoComponent', () => {
  let component: ConstsumOptionsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumOptionsQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
