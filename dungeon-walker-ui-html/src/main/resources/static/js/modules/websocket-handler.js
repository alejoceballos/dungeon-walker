const init = (protocol, host, endpoint) => {

    let ws;

    const connect = (
        onConnectCallback,
        onDisconnectCallback,
        onErrorCallback,
        onMessageCallback
    ) => {
        // ws://localhost:8085/ws-server/ws-endpoint
        ws = new WebSocket(`${protocol}://${host}/${endpoint}`);

        ws.onopen = event => {
            onConnectCallback();
        };

        ws.onclose = event => {
            onDisconnectCallback(event.wasClean, event.reason, event.code);
        }

        ws.onerror = (event) => {
            onErrorCallback(event);
        }

        ws.onmessage = event => {
            onMessageCallback(JSON.parse(event.data));
        };
    }

    const disconnect = () => {
        if (ws?.readyState === WebSocket.OPEN || ws?.readyState === WebSocket.CONNECTING) {
            ws.close();
        }
    };

    const sendAuthentication = token => {
        ws.send(JSON.stringify(
            {
                type: "authentication",
                data: {
                    token
                }
            }
        ));
    }

    const sendHeartbeat = () => {
        ws.send(JSON.stringify({
            type: "heartbeat",
            data: {
                timestamp: new Date()
            }
        }));
    }

    const sendMovement = direction => {
        ws.send(JSON.stringify({
            type: "movement",
            data: {
                direction: direction
            }
        }));
    }

    const sendAbandon = () => {
        ws.send(JSON.stringify({
            type: "abandon",
            data: {}
        }));
    }

    return {
        connect,
        disconnect,
        sendAuthentication,
        sendHeartbeat,
        sendMovement,
        sendAbandon
    }

}

export {
    init
}