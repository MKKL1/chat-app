import {createFeatureSelector, createSelector} from "@ngrx/store";
import {CommunitiesState} from "./community.reducer";

export const selectCommunitiesState = createFeatureSelector<CommunitiesState>('communitiesState');

export const selectCommunities = createSelector(
  selectCommunitiesState,
  (state: CommunitiesState) => state.communities
);
