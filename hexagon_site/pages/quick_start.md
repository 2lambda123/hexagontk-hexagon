
# What is Hexagon

Hexagon is a microservices framework written in [Kotlin] which pursose is to ease the building of
services (Web applications, APIs or queue consumers) that run inside cloud platforms.

It is meant to provide abstraction from underlying technoligies to be able to change them with
minimum impact.

It only supports Kotlin use, Java is not a targeted language for the framework.

# Why it was created

* More time reading framework docs than coding
* Frameworks targeted to everyone instead my custom needs
* Easy to hack better than work ok for every use case
* For fun!
* To learn Kotlin

# How it works

The framework is build upon smaller pieces:

## Modules

ports and adapters

## Components

events, core, templates

Features

Add Needed infrastructure for µS

## Adapters

feature implementations

The heavy lifting is done by these ones (Undertow, Jetty, RabbitMQ, MongoDB)

# Concepts

Service (API, Web, Consumer)

Paths

Routes

Filters

Handlers

Callbacks

Routers

Servers

Templates

Events

... Stores + Rest

# Teaser / Roadmap

Froze interface
The focus will be library ease of use and API freeze

Async

Tools (generate
Support Coreos, Docker, Systemd, CoreOS Fleet

Metrics

Registering

Logging

Health checks

Web to download starters

Tool for client requests

HTTP/2

Websockets

CBOR

Swagger

Clients and Servers contracts... when you define routes, and route documentation is in place
