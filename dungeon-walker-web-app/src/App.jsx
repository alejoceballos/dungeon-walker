import './App.css'
import {initializeCells, setCellValue} from "./app/service/cell-manager.js";
import Dungeon from "./app/component/Dungeon.jsx";

const height = 10;
const width = 10;

const socket = new WebSocket("ws://localhost:8080/ws-endpoint")

// Connection opened
socket.addEventListener("open", event => {
    socket.send("Connection established")
});

// Listen for messages
socket.addEventListener("message", event => {
    console.log("Message from server ", event.data)
});

function App() {
    console.log("Rendering App");

    initializeCells(width, height);

    let x;
    let y;

    return (
        <>
            <button onClick={() => setCellValue(x, y)}>Change Cell</button>
            <input type="text" placeholder="X Coordinate" onChange={e => x = e.target.value}/>
            <input type="text" placeholder="Y Coordinate" onChange={e => y = e.target.value}/>
            <Dungeon width={width} height={height}/>
        </>
    )
}

export default App
