import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import { InstructorHelpPanelComponent } from './instructor-help-panel.component';

describe('InstructorHelpPanelComponent', () => {
  let component: InstructorHelpPanelComponent;
  let fixture: ComponentFixture<InstructorHelpPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpPanelComponent],
      imports: [NoopAnimationsModule, PanelChevronModule],
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
