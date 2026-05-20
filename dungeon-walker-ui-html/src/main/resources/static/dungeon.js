const ws = new WebSocket("ws://localhost:8085/ws-server/ws-endpoint");

ws.onopen = () => {
    addMessage('🟢🔗 Connected!');
    startTimer();
};

ws.onclose = () => {
    stopTimer()
    addMessage('🔴⛓️‍💥 Disconnected!');
}

ws.onerror = () => error => {
    addMessage('🔴⚠️ Error: ' + error.message);
}

ws.onmessage = (event) => {
    addMessage('🟢⬅️ ' + event.data);
};

let counter = 0;

const addMessage = (message) => {
    const div = document.createElement('div');
    div.textContent = '(' + ++counter + ') ' + message;

    const messages = document.getElementById('messages');
    messages.prepend(div);
    messages.scrollTop = messages.scrollHeight;

    if (messages.childElementCount > 10) {
        messages.removeChild(messages.lastElementChild);
    }
}

let interval;

const startTimer = () => {
    interval = setInterval(() => {
        const json = JSON.stringify({
            type: "heartbeat",
            data: {
                timestamp: new Date()
            }
        });

        ws.send(json);
        addMessage('⚪➡️ ' + json);
    }, 10000);
}

const stopTimer = () => clearInterval(interval);

