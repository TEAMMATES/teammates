import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TextQuestionAdditionalInfoComponent } from './text-question-additional-info.component';

describe('TextQuestionAdditionalInfoComponent', () => {
  let component: TextQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<TextQuestionAdditionalInfoComponent>;

  beforeEach(waitForAsync(() => {
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
