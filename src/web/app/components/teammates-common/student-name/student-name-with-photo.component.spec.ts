import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesRouterModule } from '../../teammates-router/teammates-router.module';
import { FormatPhotoUrlPipe } from '../format-photo-url.pipe';
import { ViewPhotoPopoverComponent } from '../view-photo-popover/view-photo-popover.component';
import { StudentNameWithPhotoComponent } from './student-name-with-photo.component';

describe('StudentNameWithPhotoComponent', () => {
  let component: StudentNameWithPhotoComponent;
  let fixture: ComponentFixture<StudentNameWithPhotoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StudentNameWithPhotoComponent, FormatPhotoUrlPipe, ViewPhotoPopoverComponent],
      imports: [NgbModule, RouterTestingModule, TeammatesRouterModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentNameWithPhotoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
