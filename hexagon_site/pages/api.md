
API Reference
=============

## Modules

Modules that provide features without the need of custom implementations:

* [Hexagon Core](/api/hexagon_core/index.html)
* [Hexagon REST](/api/hexagon_rest/index.html)
* [Hexagon Scheduler](/api/hexagon_scheduler/index.html)
* [Hexagon Test](/api/hexagon_test/index.html)

## Ports

Interfaces to a certain feature that must be implemented by an adapter:

* [Client Port](/api/port_client/index.html)
* [Events Port](/api/port_events/index.html)
* [Server Port](/api/port_server/index.html)
* [Store Port](/api/port_store/index.html)

## Adapters

Concrete ports implementations:

### Events
* [RabbitMQ Events Adapter](/api/events_rabbitmq/index.html)

### Server
* [Servlet Server Adapter](/api/server_servlet/index.html)
* [Jetty Server Adapter](/api/server_jetty/index.html)
* [Undertow Server Adapter](/api/server_undertow/index.html)

### Templates
* [Pebble Templates Adapter](/api/templates_pebble/index.html)
* [Rocker Templates Adapter](/api/templates_rocker/index.html)
