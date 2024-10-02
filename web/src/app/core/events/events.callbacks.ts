import {EventHandler} from "./event.handler";
import {Message} from "../../features/models/message";

// with this solution it is not possible to use dependency injection,
// which makes event callbacks useless in almost every case

const handler = new EventHandler();

handler.add('MESSAGE_CREATE_EVENT', (data: Message) => {
  console.log('Event handling test');
});

export default handler;
