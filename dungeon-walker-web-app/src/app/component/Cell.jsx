import {useState} from 'react'
import {stateExporter} from "../service/cell-manager.js";

function Cell({x, y}) {
    console.log("Rendering Cell");

    const [value, setValue] = useState(0);
    stateExporter(x, y)(setValue);

    return (
        <td>
            {value || `(${x},${y})`}
        </td>
    )
}

export default Cell;