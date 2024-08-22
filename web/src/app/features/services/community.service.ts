import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environment";

@Injectable({
  providedIn: 'root'
})
export class CommunityService {

  constructor(private http: HttpClient) { }

  createCommunity(){
    this.http.get(environment.api + "users/me").subscribe(res => {
      console.log(res);
    });
  }
}
