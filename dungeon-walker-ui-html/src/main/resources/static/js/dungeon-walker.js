import {init as initMessages} from "./modules/messages-handler.js"
import {init as initAuth} from "./modules/authentication-handler.js";
import {init as initWebSocket} from "./modules/websocket-handler.js"
import {init as initHeartbeat} from "./modules/heartbeat-handler.js"
import {init as initDungeonDisplay} from "./modules/dungeon-display-handler.js";
import {init as initInput} from "./modules/input-handler.js";

const commandInput = document.getElementById("command-input");

const clearInput = () => commandInput.value = "";

const messagesList = document.getElementById("messageList");
messagesList.replaceChildren();

const messagesHandler = {
    addMessage: message => {
        const li = document.createElement("li");
        li.textContent = message;
        messagesList.prepend(li);
    },

    removeLastMessage: () => {
        const lastMessage = messagesList.lastChild;
        if (lastMessage) {
            messagesList.removeChild(lastMessage);
        }
    },

    messageCount: () => messagesList.childElementCount
};

const dungeonTable = document.getElementById("dungeon-table");
dungeonTable.replaceChildren();

const dungeonHandler = {
    addRow: y => {
        const tr = document.createElement("tr");
        tr.setAttribute("id", `row-${y}`);
        dungeonTable.appendChild(tr);
    },

    addCell: (x, y) => {
        const td = document.createElement("td");
        td.setAttribute("id", `cell-${x}-${y}`);
        document.getElementById(`row-${y}`).appendChild(td);
    },

    addThing: (icon, x, y) => {
        document.getElementById(`cell-${x}-${y}`).textContent = icon;
    },

    removeThing: (x, y) => {
        document.getElementById(`cell-${x}-${y}`).textContent = "";
    }
};

window.initDungeonWalker = (
    messagesMaxCount,
    securityProtocol,
    securityHost,
    securityEndpoint,
    webSocketProtocol,
    webSocketHost,
    webSocketEndpoint
) => {
    const messageContext = initMessages(
        messagesHandler,
        messagesMaxCount);

    const authContext = initAuth(
        securityProtocol,
        securityHost,
        securityEndpoint);

    const webSocketContext = initWebSocket(
        webSocketProtocol,
        webSocketHost,
        webSocketEndpoint);

    const heartbeatContext = initHeartbeat(
        webSocketContext,
        messageContext);

    const dungeonDisplayContext = initDungeonDisplay(dungeonHandler);

    const inputContext = initInput(
        clearInput,
        authContext,
        messageContext,
        webSocketContext,
        heartbeatContext,
        dungeonDisplayContext);

    commandInput.addEventListener("keydown", event => {
        const input = document.getElementById("command-input").value;

        if (event.key === "Enter") {
            const command = inputContext.getCommand(input);
            command();
        }
    });

}
