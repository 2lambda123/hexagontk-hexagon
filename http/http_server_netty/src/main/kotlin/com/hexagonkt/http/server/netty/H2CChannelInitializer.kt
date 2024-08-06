package com.hexagonkt.http.server.netty

import kotlin.Int.Companion.MAX_VALUE
import io.netty.util.concurrent.EventExecutorGroup
import io.netty.util.AsciiString
import io.netty.handler.codec.http2.*
import io.netty.handler.codec.http2.Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME
import io.netty.channel.socket.SocketChannel
import com.hexagonkt.http.handlers.HttpHandler
import com.hexagonkt.http.server.HttpServerSettings
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpServerUpgradeHandler


internal class H2CChannelInitializer(
    private val handlers: Map<HttpMethod, HttpHandler>,
    private val executorGroup: EventExecutorGroup?,
    private val settings: HttpServerSettings,
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(channel: SocketChannel) {
        val pipeline = channel.pipeline()
        val sourceCodec = HttpServerCodec()
        val connection = DefaultHttp2Connection(true)

//        pipeline.addLast(Http2Cle)
    }

    private fun initChannelBack(channel: SocketChannel) {
        val pipeline = channel.pipeline()
        val sourceCodec = HttpServerCodec()
        val connection = DefaultHttp2Connection(true)

        val listener = InboundHttp2ToHttpAdapterBuilder(connection)
            .propagateSettings(true)
            .maxContentLength(MAX_VALUE)
            .build()

        val http2Handler = HttpToHttp2ConnectionHandlerBuilder()
            .connection(connection)
            .frameListener(listener)
            .build()

        val upgradeCodecFactory = HttpServerUpgradeHandler.UpgradeCodecFactory { protocol ->
            if (AsciiString.contentEquals(HTTP_UPGRADE_PROTOCOL_NAME, protocol))
                Http2ServerUpgradeCodec(http2Handler)
            else
                null
        }

        val upgradeHandler = HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory, MAX_VALUE)

        val cleartextHttp2ServerUpgradeHandler =
            CleartextHttp2ServerUpgradeHandler(sourceCodec, upgradeHandler, http2Handler)

        pipeline.addLast(cleartextHttp2ServerUpgradeHandler)
        val nettyServerHandler = NettyServerHandler(handlers, null, false)
        if (executorGroup == null)
            pipeline.addLast(nettyServerHandler)
        else
            pipeline.addLast(executorGroup, nettyServerHandler)
    }
}
