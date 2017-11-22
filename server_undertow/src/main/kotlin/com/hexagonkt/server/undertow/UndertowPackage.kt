package com.hexagonkt.server.undertow

import com.hexagonkt.server.Router
import com.hexagonkt.server.Server
import com.hexagonkt.settings.SettingsManager

fun serve(
    settings: Map<String, *> = SettingsManager.settings,
    block: Router.() -> Unit): Server =
    com.hexagonkt.server.serve(UndertowAdapter(), settings, block)

fun server(
    settings: Map<String, *> = SettingsManager.settings,
    block: Router.() -> Unit): Server =
    com.hexagonkt.server.server(UndertowAdapter(), settings, block)
