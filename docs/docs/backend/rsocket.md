# RSocket
## How server handles event streams
For each stream request server requests rabbitmq to create temporary queue and corresponding to it binding.
After requester disconnects for any reason, queue and binding are deleted.

<!-- Note: reactor's implementation of AMQP don't reuse channels after consumer disconnects. Which means that each request
always opens new channel. If it becomes a problem in the future, custom channel pool can be implemented to mitigate this issue. -->