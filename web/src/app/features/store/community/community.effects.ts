import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import * as CommunityActions from './community.actions';
import {CommunityService} from "../../services/community.service";
import {catchError, map, mergeMap, of} from "rxjs";

@Injectable()
export class CommunityEffects {
  // loadCommunities$ = createEffect(() =>
  //   this.actions$.pipe(
  //     ofType(CommunityActions.loadCommunities),
  //     mergeMap(() => this.communityService.getAllCommunities().pipe(
  //       map((communities) => CommunityActions.loadCommunitiesSuccess({communities})),
  //       catchError((error) => of(CommunityActions.loadCommunitiesFailure({error})))
  //       )
  //     )
  //   )
  // );

  // that boilerplate is insane
  // I'm starting to think about just using services

  // addCommunity$ = createEffect(() =>
  //   this.actions$.pipe(
  //     ofType(CommunityActions.addCommunity),
  //     mergeMap(({community}) => this.communityService.createCommunity(community).pipe(
  //       map((newCommunity) => CommunityActions.addCommunity({community: newCommunity})),
  //       catchError((error) => of(CommunityActions.a))
  //     ))
  //   ))

  constructor(private actions$: Actions, private communityService: CommunityService) {
  }
}
