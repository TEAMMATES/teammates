import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';
import { InstructorHelpGeneralSectionComponent } from './instructor-help-general-section.component';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('InstructorHelpGeneralSectionComponent', () => {
    let component: InstructorHelpGeneralSectionComponent;
    let fixture: ComponentFixture<InstructorHelpGeneralSectionComponent>;
  
    beforeEach(async(() => {
      TestBed.configureTestingModule({
        declarations: [
          InstructorHelpGeneralSectionComponent,
          InstructorHelpPanelComponent,
        ],
        imports: [RouterTestingModule, TeammatesRouterModule],
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