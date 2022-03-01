import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { FeedbackPathPanelComponent } from './feedback-path-panel.component';

describe('FeedbackPathPanelComponent', () => {
  let component: FeedbackPathPanelComponent;
  let fixture: ComponentFixture<FeedbackPathPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        FeedbackPathPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbDropdownModule,
        TeammatesCommonModule,
      ],
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
