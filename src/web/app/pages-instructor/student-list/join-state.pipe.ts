import { Pipe, PipeTransform } from '@angular/core';
import { JoinState } from "../../../types/api-output";

@Pipe({
  name: 'joinState'
})
export class JoinStatePipe implements PipeTransform {

  transform(joinState: JoinState): any {
    switch(joinState) {
      case (JoinState.JOINED):
        return 'Joined';
      case (JoinState.NOT_JOINED):
        return 'Yet to Join';
      default:
        return 'Unknown';
    }
  }

}
