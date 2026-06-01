import { JoinState } from '../../types/api-output';

export function joinStateToString(joinState?: JoinState): string {
  switch (joinState) {
    case JoinState.JOINED:
      return 'Joined';
    case JoinState.NOT_JOINED:
      return 'Yet to Join';
    default:
      return 'Unknown';
  }
}
