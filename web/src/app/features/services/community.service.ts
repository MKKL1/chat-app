import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";
import {Observable} from "rxjs";
import {Community} from "../models/community";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {

  constructor(private http: HttpClient) { }

  getAllCommunities(): Observable<Community[]> {
    return this.http.get<Community[]>(environment.api + "communities");
  }

  createCommunity(form: {name: string}): Observable<any> {
    return this.http.post(environment.api + "communities", {name: form.name});
  }
}
