import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupedResponsesModule } from '../grouped-responses/grouped-responses.module';
import { GrqRgqViewResponsesComponent } from './grq-rgq-view-responses.component';

describe('GrqRgqViewResponsesComponent', () => {
  let component: GrqRgqViewResponsesComponent;
  let fixture: ComponentFixture<GrqRgqViewResponsesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GrqRgqViewResponsesComponent],
      imports: [GroupedResponsesModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrqRgqViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
