import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteMessageComponent } from './delete-message.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MessageService} from "../../../../services/message.service";

const messageServiceMock = {
  deleteMessage: jest.fn()
};

const matDataObjMock = {
  id: '123'
};

describe('DeleteMessageComponent', () => {
  let component: DeleteMessageComponent;
  let fixture: ComponentFixture<DeleteMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteMessageComponent],
      providers: [
        {provide: MatDialogRef, useValue: {}},
        {provide: MAT_DIALOG_DATA, useValue: matDataObjMock},
        {provide: MessageService, useValue: messageServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
