import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdditionalQuestionPanelComponent } from './additional-question-panel.component';
import { AdditionalQuestionPanelModule } from './additional-question-panel.module';

describe('AdditionalQuestionPanelComponent', () => {
  let component: AdditionalQuestionPanelComponent;
  let fixture: ComponentFixture<AdditionalQuestionPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdditionalQuestionPanelComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdditionalQuestionPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
