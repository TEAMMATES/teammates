import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { PreviewSessionPanelComponent } from './preview-session-panel.component';

describe('PreviewSessionPanelComponent', () => {
  let component: PreviewSessionPanelComponent;
  let fixture: ComponentFixture<PreviewSessionPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PreviewSessionPanelComponent,
      ],
      imports: [
        NgbTooltipModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewSessionPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
