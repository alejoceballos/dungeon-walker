import Cell from "./Cell.jsx";
import {xyKey} from "../service/cell-manager.js";

function Row({y, width}) {
    console.log("Rendering Row");
    return (
        <tr>
            <td>{y}</td>
            {[...Array(width)].map((_, x) => <Cell key={cellKey(x, y)} x={x} y={y}/>)}
        </tr>
    )
}

function cellKey(x, y) {
    return `cell-${xyKey(x, y)}`;
}

export default Row;