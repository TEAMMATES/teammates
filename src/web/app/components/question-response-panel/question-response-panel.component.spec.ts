import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionResponsePanelComponent } from './question-response-panel.component';

describe('QuestionResponsePanelComponent', () => {
  let component: QuestionResponsePanelComponent;
  let fixture: ComponentFixture<QuestionResponsePanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QuestionResponsePanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionResponsePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
