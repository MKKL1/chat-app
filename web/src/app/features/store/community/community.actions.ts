import {createAction, props} from "@ngrx/store";
import {Community} from "../../models/community";

export const loadCommunities = createAction('[Community] Load communities');

export const loadCommunitiesSuccess = createAction(
  '[Community] Load communities success',
  props<{communities: Community[]}>()
);

export const loadCommunitiesFailure = createAction(
  '[Community] Load communities failure',
  props<{error: any}>()
);
