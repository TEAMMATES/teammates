import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NumscaleQuestionAdditionalInfoComponent } from './numscale-question-additional-info.component';

describe('NumscaleQuestionAdditionalInfoComponent', () => {
  let component: NumscaleQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<NumscaleQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NumscaleQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumscaleQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
