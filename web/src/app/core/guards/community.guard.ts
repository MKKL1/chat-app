import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {CommunityQuery} from "../../features/store/community/community.query";

// Some pages can be accessed before user has selected community to which content showed on page belongs
// E.g. user can add channel to community even if app don't know community id yet
// This guard is preventing this situations by checking if community is in store
export const communityGuard: CanActivateFn = (route, state) => {
  const router: Router = inject(Router);
  const communityQuery: CommunityQuery = inject(CommunityQuery);

  let isSelected: boolean = communityQuery.hasActive();

  return isSelected ? isSelected : router.createUrlTree(['/','app','communities']);
};
