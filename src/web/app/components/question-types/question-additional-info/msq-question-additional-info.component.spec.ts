import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsqQuestionAdditionalInfoComponent } from './msq-question-additional-info.component';

describe('MsqQuestionAdditionalInfoComponent', () => {
  let component: MsqQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<MsqQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
