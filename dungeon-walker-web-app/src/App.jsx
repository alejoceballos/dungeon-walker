import './App.css'
import {setCellValue} from "./app/service/cell-manager.js";
import {initializeWebSocket} from "./app/service/ws-manager.js";
import Dungeon from "./app/component/Dungeon.jsx";
import {useState} from "react";

function App() {
    console.log("Rendering App");

    const [mapSize, setMapSize] = useState({width: 0, height: 0});

    let x;
    let y;

    initializeWebSocket(mapSize, setMapSize);

    return (
        <>
            <button onClick={() => setCellValue(x, y)}>Change Cell</button>
            <input type="text" placeholder="X Coordinate" onChange={e => x = e.target.value}/>
            <input type="text" placeholder="Y Coordinate" onChange={e => y = e.target.value}/>
            <Dungeon width={mapSize.width} height={mapSize.height}/>
        </>
    )
}

export default App
