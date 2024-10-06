import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainComponent } from './main.component';
import {UserService} from "../../../../core/services/user.service";
import {EventService} from "../../../../core/events/event.service";
import {provideRouter} from "@angular/router";

// nothing really to test for now

const userServiceMock = {
  fetchUserData: jest.fn()
};

const eventServiceMock = {
  init: jest.fn()
};

describe('MainComponent', () => {
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainComponent],
      providers: [
        {provide: UserService, useValue: userServiceMock},
        {provide: EventService, useValue: eventServiceMock},
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call service in ngOnInit', () => {
    const userServiceSpy = jest.spyOn(userServiceMock, 'fetchUserData');
    const eventServiceSpy = jest.spyOn(eventServiceMock, 'init');

    component.ngOnInit();

    expect(userServiceSpy).toHaveBeenCalled();
    expect(eventServiceSpy).toHaveBeenCalled();
  });
});
