package com.hexagonkt.server.servlet

import com.hexagonkt.server.Router

import java.util.*
import javax.servlet.DispatcherType
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

/**
 * Not a standard engine as it is not started/stopped
 * TODO Take care of wildcards (review servlet specs) to group filters
 * TODO Take care of wildcards (review servlet specs) to group routes in servlets
 * TODO Receive router in parameter (easier to use in Servlet containers)
 */
abstract class ServletServer(private val async: Boolean = false) : ServletContextListener {
    val serverRouter by lazy { createRouter() }

    abstract fun createRouter(): Router

    override fun contextInitialized(sce: ServletContextEvent) {
        val servletFilter = ServletFilter(serverRouter.flatRequestHandlers())
        val filter = sce.servletContext.addFilter("filters", servletFilter)
        filter.setAsyncSupported(async)
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
    }

    override fun contextDestroyed(sce: ServletContextEvent?) { /* Empty */ }
}
