import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumQuestionAdditionalInfoComponent } from './constsum-question-additional-info.component';

describe('ConstsumQuestionAdditionalInfoComponent', () => {
  let component: ConstsumQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<ConstsumQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
