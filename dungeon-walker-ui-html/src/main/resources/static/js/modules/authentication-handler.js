const init = (protocol, host, endpoint) => {
    const url = protocol + "://" + host + endpoint;

    let loggedUser;

    const getUsername = () => loggedUser;

    const clearUsername = () => loggedUser = undefined;

    const requestToken = async (username, password) => {
        const credentials = {
            username: username,
            password: password
        }

        try {
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(credentials)
            });

            const message = await response.json();

            if (!response.ok) {
                return {
                    status: "ERROR",
                    message: !!message.errorMessage ? message.errorMessage : "Unknown error"
                };
            }

            loggedUser = credentials.username;

            return {
                status: "SUCCESS",
                token: message.token
            };

        } catch ({message}) {
            return {
                status: "ERROR",
                message: message
            };
        }
    }

    return {
        requestToken,
        getUsername,
        clearUsername
    }

}

export {
    init
}