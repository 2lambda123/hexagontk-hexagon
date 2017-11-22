package com.hexagonkt.events.rabbitmq

import com.hexagonkt.events.EventsPort
import org.testng.annotations.Test

@Test class RabbitMqAdapterTest {
    /**
     * TODO Add asserts
     */
    fun `event manager` () {
        val engine: EventsPort = RabbitMqAdapter()
        engine.consume(com.hexagonkt.events.rabbitmq.RabbitTest.Sample::class) {
            if (it.str == "no message error")
                throw IllegalStateException()
            if (it.str == "message error")
                error("message")
        }
        engine.publish(com.hexagonkt.events.rabbitmq.RabbitTest.Sample("foo", 1))
//        EventManager.publish(Sample("no message error", 1))
//        EventManager.publish(Sample("message error", 1))
    }
}
