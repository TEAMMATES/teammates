import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EditorComponent, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';
import { Editor, EditorEvent, RawEditorOptions } from 'tinymce';

const RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH = 2000;

/**
 * A rich text editor.
 */
@Component({
  selector: 'tm-rich-text-editor',
  templateUrl: './rich-text-editor.component.html',
  styleUrls: ['./rich-text-editor.component.scss'],
  imports: [EditorComponent, NgClass, FormsModule],
  providers: [{ provide: TINYMCE_SCRIPT_SRC, useValue: '/tinymce/tinymce.min.js' }],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RichTextEditorComponent implements OnInit, OnChanges {
  // const
  RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH: number = RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH;

  @Input()
  isDisabled = false;

  @Input()
  hasCharacterLimit = false;

  @Input()
  minHeightInPx = 150;

  @Input()
  placeholderText = '';

  @Input()
  richText = '';

  @Output()
  richTextChange: EventEmitter<string> = new EventEmitter();

  characterCount = signal(0);

  // the argument passed to tinymce.init() in native JavaScript
  init: RawEditorOptions = {};

  private editorInstance?: Editor;

  defaultToolbar: string =
    'styles | forecolor backcolor ' +
    '| bold italic underline strikethrough subscript superscript ' +
    '| alignleft aligncenter alignright alignjustify ' +
    '| bullist numlist | link image charmap emoticons';

  ngOnInit(): void {
    this.init = this.getEditorSettings();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isDisabled']) {
      this.editorInstance?.mode.set(this.isDisabled ? 'readonly' : 'design');
    }
  }

  private getEditorSettings(): RawEditorOptions {
    return {
      base_url: '/tinymce',
      skin_url: '/tinymce/skins/ui/oxide',
      content_css: '/assets/tinymce/tinymce.css',
      suffix: '.min',
      height: this.minHeightInPx,
      resize: true,
      inline: false,
      relative_urls: false,
      convert_urls: false,
      remove_linebreaks: false,
      entity_encoding: 'raw',
      placeholder: this.placeholderText,
      plugins: [
        'advlist',
        'autolink',
        'autoresize',
        'lists',
        'link',
        'image',
        'charmap',
        'anchor',
        'searchreplace',
        'wordcount',
        'visualblocks',
        'visualchars',
        'code',
        'insertdatetime',
        'nonbreaking',
        'save',
        'table',
        'directionality',
        'emoticons',
      ],
      menubar: false,
      autoresize_bottom_margin: 50,

      toolbar1: this.defaultToolbar,
      setup: (editor: Editor) => {
        this.editorInstance = editor;
        editor.on('init', () => {
          this.editorInstance?.mode.set(this.isDisabled ? 'readonly' : 'design');
        });

        if (this.hasCharacterLimit) {
          editor.on('GetContent', () => {
            queueMicrotask(() => {
              this.characterCount.set(this.getCurrentCharacterCount(editor));
            });
          });
          editor.on('keypress', (event: EditorEvent<KeyboardEvent>) => {
            const currentCharacterCount = this.getCurrentCharacterCount(editor);
            if (currentCharacterCount >= RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH) {
              event.preventDefault();
            }
          });
          editor.on('paste', (event: EditorEvent<ClipboardEvent>) => {
            const contentBeforePasteEvent = editor.getContent({ format: 'text' });
            setTimeout(() => {
              const currentCharacterCount = this.getCurrentCharacterCount(editor);
              if (currentCharacterCount >= RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH) {
                event.preventDefault();
                const contentAfterPasteEvent = editor.getContent({ format: 'text' });
                let firstDifferentIndex = 0;
                while (contentBeforePasteEvent[firstDifferentIndex] === contentAfterPasteEvent[firstDifferentIndex]) {
                  firstDifferentIndex += 1;
                }
                const contentBeforeFirstDifferentIndex = contentBeforePasteEvent.substring(0, firstDifferentIndex);
                const contentAfterFirstDifferentIndex = contentBeforePasteEvent.substring(firstDifferentIndex);
                const lengthExceed = currentCharacterCount - RICH_TEXT_EDITOR_MAX_CHARACTER_LENGTH;
                const pasteContentLength = contentAfterPasteEvent.length - contentBeforePasteEvent.length;
                const pasteContent = contentAfterPasteEvent.substring(
                  firstDifferentIndex,
                  firstDifferentIndex + pasteContentLength,
                );
                const truncatedPastedText = pasteContent.substring(0, pasteContentLength - lengthExceed);
                const finalContent =
                  contentBeforeFirstDifferentIndex + truncatedPastedText + contentAfterFirstDifferentIndex;
                editor.setContent(finalContent);

                // This sets the cursor to the end of the text.
                const selection = editor.selection.getRng();
                const newCursorPosition = firstDifferentIndex + truncatedPastedText.length;
                const newRange = editor.dom.createRng();
                newRange.setStart(selection.startContainer, newCursorPosition);
                newRange.collapse(true);
                editor.selection.setRng(newRange);
              }
            }, 0);
          });
        }
      },
    };
  }

  getCurrentCharacterCount(editor: Editor): number {
    const wordCountApi = editor.plugins['wordcount'];
    const currentCharacterCount = wordCountApi['body'].getCharacterCount();
    return currentCharacterCount;
  }
}
