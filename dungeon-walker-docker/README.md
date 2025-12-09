# Container Environments for Dungeon Walker

## Kafka

If you run [build-n-run.sh](build-n-run.sh) to start the containers, no topic creation may be needed. If not, after the 
container has started, run the following command:
```shell
docker exec -i kafka opt/kafka/bin/kafka-topics.sh \
--create \
--if-not-exists \
--bootstrap-server kafka:29092 \
--replication-factor 1 \
--partitions 1 \
--topic game-engine-consumer-topic
```

### Using IJ's Kafka Plugin

1. `View -> Tool Windows -> Kafka`
2. `+` (New Connection)
3. Configuration source: `Custo`
4. Bootstrap servers: `127.0.0.1:9092`
5. Authentication: `None`
6. Schema Registry (Optional):
   - Type:`None`

#### IJ's Kafka Plugin Producer

- Topic: `game-engine-consumer-topic`
- Key:
  - Type: `String`
  - Value: `user-id`
- Value:
  - Type: `Protobuf (Custom)`
  - Schema: `Explicit`
```protobuf
syntax = "proto3";

package contract.client;

message ClientRequest {
  string client_id = 1;
  oneof data {
    Connection connection = 2;
    Movement movement = 3;
  }
}

message Connection {}

message Movement {
  Direction direction = 1;
}

enum Direction {
  N = 0;
  E = 1;
  S = 2;
  W = 3;
  NE = 4;
  SE = 5;
  SW = 6;
  NW = 7;
}
```
  - Payloads:
```json
{
  "client_id": "user-id",
  "connection": {}
}
```
```json
{
  "client_id": "user-id",
  "movement": {
    "direction": "E"
  }
}
```

#### IJ's Kafka Plugin Consumer

- Topic: `game-engine-consumer-topic`
- Key:
    - Type: `String`
- Value:
    - Type: `Protobuf (Custom)`
    - Schema: `Explicit`
```protobuf
syntax = "proto3";

package contract.client;

message ClientRequest {
  string client_id = 1;
  oneof data {
    Connection connection = 2;
    Movement movement = 3;
  }
}

message Connection {}

message Movement {
  Direction direction = 1;
}

enum Direction {
  N = 0;
  E = 1;
  S = 2;
  W = 3;
  NE = 4;
  SE = 5;
  SW = 6;
  NW = 7;
}
```
- Range and Filters
  - Start from: Latests
  - Limit: None
  - Filter: None

## PostgreSQL

The schema in the [create_tables_postgres.sql](ddl-scripts/create_tables_postgres.sql) file should be run when the 
PostgreSQL container is started for the first time. If for some reason the schema is not created, run the following 
command in the `dungeon-walker-docker` root folder:
```shell
docker exec -i postgres-db psql -U postgres -t < ddl-scripts/create_tables_postgres.sql
```
