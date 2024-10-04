import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommunityComponent } from './community.component';
import {CommunityQuery} from "../../../store/community/community.query";
import {MatDialog} from "@angular/material/dialog";

describe('CommunityComponent', () => {
  let component: CommunityComponent;
  let fixture: ComponentFixture<CommunityComponent>;

  let communityQuery: CommunityQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommunityComponent],
      providers: [MatDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommunityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    communityQuery = TestBed.inject(CommunityQuery);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
