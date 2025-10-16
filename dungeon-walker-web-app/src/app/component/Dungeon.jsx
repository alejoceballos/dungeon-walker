import Row from "./Row.jsx";

function Dungeon({width, height}) {
    console.log("Rendering Dungeon");
    return (
        <table className="dungeon-grid">
            <thead>
            <tr>
                <th></th>
                {[...Array(width)].map((_, x) => <th key={thKey(x)}>{x}</th>)}
            </tr>
            </thead>
            <tbody>
            {[...Array(height)].map((_, y) => <Row key={rowKey(y)} y={y} width={width}/>)}
            </tbody>
        </table>
    )
}

function thKey(x) {
    return `th-${x}`;
}

function rowKey(y) {
    return `row-${y}`;
}

export default Dungeon;