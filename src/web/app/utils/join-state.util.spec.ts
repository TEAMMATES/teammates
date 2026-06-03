import { JoinState } from '../../types/api-output';
import { joinStateToString } from './join-state.util';

describe('joinStateToString', () => {
  it('should return "Joined" for JOINED state', () => {
    expect(joinStateToString(JoinState.JOINED)).toBe('Joined');
  });

  it('should return "Yet to Join" for NOT_JOINED state', () => {
    expect(joinStateToString(JoinState.NOT_JOINED)).toBe('Yet to Join');
  });

  it('should return "Unknown" when state is undefined', () => {
    expect(joinStateToString(undefined)).toBe('Unknown');
  });

  it('should return "Unknown" when state is not provided', () => {
    expect(joinStateToString()).toBe('Unknown');
  });
});
