import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorHelpPanelComponent } from './instructor-help-panel.component';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';

describe('InstructorHelpPanelComponent', () => {
  let component: InstructorHelpPanelComponent;
  let fixture: ComponentFixture<InstructorHelpPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpPanelComponent],
      imports: [NoopAnimationsModule, PanelChevronModule, RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
