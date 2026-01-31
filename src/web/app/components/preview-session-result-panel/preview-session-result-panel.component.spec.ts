import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PreviewSessionResultPanelComponent } from './preview-session-result-panel.component';

describe('PreviewSessionPanelComponent', () => {
  let component: PreviewSessionResultPanelComponent;
  let fixture: ComponentFixture<PreviewSessionResultPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
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
