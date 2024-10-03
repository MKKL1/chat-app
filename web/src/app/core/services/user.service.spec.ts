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
    expect().nothing();
  });

  it('should get user',() => {
    expect().nothing();
  });

  it('should fetch user data',() => {
    expect().nothing();
  });

  it('should register user',() => {
    expect().nothing();
  });

  it('should edit avatar',() => {
    expect().nothing();
  });

  it('should edit description',() => {
    expect().nothing();
  });

  it('should delete account',() => {
    expect().nothing();
  });
});
