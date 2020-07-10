import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

/**
 * A rich text editor.
 */
@Component({
  selector: 'tm-rich-text-editor',
  templateUrl: './rich-text-editor.component.html',
  styleUrls: ['./rich-text-editor.component.scss'],
})
export class RichTextEditorComponent implements OnInit {

  @Input()
  isDisabled: boolean = false;

  @Input()
  isInlineMode: boolean = true;

  @Input()
  minHeightInPx: number = 150;

  @Input()
  placeholderText: string = '';

  @Input()
  richText: string = '';

  @Output()
  richTextChange: EventEmitter<string> = new EventEmitter();

  // the argument passed to tinymce.init() in native JavaScript
  init: any = {};

  constructor() { }

  ngOnInit(): void {
    this.init = this.getEditorSettings();
  }

  private getEditorSettings(): any {
    return {
      base_url: '/tinymce',
      skin_url: '/tinymce/skins/ui/oxide',
      suffix: '.min',
      resize: false,
      fontsize_formats: '8pt 9pt 10pt 11pt 12pt 14pt 16pt 18pt 20pt 24pt 26pt 28pt 36pt 48pt 72pt',
      font_formats: 'Andale Mono=andale mono,times;'
          + 'Arial=arial,helvetica,sans-serif;'
          + 'Arial Black=arial black,avant garde;'
          + 'Book Antiqua=book antiqua,palatino;'
          + 'Comic Sans MS=comic sans ms,sans-serif;'
          + 'Courier New=courier new,courier;'
          + 'Georgia=georgia,palatino;'
          + 'Helvetica=helvetica;'
          + 'Impact=impact,chicago;'
          + 'Symbol=symbol;'
          + 'Tahoma=tahoma,arial,helvetica,sans-serif;'
          + 'Terminal=terminal,monaco;'
          + 'Times New Roman=times new roman,times;'
          + 'Trebuchet MS=trebuchet ms,geneva;'
          + 'Verdana=verdana,geneva;'
          + 'Webdings=webdings;'
          + 'Wingdings=wingdings,zapf dingbats',
      relative_urls: false,
      convert_urls: false,
      remove_linebreaks: false,
      plugins: [
        'placeholder',
        'advlist autolink lists link image charmap print hr anchor',
        'searchreplace wordcount visualblocks visualchars code fullscreen',
        'insertdatetime nonbreaking save table directionality',
        'emoticons paste textpattern',
      ],

      toolbar1: 'insertfile undo redo | styleselect | bold italic underline '
          + '| alignleft aligncenter alignright alignjustify '
          + '| bullist numlist outdent indent | link image',
      toolbar2: 'print | forecolor backcolor | fontsizeselect fontselect | charmap emoticons | fullscreen',
    };
  }

}
