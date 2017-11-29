package ag.boersego.bgjs.modules

import ag.boersego.bgjs.*
import ag.boersego.v8annotations.*
import okhttp3.*

/**
 * Created by Kevin Read <me@kevin-read.com> on 23.11.17 for myrmecophaga-2.0.
 * Copyright (c) 2017 BörseGo AG. All rights reserved.
 */

@V8Class(creationPolicy = V8ClassCreationPolicy.JAVA_ONLY)
class BGJSWebSocket : JNIV8Object, Runnable {

    private enum class ReadyState {
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED
    }


    private var socket: WebSocket? = null

    override fun run() {
        val request = Request.Builder().url(_url).build()
        socket = httpClient.newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                _readyState = ReadyState.OPEN
                onopen?.callAsV8Function()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {
                super.onFailure(webSocket, t, response)
                _readyState = ReadyState.CLOSED

                onerror?.callAsV8Function()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                onmessage?.callAsV8Function(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                _readyState = ReadyState.CLOSED

                if (onclose != null) {
                    val closeEvent = JNIV8GenericObject.Create(v8Engine)
                    closeEvent.setV8Field("code", code)
                    closeEvent.setV8Field("reason", reason)
                    onclose?.callAsV8Function(closeEvent)
                    closeEvent.dispose()
                }
                socket = null
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)

                _readyState = ReadyState.CLOSING
            }
        })
    }

    @V8Function
    fun close(args: Array<Any>): Any? {
        if (socket != null) {
            val code = if (args.isNotEmpty() && args[0] is Number) (args[0] as Number).toInt() else 1000
            val reason = if (args.size > 1 && args[1] is String) (args[1] as String) else null
            socket?.close(code, reason)
        }
        return JNIV8Undefined.GetInstance()
    }

    @V8Function
    fun send(args: Array<Any>): Any? {
        if (_readyState != ReadyState.OPEN) {
            throw RuntimeException("INVALID_STATE_ERR")
        }
        if (socket != null) {
            if (args.isEmpty() || args[0] !is String) {
                throw IllegalArgumentException("send needs one argument of type string")
            }
            socket?.send(args[0] as String)
            return true
        }
        return false
    }

    constructor(engine: V8Engine) : super(engine)

    private var onclose: JNIV8Function? = null
        @V8Getter get
        @V8Setter set
    private var onerror: JNIV8Function? = null
        @V8Getter get
        @V8Setter set
    private var onmessage: JNIV8Function? = null
        @V8Getter get
        @V8Setter set
    private var onopen: JNIV8Function? = null
        @V8Getter get
        @V8Setter set
    private var _readyState: ReadyState = ReadyState.CONNECTING

    @Suppress("unused")
    var readyState: Any? = null
        @V8Getter
        get() = _readyState.ordinal

    var url: Any? = null
        @V8Getter
        get() = _url

    @Suppress("unused")
    var bufferedAmount: Any? = null
        @V8Getter
        get() = if (socket != null) socket?.queueSize()?.toInt() else 0


    private lateinit var httpClient: OkHttpClient

    private lateinit var _url: String

    fun setData(okHttpClient: OkHttpClient, url: String) {
        this.httpClient = okHttpClient
        this._url = url
    }
}

class BGJSModuleWebSocket (private var okHttpClient: OkHttpClient) : JNIV8Module("websocket") {

    override fun Require(engine: V8Engine, module: JNIV8GenericObject?) {
        val exports = JNIV8GenericObject.Create(engine)

        exports.setV8Field("create", JNIV8Function.Create(engine, { _, arguments ->
            if (arguments.isEmpty() || arguments[0] !is String) {
                throw IllegalArgumentException("create needs one string argument (url)")
            }
            val websocket = BGJSWebSocket(engine)
            websocket.setData(okHttpClient, arguments[0] as String)
            engine.enqueueOnNextTick(websocket)

            websocket
        }))

        module?.setV8Field("exports", exports)
    }
    companion object {
        init {
            JNIV8Object.RegisterV8Class(BGJSWebSocket::class.java)
        }
    }

}