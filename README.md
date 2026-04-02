# TiDB Index Bug Reproduction

This repository is a close-to-minimal reproduction case for a TiDB bug related to index usage during paginated queries.

## Overview

The bug manifests as a **flaky test**: the same SQL query, run against the same data, returns unexpected (incorrect) results on approximately **10% of runs** when using TiDB. The same test passes **100% of the time** when run against MySQL 8.0 with an identical schema and dataset.

The test (`shouldCombineAllThreeFiltersSearchStatusAndPagination`) inserts 10 rows into a `space` table and then performs three paginated queries using a keyset/cursor-based pagination pattern. The query filters by `workspace_id`, `status`, and a `LIKE` on `LOWER(name)`, ordered by `LOWER(name) ASC, id ASC`.

On some runs, the second or third page of results returns an incorrect number of rows (e.g. 0 or 1 instead of 2), indicating the index is returning inconsistent results between queries within the same logical dataset.

## Space Table Schema

```sql
CREATE TABLE IF NOT EXISTS `space` (
    `workspace_id` BINARY(16) NOT NULL,
    `tenant_id`    VARCHAR(100) NOT NULL,
    `space_id`     BINARY(16) NOT NULL,
    `name`         VARCHAR(200) NOT NULL,
    `description`  VARCHAR(500),
    `created_at`   DATETIME(6) NOT NULL,
    `updated_at`   DATETIME(6),
    `created_by`   VARCHAR(128) NOT NULL,
    `updated_by`   VARCHAR(128),
    `status`       ENUM('CURRENT', 'ARCHIVED', 'TRASHED') NOT NULL,
    `is_default`   BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`workspace_id`, `space_id`)
);

CREATE INDEX `space_default_idx`
    ON `space`(`is_default`);

CREATE INDEX `space_workspace_id_lower_name_id_idx`
    ON `space`(`workspace_id`, (LOWER(`name`)), `space_id`);

CREATE INDEX `space_workspace_id_status_id_idx`
    ON `space`(`workspace_id`, `status`, `space_id`);

CREATE INDEX `space_workspace_id_status_lower_name_id_idx`
    ON `space`(`workspace_id`, `status`, (LOWER(`name`)), `space_id`);
```

## What the Test Does

The test is a `@RepeatedTest(50)` — it runs the same scenario 50 times in a single test run. Each repetition:

1. Creates a fresh `workspace_id` (random UUID)
2. Inserts 10 spaces into the table:
   - 5 spaces named `"Backend Team 0..4"` with status `CURRENT`
   - 3 spaces named `"Backend Archive 0..2"` with status `ARCHIVED`
   - 2 spaces named `"Frontend Team 0..1"` with status `CURRENT`
3. Queries for spaces matching `name LIKE '%backend%'` AND `status = 'CURRENT'`, paginated with limit 2
4. Asserts:
   - Page 1: 2 results, `hasNext = true`
   - Page 2: 2 results, `hasNext = true`
   - Page 3: 1 result, `hasNext = false`

The pagination uses keyset cursors: each subsequent page adds a `WHERE (LOWER(name) > ?) OR (LOWER(name) = ? AND id > ?)` clause.

**Expected**: 5 `CURRENT` spaces match `LIKE '%backend%'` → 3 pages of [2, 2, 1].

**Actual (on failure)**: Some pages return fewer results than expected, suggesting the index is not returning all matching rows consistently.

## Repository Structure

```
├── src/
│   ├── main/kotlin/        # Application source — Spring Boot app (minimal, kept for context)
│   └── test/kotlin/        # Integration test
│       └── .../spaces/repository/SpacesRepositoryIT.kt  ← The flaky test
├── test-infrastructure/
│   └── docker/
│       ├── compose.dependencies-only-tidb.yml   # TiDB container (port 4000)
│       └── compose.dependencies-only-mysql.yml  # MySQL container (port 4000)
├── src/test/resources/
│   └── nebulae-integration-test.properties      # Database connection config (localhost:4000)
├── start-docker-tidb.sh    # Start/stop TiDB container
├── start-docker-mysql.sh   # Start/stop MySQL container
└── run-tests.sh            # Run the flaky test
```

## Prerequisites

- Java 21
- Docker
- Internet access to Maven Central (for standard public dependencies)

## Reproduction Steps (TiDB — bug present)

```bash
# 1. Start TiDB
./start-docker-tidb.sh

# 2. Run the test 50 times (expect ~10% failure rate)
./run-tests.sh

# 3. Stop TiDB
./start-docker-tidb.sh stop
```

You should see some repetitions fail with one of the following assertion errors:

**Failure type 1** — wrong number of results returned on a page:
```
java.lang.AssertionError:
Expected size: 2 but was: 1 in:
[Space(workspaceId=9397e751-58f9-4709-81f9-e6c73a8383f3, spaceId=7dcc7ed9-83ee-472c-8f02-8e9a878c705f, name=Backend Team 4, description=Example Description (iVfWY), createdBy=8f61d976b6ad789b6fcb1a58, updatedBy=null, createdAt=2026-03-30T08:40:00.698346Z, updatedAt=null, status=CURRENT, default=false)]
	at io.atlassian.micros.studio.application.spaces.repository.SpacesRepositoryIT$GetSpacesIT$CombinedFiltersIT.shouldCombineAllThreeFiltersSearchStatusAndPagination(SpacesRepositoryIT.kt:122)
```

**Failure type 2** — `hasNext` incorrectly returns `false` when more pages exist:
```
org.opentest4j.AssertionFailedError:
Expecting value to be true but was false
	at io.atlassian.micros.studio.application.spaces.repository.SpacesRepositoryIT$GetSpacesIT$CombinedFiltersIT.shouldCombineAllThreeFiltersSearchStatusAndPagination(SpacesRepositoryIT.kt:123)
```

Failures occur on approximately 10% of the 50 repetitions, non-deterministically.

## Comparison Steps (MySQL — bug NOT present)

To confirm this is a TiDB-specific issue, swap out TiDB for MySQL:

```bash
# 1. Start MySQL instead of TiDB
./start-docker-mysql.sh

# 2. Run the exact same test
./run-tests.sh

# 3. Stop MySQL
./start-docker-mysql.sh stop
```

All 50 repetitions should pass consistently with MySQL.

## ORM & Table Definition

This project uses [Exposed](https://github.com/JetBrains/Exposed) (JetBrains' Kotlin ORM) to interact with the database. Tables are defined as Kotlin objects extending `Table`, and the schema is created at test startup using `SchemaUtils.create(SpaceTable)`.

The `SpaceTable` object defines all columns and indexes:

```kotlin
object SpaceTable : BaseTable("space") {
    val id = javaUUID("id")
    val name = varchar("name", 200)
    val description = varchar("description", 500).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at").nullable()
    val createdBy = varchar("created_by", 128)
    val updatedBy = varchar("updated_by", 128).nullable()
    val status = nativeEnum<SpaceStatus>("status")
    val default = bool("is_default").default(false).index(customIndexName = "space_default_idx")

    override val primaryKey = PrimaryKey(arrayOf(workspaceId, id))

    init {
        index(
            customIndexName = "space_workspace_id_lower_name_id_idx",
            functions = listOf(workspaceId, name.lowerCase(), id),
        )
        index(customIndexName = "space_workspace_id_status_id_idx", columns = arrayOf(workspaceId, status, id))
        index(
            customIndexName = "space_workspace_id_status_lower_name_id_idx",
            functions = listOf(workspaceId, status, name.lowerCase(), id),
        )
    }
}
```

Note that `workspaceId` is inherited from `BaseTable` and is of type `BINARY(16)`.

## Investigation Findings

Through testing different index configurations, the following behaviour has been observed:

| Index configuration | Flakiness |
|---|---|
| All indexes present (as above) | ~10% failure rate |
| `space_workspace_id_lower_name_id_idx` removed | 0% — tests pass 100% |
| `space_workspace_id_status_lower_name_id_idx` removed | 0% — tests pass 100% |
| `space_workspace_id_status_id_idx` removed (other two kept) | Still flaky |
| Index declaration order changed | Flakiness rate changes (e.g. ~3% vs ~10%) |

**Key observations:**
- The bug only appears when **both** functional indexes involving `LOWER(name)` are present together
- Removing **either** of the `LOWER(name)` indexes causes the bug to disappear entirely
- Removing only the non-functional index (`space_workspace_id_status_id_idx`) does **not** fix the bug
- Changing the **order** in which the indexes are declared affects the flakiness rate
- This strongly suggests the bug is related to how TiDB handles **multiple functional indexes on the same expression** (`LOWER(name)`) within the same table, or how it chooses between them during query planning

## Notes

- The `start-docker-tidb.sh` and `start-docker-mysql.sh` scripts both expose the database on port `4000`, so no configuration changes are needed when switching between them.
- The TiDB version used is `pingcap/tidb:v8.5.5` (matching the production version).
- The MySQL version used is `mysql:8.0`.
- The schema is created fresh at the start of the test class (`@BeforeAll`) and rows are inserted per repetition using a unique `workspace_id`, so there is no cross-repetition data contamination.
