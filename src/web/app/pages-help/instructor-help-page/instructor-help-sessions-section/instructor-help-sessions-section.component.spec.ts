import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';

import { SessionEditFormModule } from '../../../components/session-edit-form/session-edit-form.module';
import { ExampleBoxComponent } from '../example-box/example-box.component';
import { InstructorHelpSessionsSectionComponent } from './instructor-help-sessions-section.component';

describe('InstructorHelpSessionsSectionComponent', () => {
  let component: InstructorHelpSessionsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpSessionsSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpSessionsSectionComponent, ExampleBoxComponent],
      imports: [NgbModule, RouterTestingModule, NgxPageScrollCoreModule, SessionEditFormModule, MatSnackBarModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpSessionsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
