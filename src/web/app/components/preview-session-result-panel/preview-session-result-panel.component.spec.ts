import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { PreviewSessionResultPanelComponent } from './preview-session-result-panel.component';

describe('PreviewSessionPanelComponent', () => {
  let component: PreviewSessionResultPanelComponent;
  let fixture: ComponentFixture<PreviewSessionResultPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PreviewSessionResultPanelComponent,
      ],
      imports: [
        FormsModule,
        RouterTestingModule,
        NgbTooltipModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewSessionResultPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
