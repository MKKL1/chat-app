import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {BehaviorSubject, filter, map, mergeMap, Observable, of, tap} from "rxjs";
import {Community} from "../models/community";
import {UserService} from "../../core/services/user.service";
import {CommunityStore} from "../store/community.store";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private readonly apiPath: string = environment.api + "communities";

  private communitiesSubject: BehaviorSubject<Community[]> = new BehaviorSubject<Community[]>([]);

  constructor(private http: HttpClient, private communityStore: CommunityStore, private userService: UserService) { }

  // after fetching all records beside first return error
  fetchCommunity(id: string){
    console.log("id in service: " + id);
    this.http.get<Community>(this.apiPath + "/" + id).subscribe({
      next: (community: Community) => this.communityStore.selectCommunity(community),
      error: (err) => console.error(err)
    });
  }

  // for now backend returns community without owner
  // communities list has changed id's
  fetchCommunities() {
     this.http.get<Community[]>(this.apiPath
     ).subscribe({
       next: (communities) => this.communitiesSubject.next(communities),
       error: (err) => console.error(err)
     });
  }

  getUserCommunities(): Observable<Community[]> {
    return this.communitiesSubject.asObservable();
  }

  getOwnedCommunities(): Observable<Community[]> {
    return this.communitiesSubject.asObservable().pipe(
      map((communities: Community[]) =>
        communities.filter((community: Community) =>
          community.ownerId == this.userService.getUser().id))
    );
  }

  // change those types
  createCommunity(form: {name: string}) {
    this.http.post<Community>(this.apiPath, {name: form.name})
      .subscribe(
        community => this.communitiesSubject.next(
          [
            ...this.communitiesSubject.getValue(),
            community
          ]
        )
      );
  }

  // backend don't work yet
  deleteCommunity(id: string) {
    return this.http.delete(this.apiPath + "/" + id);
  }

  createInvitation(id: string, days: number){
    return this.http.post<{link: string}>(this.apiPath + "/" + id + "/invite",{days: days});
  }

  acceptInvitation(communityId: string, invitationId: string){
    return this.http.post(environment.api + "communities/" + communityId + "/join", {invitationId: invitationId});
  }

}
