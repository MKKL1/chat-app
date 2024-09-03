import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {BehaviorSubject, map, mergeMap, Observable, of, tap} from "rxjs";
import {Community} from "../models/community";
import {CommunityResponse} from "../models/community-response";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private readonly apiPath: string = environment.api + "communities";

  private communitiesSubject: BehaviorSubject<Community[]> = new BehaviorSubject<Community[]>([]);

  constructor(private http: HttpClient) { }

  // for now backend returns community without owner
  fetchCommunities() {
     this.http.get<CommunityResponse[]>(this.apiPath
     ).subscribe(data => {
       console.log(data);
     });
  }

  getUserCommunities(): Observable<Community[]> {
    return this.communitiesSubject.asObservable();
  }

  // getOwnedCommunities(): Observable<Community> {
  //
  // }

  // change those types
  createCommunity(form: {name: string}) {
    this.http.post<Community>(this.apiPath, {name: form.name})
      .pipe(
        tap((community: Community) => {
          console.log("community: ");
          console.log(community);
        })).subscribe(
          community => this.communitiesSubject.next(
            [
              ...this.communitiesSubject.getValue(),
              community
            ]
          )
        );
  }
}
