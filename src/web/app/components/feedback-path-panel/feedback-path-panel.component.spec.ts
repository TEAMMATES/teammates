import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { FeedbackPathPanelComponent } from './feedback-path-panel.component';

describe('FeedbackPathPanelComponent', () => {
  let component: FeedbackPathPanelComponent;
  let fixture: ComponentFixture<FeedbackPathPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        FeedbackPathPanelComponent,
      ],
      imports: [
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
