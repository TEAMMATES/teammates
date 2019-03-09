import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionAdditionalInfoComponent } from './text-question-additional-info.component';

describe('TextQuestionAdditionalInfoComponent', () => {
  let component: TextQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<TextQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TextQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
