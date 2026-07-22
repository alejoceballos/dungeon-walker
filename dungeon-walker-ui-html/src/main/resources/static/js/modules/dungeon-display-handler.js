const init = dungeonHandler => {

    const removeFromArray = (array, items) => {
        for (const item of items) {
            const index = array.indexOf(item);

            if (index > -1) {
                array.splice(index, 1);
            }
        }
    };

    const ICON = {
        WALL: "🧱",
        WALKER: "️🧍",
        WALKER_RIGHT: "🚶‍➡️",
        WALKER_LEFT: "🚶"
    };

    const DIRECTION = {
        STOPPED: "⏹️",
        NORTH: "⬆️",
        SOUTH: "⬇️",
        EAST: "➡️",
        WEST: "⬅️",
        NORTH_EAST: "↗️",
        NORTH_WEST: "↖️",
        SOUTH_EAST: "↘️",
        SOUTH_WEST: "↙️"
    }

    const walkerDirection = (fromCoords, toCoords) => {
        const firstAppearance = !fromCoords;

        if (firstAppearance) {
            return DIRECTION.STOPPED;
        }

        const noChange = fromCoords.x === toCoords.x && fromCoords.y === toCoords.y;
        if (noChange) {
            return DIRECTION.STOPPED;
        }

        let directions = [
            DIRECTION.NORTH,
            DIRECTION.SOUTH,
            DIRECTION.EAST,
            DIRECTION.WEST,
            DIRECTION.NORTH_EAST,
            DIRECTION.NORTH_WEST,
            DIRECTION.SOUTH_EAST,
            DIRECTION.SOUTH_WEST];

        if (toCoords.x > fromCoords.x) {
            removeFromArray(directions, [
                DIRECTION.NORTH_WEST,
                DIRECTION.SOUTH_WEST,
                DIRECTION.WEST,
                DIRECTION.NORTH,
                DIRECTION.SOUTH]);
        } else if (toCoords.x < fromCoords.x) {
            removeFromArray(directions, [
                DIRECTION.NORTH_EAST,
                DIRECTION.SOUTH_EAST,
                DIRECTION.EAST,
                DIRECTION.NORTH,
                DIRECTION.SOUTH]);
        } else {
            removeFromArray(directions, [
                DIRECTION.NORTH_EAST,
                DIRECTION.SOUTH_EAST,
                DIRECTION.NORTH_WEST,
                DIRECTION.SOUTH_WEST,
                DIRECTION.WEST,
                DIRECTION.EAST]);
        }

        if (toCoords.y > fromCoords.y) {
            removeFromArray(directions, [
                DIRECTION.NORTH_WEST,
                DIRECTION.NORTH,
                DIRECTION.NORTH_EAST]);
        } else if (toCoords.y < fromCoords.y) {
            removeFromArray(directions, [
                DIRECTION.SOUTH_WEST,
                DIRECTION.SOUTH,
                DIRECTION.SOUTH_EAST]);
        } else {
            removeFromArray(directions, [
                DIRECTION.NORTH_EAST,
                DIRECTION.SOUTH_EAST,
                DIRECTION.NORTH_WEST,
                DIRECTION.SOUTH_WEST,
                DIRECTION.NORTH,
                DIRECTION.SOUTH]);
        }

        return directions[0];
    }

    const userWalkerIcon = direction => {
        if ([DIRECTION.NORTH, DIRECTION.SOUTH, DIRECTION.STOPPED].includes(direction)) {
            return ICON.WALKER;
        }

        if ([DIRECTION.NORTH_EAST, DIRECTION.SOUTH_EAST, DIRECTION.EAST].includes(direction)) {
            return ICON.WALKER_RIGHT;
        }

        if ([DIRECTION.NORTH_WEST, DIRECTION.SOUTH_WEST, DIRECTION.WEST].includes(direction)) {
            return ICON.WALKER_LEFT;
        }
    }

    const icon = (thingId, direction) => {
        if (!thingId || thingId.trim().length === 0) {
            return "";
        }

        if (thingId.length === 5 && thingId.startsWith("W-")) {
            return ICON.WALL;
        }

        return userWalkerIcon(direction);
    }

    let dungeonData;

    const create = data => {
        dungeonData = data;

        for (let y = 0; y < dungeonData.height; y++) {
            dungeonHandler.addRow(y);

            for (let x = 0; x < dungeonData.width; x++) {
                dungeonHandler.addCell(x, y)
            }
        }
    };

    const update = dungeonOrCellState => {
        if (dungeonOrCellState.height && dungeonOrCellState.width && dungeonOrCellState.coordinates) {
            updateDungeonState(dungeonOrCellState.coordinates);

        } else if (dungeonOrCellState.coordinates.x && dungeonOrCellState.coordinates.y) {
            updateCellState(dungeonOrCellState);
        }
    }

    const updateDungeonState = coordinates => {
        for (const thingId in coordinates) {
            const currentCoords = getCoordinates(thingId);
            const newCoords = coordinates[thingId];

            if (currentCoords) {
                dungeonHandler.removeThing(currentCoords.x, currentCoords.y);
            }

            if (newCoords) {
                const direction = walkerDirection(currentCoords, newCoords);

                dungeonHandler.addThing(
                    icon(thingId, direction),
                    newCoords.x,
                    newCoords.y);
            }
        }

        dungeonData.coordinates = {...dungeonData.coordinates, ...coordinates};
    };

    const updateCellState = data => {
        if (!data.id) {
            return removeFromDungeonByCoordinates(data.coordinates.x, data.coordinates.y);
        }

        const currentCoords = getCoordinates(data.id);

        if (currentCoords) {
            dungeonHandler.removeThing(currentCoords.x, currentCoords.y);
        }

        const direction = walkerDirection(currentCoords, data.coordinates);

        dungeonHandler.addThing(
            icon(data.id, direction),
            data.coordinates.x,
            data.coordinates.y);

        updateCoordinates(data.id, data.coordinates);
    }

    const removeFromDungeonById = key => {
        const coords = getCoordinates(key);
        dungeonData.coordinates[key] = undefined;
        dungeonHandler.removeThing(coords.x, coords.y);
    }

    const removeFromDungeonByCoordinates = (x, y) => {
        for (const [key, coords] of Object.entries(dungeonData.coordinates)) {
            if (coords.x === x && coords.y === y) {
                dungeonData.coordinates[key] = undefined;
                dungeonHandler.removeThing(coords.x, coords.y);
                return;
            }
        }
    }

    const updateCoordinates = (thingId, coordinates) => dungeonData.coordinates[thingId] = coordinates;

    const getCoordinates = thingId => dungeonData.coordinates[thingId];

    const isCreated = () => !!dungeonData;

    return {
        create,
        update,
        isDungeonCreated: isCreated,
        removeFromDungeon: removeFromDungeonById
    }

};

export {
    init
}
