import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {KeycloakService} from "keycloak-angular";

export class MockKeycloakService{
  getUsername(){
    return 'Username';
  }

  logout(){

  }
}

describe('UserService', () => {
  let service: UserService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting(),
        {provide: KeycloakService, useClass: MockKeycloakService}
      ]
    });

    httpTesting = TestBed.inject(HttpTestingController);
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set user',() => {

  });

  it('should get user',() => {

  });

  it('should fetch user data',() => {

  });

  it('should register user',() => {

  });

  it('should edit avatar',() => {

  });

  it('should edit description',() => {

  });

  it('should delete account',() => {

  });
});
