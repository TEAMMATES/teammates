import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { VisibilityPanelComponent } from './visibility-panel.component';

describe('VisibilityPanelComponent', () => {
  let component: VisibilityPanelComponent;
  let fixture: ComponentFixture<VisibilityPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        VisibilityPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbDropdownModule,
        NgbTooltipModule,
        TeammatesCommonModule,
        VisibilityMessagesModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisibilityPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
