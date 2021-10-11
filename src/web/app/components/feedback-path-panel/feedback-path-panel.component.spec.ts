import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackPathPanelComponent } from './feedback-path-panel.component';
import { FeedbackPathPanelModule } from './feedback-path-panel.module';

describe('FeedbackPathPanelComponent', () => {
  let component: FeedbackPathPanelComponent;
  let fixture: ComponentFixture<FeedbackPathPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FeedbackPathPanelComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackPathPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
