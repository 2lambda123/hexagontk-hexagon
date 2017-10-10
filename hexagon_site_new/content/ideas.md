---
layout: site_page
title: Ideas
date: 2016-04-13 15:07
published: false
---

Composition over inheritance

Auth https://shiro.apache.org/java-authentication-guide.html
Subject: uid (can be a system, not a person)
Credential: pass, public Key
Principal: personal data name, nick, birthday
Role:
Permission: action:type:id
User: physical human user

Settings/Configuration: a component will have its credentials (as a user) to login in services
it will have a subject (its project name)

System:

Components: API, message broker
Applications: are a kind of component which is final (meant for users)
WebApplication, Desktop Application, Mobile Application or Browser Aplication

For components:
Services: local or remote functionalities (interfaces), implementations will be *Client or
*Engine Ie:
    MessagingService <- RabbitMqClient, JeroMqClient
    StorageService <- RedisClient, MongoDbClient, CouchBaseClient

    SettingsService <- LocalSettingsEngine, EventSettingsEngine
    ServerService <- ServletEngine, JettyServletEngine, UndertowEngine
    ClientService <- OkHttpEngine
    SerializationService <- MessagePackFormat
    TemplateService

    MetricsService <- MetricsClient TODO
    RegistryService <- ConsulClient TODO

ServiceClient: implementations to access a concrete type of service
ComponentClient:

Managers: singletons to manage across an application and/or between services
    EventManager
    SettingsManager
    TemplatesManager

Packaging and deployment

TODO
`Exchange.call` method to redirect to other handler
Add auto parsing/serializing of body/response based on a type
  Ie: Add get<Request, Response>("/path") {} (for all methods)
Add `Exchange.body/headers/params` as Sinatra. Ie: headers["Date"] = date
Streaming support if returning a stream

Settings load order:
  resources <- system properties <- environment variables <- files <- command line arguments

Render template if no route found and template with that name exist (after filter)

Use Hexagon static site builder (to be developed) to generate site
Create kjava.gradle, kjavascript.gradle, kandroid.gradle

Server contains a list of RequestHandler (a router is only a list of RequestHandler)
Move router funtions to package as functions that produce RequestHandlers
