import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PerQuestionViewResponsesComponent } from './per-question-view-responses.component';

describe('PerQuestionViewResponsesComponent', () => {
  let component: PerQuestionViewResponsesComponent;
  let fixture: ComponentFixture<PerQuestionViewResponsesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PerQuestionViewResponsesComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PerQuestionViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
