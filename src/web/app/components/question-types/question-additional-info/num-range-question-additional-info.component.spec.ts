import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionAdditionalInfoComponent } from './num-range-question-additional-info.component';

describe('NumRangeQuestionAdditionalInfoComponent', () => {
  let component: NumRangeQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<NumRangeQuestionAdditionalInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionAdditionalInfoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
