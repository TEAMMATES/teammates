import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { InstructorTrackViewPageComponent } from './instructor-track-view-page.component';
import { InstructorTrackViewPageModule } from './instructor-track-view-page.module';

describe('InstructorTrackViewPageComponent', () => {
  let component: InstructorTrackViewPageComponent;
  let fixture: ComponentFixture<InstructorTrackViewPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        InstructorTrackViewPageModule,
        NgbModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorTrackViewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
