import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import {
  SectionTypeDescriptionModule,
} from '../../pages-instructor/instructor-session-result-page/section-type-description.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { ViewResultsPanelComponent } from './view-results-panel.component';

describe('ViewResultsPanelComponent', () => {
  let component: ViewResultsPanelComponent;
  let fixture: ComponentFixture<ViewResultsPanelComponent>;

  beforeEach(waitForAsync(() => {
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
