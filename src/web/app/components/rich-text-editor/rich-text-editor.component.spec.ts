import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RichTextEditorComponent } from './rich-text-editor.component';
import { Editor } from 'tinymce';

describe('RichTextEditorComponent', () => {
  let component: RichTextEditorComponent;
  let fixture: ComponentFixture<RichTextEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RichTextEditorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize editor settings on init', () => {
    component.ngOnInit();

    expect(component.init).toBeTruthy();
    expect(component.init.toolbar1).toBe(component.defaultToolbar);
  });

  it('should use input values in editor settings', () => {
    component.minHeightInPx = 300;
    component.placeholderText = 'Enter text here';

    component.ngOnInit();

    expect(component.init.height).toBe(300);
    expect(component.init.placeholder).toBe('Enter text here');
  });

  it('should return current character count from editor', () => {
    const mockEditor = {
      plugins: {
        wordcount: {
          body: {
            getCharacterCount: () => 123,
          },
        },
      },
    } as unknown as Editor;

    expect(component.getCurrentCharacterCount(mockEditor)).toBe(123);
  });

  it('should define setup function when character limit is enabled', () => {
    component.hasCharacterLimit = true;
    component.ngOnInit();
    expect(component.init.setup).toBeDefined();
  });

  it('should update character count on GetContent when character limit is enabled', async () => {
    component.hasCharacterLimit = true;
    component.ngOnInit();

    let handler: any;

    const mockEditor = {
      on: (event: string, cb: any) => {
        if (event === 'GetContent') handler = cb;
      },
      plugins: {
        wordcount: {
          body: {
            getCharacterCount: () => 50,
          },
        },
      },
    } as unknown as Editor;

    component.init.setup!(mockEditor);
    handler();

    await Promise.resolve();

    expect(component.characterCount()).toBe(50);
  });

  it('should prevent keypress when character limit is reached', () => {
    component.hasCharacterLimit = true;
    component.ngOnInit();

    let keypressHandler: ((event: { preventDefault: () => void }) => void) | undefined;

    const mockEditor = {
      on: (eventName: string, handler: (event: { preventDefault: () => void }) => void) => {
        if (eventName === 'keypress') {
          keypressHandler = handler;
        }
      },
      plugins: {
        wordcount: {
          body: {
            getCharacterCount: () => 2000,
          },
        },
      },
    } as unknown as Editor;

    const mockEvent = {
      preventDefault: vi.fn(),
    };

    component.init.setup!(mockEditor);
    keypressHandler!(mockEvent);

    expect(mockEvent.preventDefault).toHaveBeenCalled();
  });

  it('should not prevent keypress when character limit is not reached', () => {
    component.hasCharacterLimit = true;
    component.ngOnInit();

    let keypressHandler: ((event: { preventDefault: () => void }) => void) | undefined;

    const mockEditor = {
      on: (eventName: string, handler: (event: { preventDefault: () => void }) => void) => {
        if (eventName === 'keypress') {
          keypressHandler = handler;
        }
      },
      plugins: {
        wordcount: {
          body: {
            getCharacterCount: () => 1999,
          },
        },
      },
    } as unknown as Editor;

    const mockEvent = {
      preventDefault: vi.fn(),
    };

    component.init.setup!(mockEditor);
    keypressHandler!(mockEvent);

    expect(mockEvent.preventDefault).not.toHaveBeenCalled();
  });
});
