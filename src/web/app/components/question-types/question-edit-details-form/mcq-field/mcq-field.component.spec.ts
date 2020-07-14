import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { OptionRichTextEditorModule } from '../option-rich-text-editor/option-rich-text-editor.module';
import { McqFieldComponent } from './mcq-field.component';

describe('McqFieldComponent', () => {
  let component: McqFieldComponent;
  let fixture: ComponentFixture<McqFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqFieldComponent],
      imports: [
        FormsModule,
        OptionRichTextEditorModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
