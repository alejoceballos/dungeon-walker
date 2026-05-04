import {useEffect, useState} from "react";
import WsConnection from "./WsConnection.jsx";

export default function DungeonMap() {
    const [messages, setMessages] = useState([]);
    useEffect(WsConnection(setMessages), []);

    return <>
        <div className="map">
            <h2>Messages from server:</h2>
            <ul>
                {messages.map((m, i) => (
                    <li key={i}>{m}</li>
                ))}
            </ul>
        </div>
    </>
}