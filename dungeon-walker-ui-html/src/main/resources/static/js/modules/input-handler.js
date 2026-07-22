const DOUBLE_SPACE = "  ";
const SINGLE_SPACE = " ";

const CONTEXT = {
    loggedIn: false
};

const init = (
    clearInput,
    authContext,
    messageContext,
    webSocketContext,
    heartbeatContext,
    dungeonDisplayContext
) => {
    const onConnect = token => {
        messageContext.connectionState(true);
        messageContext.info("Connected");
        messageContext.outbound("🗝️ Authorizing...");

        webSocketContext.sendAuthentication(token);
    };

    const onDisconnect = (wasClean, reason, code) => {
        messageContext.connectionState(false);
        messageContext.authorizationState(false);
        CONTEXT.loggedIn = false;

        const cleanly = wasClean ? "cleanly" : "not cleanly";
        const withReason = !!reason ? `with reason "${reason}" and` : "with";

        messageContext.info(`Connection ${cleanly} closed ${withReason} code ${code}`);

        heartbeatContext.stopTimer();
        dungeonDisplayContext.removeFromDungeon(authContext.getUsername());
        authContext.clearUsername();
    }

    const onError = () => {
        messageContext.error("Some unknown WebSocket error happened")
    }

    const onMessage = message => {
        const {type, data} = message;

        switch (type) {
            case "heartbeat":
                messageContext.inboundHeartbeat("Received heartbeat");
                break;

            case "server-errors":
                messageContext.serverError(JSON.stringify(data.errors));
                break;

            case "client-errors":
                messageContext.clientError(JSON.stringify(data.errors));
                break;

            case "server-message":
                messageContext.inbound(data.message);
                break;

            case "authentication":
                if (data.authenticated) {
                    CONTEXT.loggedIn = data.authenticated;
                    messageContext.info("Authenticated!");
                    messageContext.authorizationState(true);
                    heartbeatContext.startTimer();
                    clearInput();

                    return;
                }

                messageContext.authorizationState(false);
                heartbeatContext.stopTimer();
                messageContext.error("Authentication failed!");

                break;

            case "dungeon-state":
                messageContext.inbound(`Dungeon state: ${JSON.stringify(data)}`);

                if (!dungeonDisplayContext.isDungeonCreated()) {
                    dungeonDisplayContext.create(data);
                }

                dungeonDisplayContext.update(data);

                break;

            case "cell-state":
                messageContext.inbound(`Cell state: ${JSON.stringify(data)}`);

                if (!dungeonDisplayContext.isDungeonCreated()) {
                    messageContext.warning(`Cannot update cell state. Dungeon is not created`);
                    break;
                }

                dungeonDisplayContext.update(data);
                break;

            default:
                messageContext.error(`Unknown message type: "${type}"`);
        }
    };

    const ignoreCommand = () => () => undefined;

    const invalidCommand = (input) => () => messageContext.error(`Invalid command: "${input}"`);

    const loginCommand = (args) => {
        if (CONTEXT.loggedIn) {
            return () => messageContext.warning("Already logged in. Log out first.");
        }

        if (args.length !== 2) {
            messageContext.error(`Invalid command format. Use: login <username> <password>`);
            return;
        }

        const username = args[0];
        const password = args[1];

        return async () => {
            messageContext.info(`Sending credentials...`);
            const response = await authContext.requestToken(username, password);

            if (response.status === "ERROR") {
                messageContext.clientError(`Login failed. ${response.message}`);
                return;
            }

            messageContext.info(`Credentials accepted. Connecting...`);
            webSocketContext.disconnect();
            const onConnectProxy = () => onConnect(response.token);
            webSocketContext.connect(onConnectProxy, onDisconnect, onError, onMessage);
        }
    }

    const logoutCommand = () => {
        if (!CONTEXT.loggedIn) {
            return () => messageContext.warning("You are not logged in.");
        }

        messageContext.outbound(`👋 Leaving the game`);
        clearInput();
        return () => webSocketContext.disconnect();
    }

    const moveCommand = direction => {
        if (!CONTEXT.loggedIn) {
            return () => messageContext.warning("You are not logged in.");
        }

        messageContext.outbound(`🚶‍➡️ Moving: ${direction}`);
        clearInput();
        return () => webSocketContext.sendMovement(direction);
    }

    const helpCommand = () => {
        return () => alert(`
            Available commands:
            — help (or h):
                Show this help message<br>
            — login (or li) <username> <password>:
                Log in to the game with the specified credentials
            — logout (or lo):
                Log out from the game
            — N, S, E, W, NE, NW, SE, SW:
                To move through the dungeon`);
    }

    const isInvalidInput = input => !!input && input.trim().length === 0;

    const mapToUserCommand = input => {
        while (input.includes(DOUBLE_SPACE)) {
            input = input.trim().replace(DOUBLE_SPACE, SINGLE_SPACE);
        }

        return input.split(SINGLE_SPACE);
    }

    const getCommand = input => {
        if (isInvalidInput(input)) {
            return invalidCommand(input);
        }

        const command = mapToUserCommand(input);
        const operation = command[0].toLowerCase();

        if (["h", "help"].includes(operation)) {
            return helpCommand();
        }

        if (["li", "login"].includes(operation)) {
            return loginCommand(command.slice(1));
        }

        if (["lo", "logout"].includes(operation)) {
            return logoutCommand(command.slice(1));
        }

        if (["n", "s", "e", "w", "ne", "nw", "se", "sw"].includes(operation)) {
            return moveCommand(operation.toUpperCase());
        }

        clearInput();
        messageContext.warning(`Invalid command: "${input}"`);

        return ignoreCommand();
    }

    return {
        getCommand
    }

}

export {
    init
};
