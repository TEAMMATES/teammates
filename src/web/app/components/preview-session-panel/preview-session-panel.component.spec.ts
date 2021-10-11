import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewSessionPanelComponent } from './preview-session-panel.component';
import { PreviewSessionPanelModule } from './preview-session-panel.module';

describe('PreviewSessionPanelComponent', () => {
  let component: PreviewSessionPanelComponent;
  let fixture: ComponentFixture<PreviewSessionPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PreviewSessionPanelComponent],
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
