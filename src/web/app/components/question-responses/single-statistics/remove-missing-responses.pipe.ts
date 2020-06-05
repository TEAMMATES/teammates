import { Pipe, PipeTransform } from '@angular/core';
import { ResponseOutput } from '../../../../types/api-output';

/**
 * Pipe to remove missing responses in an array of response.
 */
@Pipe({
  name: 'removeMissingResponses',
})
export class RemoveMissingResponsesPipe implements PipeTransform {

  /**
   * Filters out missing responses.
   */
  transform(responses: ResponseOutput[]): any {
    return responses.filter((response: ResponseOutput) => !response.isMissingResponse);
  }

}
