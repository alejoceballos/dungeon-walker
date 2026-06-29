const init = (webSocketContext, messageContext) => {

    let interval;

    const startTimer = () => {
        if (!interval) {
            interval = setInterval(() => {
                messageContext.outboundHeartbeat("Sending heartbeat");
                webSocketContext.sendHeartbeat();
            }, 10000);
        }
    }

    const stopTimer = () => {
        if (interval) {
            clearInterval(interval);
            interval = undefined;
        }
    };

    return {
        startTimer,
        stopTimer
    }

}

export {
    init
}