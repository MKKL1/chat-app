# Websocket streaming module

## Interacting with real-time data (from client perspective)
To simplify process of establishing connection with server, subscribing to streams, 
authentication and keeping main advantage of reactive streams, backpressure, 
both client and server use RSocket protocol.

### Connecting to RSocket server
To connect with RSocket server, client has to configure **web socket client** (not tcp) to use url: `ws://localhost:8083/events`
and then provide jwt token in composite metadata.

Java example:
```java
MimeType authenticationMimeType =
	MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
BearerTokenMetadata token = ...;
Mono<RSocketRequester> requester = RSocketRequester.builder()
	.setupMetadata(token, authenticationMimeType)
...
```

### RSocket interaction model
- request/response (stream of 1)
- request/stream (finite stream of many)
- fire-and-forget (no response)
- event subscription (infinite stream of many)?

Only request/stream is used in our case

### Subscribing to events
To lessen resource usage of websocket connection, concept of topics is used.
There is 1 endpoint for real-time communication:
- `/community/{communityId}/messages`

NOT IMPLEMENTED:
- `/community/{communityId}/general`?
- `/user/messages`
- `/user/...`

Each client can easily request and cancel many streams at once, 
this means that it shouldn't affect performance.

### Events format
Events are formatted in json, with two main fields:
- `name` - unique id of event
- `data` - payload of event (changes depending on name of event)

Example event message:
```json
{
  "name":"MESSAGE_CREATE_EVENT",
  "data": {
    "id":"123",
    "channel":"8507337136406528",
    "text":"Some text message",
    "user":"12817543232225280"
  }
}
```
As visible in example above, there are no null fields as sending empty fields is unnecessary in most cases.
Clients should implement optional fields in their receiving objects.

## How server handles event streams
For each stream request server requests rabbitmq to create temporary queue and corresponding to it binding.
After requester disconnects for any reason, queue and binding are deleted.

Note: reactor's implementation of AMQP don't reuse channels after consumer disconnects. Which means that each request
always opens new channel. If it becomes a problem in the future, custom channel pool can be implemented to mitigate this issue.