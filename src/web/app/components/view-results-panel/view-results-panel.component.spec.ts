import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { SectionTypeDescriptionModule } from '../../pages-instructor/instructor-session-result-page/section-type-description.module';
import { ViewResultsPanelComponent } from './view-results-panel.component';

describe('ViewResultsPanelComponent', () => {
  let component: ViewResultsPanelComponent;
  let fixture: ComponentFixture<ViewResultsPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ViewResultsPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbTooltipModule,
        TeammatesCommonModule,
        SectionTypeDescriptionModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewResultsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
