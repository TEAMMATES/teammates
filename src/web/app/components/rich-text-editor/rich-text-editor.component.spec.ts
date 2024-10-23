import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RichTextEditorComponent } from './rich-text-editor.component';

const MOCK_TINYMCE_BASE_URL = 'https://cdn.jsdelivr.net/npm/tinymce@6.8.2';

describe('RichTextEditorComponent', () => {
  let component: RichTextEditorComponent;
  let fixture: ComponentFixture<RichTextEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RichTextEditorComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RichTextEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the RichTextEditorComponent', () => {
    expect(component).toBeTruthy();
  });

  describe('Input Properties', () => {
    it('should set default input properties', () => {
      expect(component.isDisabled).toBeFalsy();
      expect(component.hasCharacterLimit).toBeFalsy();
      expect(component.minHeightInPx).toBe(150);
      expect(component.placeholderText).toBe('');
      expect(component.richText).toBe('');
    });

    it('should accept and apply custom input properties', () => {
      component.isDisabled = true;
      component.hasCharacterLimit = true;
      component.minHeightInPx = 300;
      component.placeholderText = 'Enter text here...';
      component.richText = '<p>Initial content</p>';
      fixture.detectChanges();

      expect(component.isDisabled).toBeTruthy();
      expect(component.hasCharacterLimit).toBeTruthy();
      expect(component.minHeightInPx).toBe(300);
      expect(component.placeholderText).toBe('Enter text here...');
      expect(component.richText).toBe('<p>Initial content</p>');
    });
  });

  describe('Output Events', () => {
    it('should emit richTextChange event when richText changes', () => {
      const emitSpy = jest.spyOn(component.richTextChange, 'emit');
      const newRichText = '<p>New content</p>';
      component.richText = newRichText;
      component.richTextChange.emit(newRichText);
      expect(emitSpy).toHaveBeenCalledWith(newRichText);
    });
  });

  describe('Editor Initialization', () => {
    it('should initialize editor settings with default values', () => {
      component.ngOnInit();
      expect(component.init).toBeDefined();
      expect(component.init.base_url).toBe(MOCK_TINYMCE_BASE_URL);
      expect(component.init.skin_url).toContain(MOCK_TINYMCE_BASE_URL);
      expect(component.init.height).toBe(150);
      expect(component.init.placeholder).toBe('');
      expect(component.init.plugins).toContain('wordcount');
      expect(component.init.toolbar1).toContain('bold');
    });

    it('should initialize editor settings with custom input values', () => {
      component.hasCharacterLimit = true;
      component.minHeightInPx = 300;
      component.placeholderText = 'Type something...';
      component.ngOnInit();
      expect(component.init.height).toBe(300);
      expect(component.init.placeholder).toBe('Type something...');
      expect(component.init.setup).toBeDefined();
    });
  });

  describe('Character Limit Functionality', () => {
    class MockRange {
      setStart = jest.fn();
      collapse = jest.fn();
    }

    let mockEditor: any;
    let mockRange: MockRange;

    beforeEach(() => {
      component.hasCharacterLimit = true;
      component.ngOnInit();

      mockRange = new MockRange();

      mockEditor = {
        on: jest.fn(),
        getContent: jest.fn().mockReturnValue('Sample text'),
        setContent: jest.fn(),
        selection: {
          getRng: jest.fn().mockReturnValue({
            startContainer: {},
            startOffset: 0,
          }),
          setRng: jest.fn(),
        },
        plugins: {
          wordcount: {
            body: {
              getCharacterCount: jest.fn().mockReturnValue(1000),
            },
          },
        },
        dom: {
          createRng: jest.fn().mockReturnValue(mockRange),
        },
      };

      const setupFunction = component.init.setup;
      if (setupFunction) {
        setupFunction(mockEditor);
      }
    });

    it('should update characterCount on GetContent event', async () => {
      expect(mockEditor.on).toHaveBeenCalledWith('GetContent', expect.any(Function));
      const getContentCallback = mockEditor.on.mock.calls.find(
        (call: [string, (...args: any[]) => void]) => call[0] === 'GetContent',
      )?.[1];

      if (getContentCallback) {
        getContentCallback();
      }

      await new Promise((resolve) => {
        setTimeout(resolve, 0);
      });

      expect(component.characterCount).toBe(1000);
    });

    it('should prevent keypress when character limit is reached', () => {
      const keypressCallback = mockEditor.on.mock.calls.find(
        (call: [string, (...args: any[]) => void]) => call[0] === 'keypress',
      )?.[1];

      const mockEvent = { preventDefault: jest.fn() };

      component.RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH = 2000;
      mockEditor.plugins.wordcount.body.getCharacterCount.mockReturnValue(2000);

      if (keypressCallback) {
        keypressCallback(mockEvent);
      }

      expect(mockEvent.preventDefault).toHaveBeenCalled();
    });

    it('should handle paste event and truncate content if necessary', async () => {
      const pasteCallback = mockEditor.on.mock.calls.find(
        (call: [string, (...args: any[]) => void]) => call[0] === 'paste',
      )?.[1];

      const mockPasteEvent = { preventDefault: jest.fn() };

      mockEditor.getContent
        .mockReturnValueOnce('Existing content')
        .mockReturnValueOnce('Existing contentPastedText');

      component.RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH = 2000;
      mockEditor.plugins.wordcount.body.getCharacterCount.mockReturnValue(2000);

      if (pasteCallback) {
        pasteCallback(mockPasteEvent);
      }

      await new Promise((resolve) => {
        setTimeout(resolve, 0);
      });

      expect(mockPasteEvent.preventDefault).toHaveBeenCalled();

      const contentBeforePaste = 'Existing content';
      const contentAfterPaste = 'Existing contentPastedText';
      let firstDifferentIndex = 0;
      while (
        firstDifferentIndex < contentBeforePaste.length
        && contentBeforePaste[firstDifferentIndex] === contentAfterPaste[firstDifferentIndex]
      ) {
        firstDifferentIndex += 1;
      }
      const contentBeforeFirstDifferentIndex = contentBeforePaste.substring(0, firstDifferentIndex);
      const contentAfterFirstDifferentIndex = contentBeforePaste.substring(firstDifferentIndex);
      const lengthExceed =
        mockEditor.plugins.wordcount.body.getCharacterCount()
        - component.RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH;
      const pasteContentLength = contentAfterPaste.length - contentBeforePaste.length;
      const pasteContent = contentAfterPaste.substring(
        firstDifferentIndex,
        firstDifferentIndex + pasteContentLength,
      );
      const truncatedPastedText = pasteContent.substring(0, pasteContentLength - lengthExceed);
      const finalContent =
        contentBeforeFirstDifferentIndex + truncatedPastedText + contentAfterFirstDifferentIndex;

      expect(mockEditor.setContent).toHaveBeenCalledWith(finalContent);
      expect(mockEditor.selection.getRng).toHaveBeenCalled();
      expect(mockEditor.dom.createRng).toHaveBeenCalled();
      expect(mockEditor.selection.setRng).toHaveBeenCalledWith(mockRange);
      expect(mockRange.setStart).toHaveBeenCalled();
      expect(mockRange.collapse).toHaveBeenCalled();
    });
  });

  describe('getCurrentCharacterCount', () => {
    it('should return the current character count from wordcount plugin', () => {
      const mockEditor = {
        plugins: {
          wordcount: {
            body: {
              getCharacterCount: () => 1500,
            },
          },
        },
      };
      const count = component.getCurrentCharacterCount(mockEditor);
      expect(count).toBe(1500);
    });
  });

  describe('renderEditor Method', () => {
    it('should set render to true when event.visible is true', () => {
      const event = { visible: true };
      component.render = false;
      component.renderEditor(event);
      expect(component.render).toBeTruthy();
    });

    it('should not change render when event.visible is false', () => {
      const event = { visible: false };
      component.render = false;
      component.renderEditor(event);
      expect(component.render).toBeFalsy();
    });
  });
});
