import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe to handle the transformation of an InstructorPermissionRole to a name.
 */
@Pipe({
  name: 'stripHtmlTags',
})
export class StripHtmlTagsPipe implements PipeTransform {

  /**
   * Transforms InstructorPermissionRole to a name.
   */
  transform(html: string): any {
    return html.replace(/(<([^>]+)>)/ig, '');
  }

}
