const cellContentSetters = {};
const cellsStateExporters = {};

function initializeCells(width, height) {
    [...Array(height)].map((_, y) =>
        [...Array(width)].map((_, x) => {
                cellsStateExporters[xyKey(x, y)] = function (setValue) {
                    cellContentSetters[xyKey(x, y)] = setValue;
                }
            }
        )
    );
}

function stateExporter(x, y) {
    return cellsStateExporters[xyKey(x, y)];
}

function xyKey(x, y) {
    return `${x}-${y}`;
}

function setCellValue(x, y) {
    cellContentSetters[xyKey(x, y)]("X");
}

export {
    initializeCells,
    stateExporter,
    setCellValue,
    xyKey
};