import {Community} from "../../models/community";
import {createReducer, on} from "@ngrx/store";
import * as CommunityActions from './community.actions';

export interface CommunitiesState {
  communities: Community[],
  error: string | null
}

export const initState: CommunitiesState = {
  communities: [],
  error: null
}

export const communityReducer = createReducer(
  initState,
  on(CommunityActions.addCommunity, (state, {community}) => ({
    ...state,
    communities: [...state.communities, community]
  })),
  on(CommunityActions.loadCommunitiesSuccess, (state, {communities}) => ({
    ...state,
    communities,
    error: null
  })),
  on(CommunityActions.loadCommunitiesFailure, (state, {error}) => ({
    ...state,
    error
  }))
);
