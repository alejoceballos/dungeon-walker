const init = (handler, maxMessages) => {

    const CONTEXT = {
        connected: false,
        authorized: false
    }

    const add = message => {
        if (handler.messageCount() === maxMessages) {
            handler.removeLastMessage();
        }

        const connected = CONTEXT.connected ? "🔗" : "⛓️‍💥";
        const authorized = CONTEXT.authorized ? "🔓" : "🔒";

        handler.addMessage(`${connected}${authorized} ${message}`);
    };

    const error = message => {
        add(`❌ ${message}`)
    }

    const serverError = message => {
        error(`💻${message}`)
    }

    const clientError = message => {
        error(`👤${message}`)
    }

    const warning = message => {
        add(`⚠️ ${message}`)
    }

    const info = message => {
        add(`ℹ️ ${message}`)
    }

    const inboundHeartbeat = message => {
        add(`💓🔻 ${message}`)
    }

    const outboundHeartbeat = message => {
        add(`💓🔺 ${message}`)
    }

    const inbound = message => {
        add(`🔻 ${message}`)
    }

    const outbound = message => {
        add(`🔺 ${message}`)
    }

    const connectionState = connected => CONTEXT.connected = connected;
    const authorizationState = authorized => CONTEXT.authorized = authorized;

    return {
        connectionState,
        authorizationState,
        serverError,
        clientError,
        error,
        warning,
        info,
        inboundHeartbeat,
        outboundHeartbeat,
        inbound,
        outbound
    }
}

export {
    init
};