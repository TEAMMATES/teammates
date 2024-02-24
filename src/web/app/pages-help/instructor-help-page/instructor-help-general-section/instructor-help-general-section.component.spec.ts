import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { InstructorHelpGeneralSectionComponent } from './instructor-help-general-section.component';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';

describe('InstructorHelpGeneralSectionComponent', () => {
  let component: InstructorHelpGeneralSectionComponent;
  let fixture: ComponentFixture<InstructorHelpGeneralSectionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorHelpGeneralSectionComponent,
        InstructorHelpPanelComponent,
      ],
      imports: [
        NgbModule,
        RouterTestingModule,
        NgxPageScrollCoreModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        PanelChevronModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpGeneralSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
