import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionResponseComponent } from './num-range-question-response.component';

describe('NumRangeQuestionResponseComponent', () => {
  let component: NumRangeQuestionResponseComponent;
  let fixture: ComponentFixture<NumRangeQuestionResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionResponseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
