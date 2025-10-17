import {initializeCells} from "./cell-manager.js";

const mapSize = {}

function initializeWebSocket(currentMapSize, setMapSize) {
    const socket = new WebSocket("ws://localhost:8080/ws-endpoint");

    socket.onopen = event => {
        console.log("[WS Manager] Connected to server: ", event);
        const identity = {
            type: "identity",
            data: {
                id: "client-id",
                name: "client-name"
            }
        };
        socket.send(JSON.stringify(identity));
    };

    socket.onclose = event => {
        console.log(`[WS Manager] Disconnected from server. Code: ${event.code}, reason: ${event.reason}`);
    }

    socket.onerror = event => {
        console.log("[WS Manager] Error connecting to server: ", event);
    }

    socket.onmessage = event => {
        console.log("[WS Manager] Message from server", event.data);

        if (!event.data && !event.data) {
            return;
        }

        const content = JSON.parse(JSON.parse(event.data));

        if (content?.type === "MapSetup") {
            if (currentMapSize.width !== content.data.width ||
                currentMapSize.height !== content.data.height) {
                initializeCells(content.data.width, content.data.height);
                setMapSize(content.data);
            }
        }
    };
}

export {
    initializeWebSocket,
    mapSize
};