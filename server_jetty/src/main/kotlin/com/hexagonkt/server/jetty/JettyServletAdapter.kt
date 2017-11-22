package com.hexagonkt.server.jetty

import com.hexagonkt.helpers.error
import com.hexagonkt.server.Server
import com.hexagonkt.server.ServerPort
import com.hexagonkt.server.servlet.ServletFilter
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener
import org.eclipse.jetty.util.component.LifeCycle
import java.net.InetSocketAddress
import java.util.*
import javax.servlet.DispatcherType
import org.eclipse.jetty.server.Server as JettyServer
import java.net.InetAddress.getByName as address

/**
 * TODO .
 */
class JettyServletAdapter(private val async: Boolean = false) : ServerPort {
    private var jettyServer: JettyServer? = null

    override fun runtimePort(): Int =
        ((jettyServer?.connectors?.get(0) ?: error) as ServerConnector).localPort.let {
            if (it == -1) error("Jetty port uninitialized. Use lazy evaluation for HTTP client ;)")
            else it
        }

    override fun started() = jettyServer?.isStarted ?: false

    override fun startup(server: Server, settings: Map<String, *>) {
        val serverInstance = JettyServer(InetSocketAddress(server.bindAddress, server.bindPort))
        jettyServer = serverInstance

        val context = ServletContextHandler(SESSIONS)
        context.addLifeCycleListener(object : AbstractLifeCycleListener() {
            override fun lifeCycleStarting(event: LifeCycle?) {
                val filter = ServletFilter (server.router.flatRequestHandlers())
                val dispatcherTypes = EnumSet.allOf(DispatcherType::class.java)
                val filterBind = context.servletContext.addFilter("filters", filter)
                filterBind.setAsyncSupported(async)
                filterBind.addMappingForUrlPatterns(dispatcherTypes, true, "/*")
            }
        })

        serverInstance.handler = context
        serverInstance.stopAtShutdown = true
        serverInstance.start()
    }

    override fun shutdown() {
        jettyServer?.stop()
    }
}
