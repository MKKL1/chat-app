export function createNotification(title: string, body: string, action?: () => void){
  const notification = new Notification(title, {
    body: body,
    requireInteraction: false
  });

  notification.onclick = () => {
    if(action){
      action();
    }
    notification.close();
  }

  setTimeout(() => {
    notification.close();
  }, 30000);
}

export function showNotification(title: string, body: string, action?: () => void){
  if('Notification' in window){
    if(Notification.permission == 'granted'){
      createNotification(title, body, action);
    } else if(Notification.permission !== 'denied'){
      Notification.requestPermission().then((permission) => {
        if(permission === 'granted'){
          createNotification(title, body, action);
        }
      });
    }
  }
}
