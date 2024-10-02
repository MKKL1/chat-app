export type EventCallback = (data: any) => void;

/*
 * EventHandler provides way to store events callbacks functions and invoke them
 */
export class EventHandler{
  /*
   * Object which stores functions with EventCallback type
   * upon key corresponding to event name returned from api
   */
  private eventsCallbacks: { [key: string]: EventCallback} = {};

  /*
   * Adds new event callback
   * @param {string} eventName - name upon which callback will be stored
   * @param {EventCallback} handler - function which will be invoked to handle callback
   */
  public add(eventName: string, handler: EventCallback){
    if(!this.eventsCallbacks[eventName]){
      this.eventsCallbacks[eventName] = handler;
    } else {
      console.error("Event is already added");
    }
  }

  /*
   * invoke event callback
   * @param {string} eventName - key of callback which will be invoked
   * @param {any} data - data passed to callback function
   */
  public handleEvent(eventName: string, data: any){
    if(eventName in this.eventsCallbacks){
      this.eventsCallbacks[eventName](data);
    } else {
      console.error(`Event with name ${eventName} is not defined`);
    }
  }
}
