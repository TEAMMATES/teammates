import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { McqQuestionAdditionalInfoComponent } from './mcq-question-additional-info.component';

describe('McqQuestionAdditionalInfoComponent', () => {
  let component: McqQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<McqQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
