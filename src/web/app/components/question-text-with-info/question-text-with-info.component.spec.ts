import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionTextWithInfoComponent } from './question-text-with-info.component';

describe('QuestionTextWithInfoComponent', () => {
  let component: QuestionTextWithInfoComponent;
  let fixture: ComponentFixture<QuestionTextWithInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [QuestionTextWithInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionTextWithInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
