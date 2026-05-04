export default function WsConnection(setMessages) {
    return () => {
        const protocol = "ws";
        const domain = "localhost"
        const port = "8085";
        const endpoint = "ws-server/ws-endpoint"
        const credentials = "user1:password1";
        const token = btoa(credentials);

        const wsConnUrl = `${protocol}://${domain}:${port}/${endpoint}?auth=${token}`
        // const wsConnUrl = `${protocol}://${credentials}@${domain}:${port}/${endpoint}`
        console.log(`Connection string: ${wsConnUrl}`);

        const socket = new WebSocket(wsConnUrl);

        socket.onopen = () => {
            console.log("Connected to WebSocket server");
            socket.send("Hello from React!");
        };

        socket.onmessage = (event) => {
            setMessages(prev => [...prev, event.data]);
        };

        socket.onerror = (error) => {
            console.error("WebSocket error:", error);
        };

        socket.onclose = () => {
            console.log("WebSocket connection closed");
        };

        // Cleanup when component unmounts
        return () => socket.close();
    };
}