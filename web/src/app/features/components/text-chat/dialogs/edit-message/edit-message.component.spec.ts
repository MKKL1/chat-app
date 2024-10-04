import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditMessageComponent } from './edit-message.component';
import {MessageService} from "../../../../services/message.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const messageServiceMock = {
  editMessage: jest.fn()
};

const matDataObjMock = {
  message: {
    id: '',
    text: '',
    channelId: '',
    userId: '',
    edited: false,
    updatedAt: new Date(),
    reactions: []
  }
};

describe('EditMessageComponent', () => {
  let component: EditMessageComponent;
  let fixture: ComponentFixture<EditMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditMessageComponent, NoopAnimationsModule],
      providers: [
        {provide: MessageService, useValue: messageServiceMock},
        {provide: MAT_DIALOG_DATA, useValue: matDataObjMock},
        {provide: MatDialogRef, useValue: {}}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
