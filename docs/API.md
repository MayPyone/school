# API Endpoints

Base URL:

```text
http://localhost:8080
```

All routes use JSON request and response bodies unless noted otherwise.

## Notes About Current Behavior

- The app does not define global exception handling. Service errors such as
  missing records or duplicate emails are thrown as runtime exceptions and will
  usually be returned by Spring as `500 Internal Server Error`.
- There is no authentication or authorization middleware.
- Passwords are stored and returned as plain text by the current code.
- Request records do not use Bean Validation annotations, so required fields are
  mostly enforced by database constraints or by service lookups.
- Some DTO fields exist but are not applied by the current service methods. Those
  fields are called out below.

## Users

### Register User

```http
POST /api/v1/users
```

Creates a user.

Request body:

```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com",
  "role": "ADMIN",
  "password": "secret"
}
```

Fields:

| Field | Type | Notes |
| --- | --- | --- |
| `firstName` | string | Required by database schema. |
| `lastName` | string | Required by database schema. |
| `email` | string | Must be unique. |
| `role` | string | One of `ADMIN`, `TEACHER`, `ASSISTANT`. |
| `password` | string | Stored as plain text in current implementation. |

Successful response: `201 Created`

Response body is the persisted `User` entity, including `id`, `createdAt`,
`updatedAt`, and `password`.

### Login User

```http
POST /api/v1/users/login
```

Validates email and password.

Request body:

```json
{
  "email": "ada@example.com",
  "password": "secret"
}
```

Successful response: `202 Accepted`

Response body is the matching `User` entity, including the password.

## Schools

### Create School

```http
POST /api/v1/schools
```

Creates a school owned by an existing user.

Request body:

```json
{
  "userId": "00000000-0000-0000-0000-000000000001",
  "schoolName": "Spring Language School",
  "schoolEmail": "info@spring-school.example",
  "schoolAddress": ["123 Main Street", "Suite 4"],
  "logoUrl": "https://example.com/logo.png",
  "phoneNumbers": ["+1-555-0100"],
  "description": "Language school for adult learners.",
  "subTitle": "Practical classes for every level",
  "openingHours": [
    {
      "dayOfWeek": "MONDAY",
      "openTime": "09:00:00",
      "closeTime": "17:00:00"
    }
  ]
}
```

Fields:

| Field | Type | Notes |
| --- | --- | --- |
| `userId` | UUID | Required. Must identify an existing user. |
| `schoolName` | string | Required by database schema. |
| `schoolEmail` | string | Unique if provided. |
| `schoolAddress` | string array | Stored as a PostgreSQL text array. |
| `logoUrl` | string | Optional. |
| `phoneNumbers` | string array | Stored as a PostgreSQL text array. |
| `description` | string | Optional. |
| `subTitle` | string | Optional. |
| `openingHours` | array | Accepted by DTO but not saved by current service code. |

Successful response: `201 Created`

Example response:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "schoolName": "Spring Language School",
  "schoolEmail": "info@spring-school.example",
  "schoolAddress": ["123 Main Street", "Suite 4"],
  "logoUrl": "https://example.com/logo.png",
  "phoneNumbers": ["+1-555-0100"],
  "description": "Language school for adult learners.",
  "subTitle": "Practical classes for every level"
}
```

### List Schools

```http
GET /api/v1/schools
```

Returns all schools.

Successful response: `200 OK`

Response body is a list of `School` entities, not `SchoolResponse` DTOs.

### Update School

```http
PUT /api/v1/schools/{id}
```

Updates selected fields on a school.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `id` | UUID | Existing school id. |

Request body:

```json
{
  "schoolName": "Updated School Name",
  "schoolEmail": "new-email@example.com",
  "schoolAddress": ["456 New Street"],
  "logoUrl": "https://example.com/new-logo.png",
  "phoneNumbers": ["+1-555-0199"],
  "description": "Updated description.",
  "subTitle": "Updated subtitle",
  "openingHours": []
}
```

Applied fields: `schoolName`, `schoolAddress`, `description`, `phoneNumbers`,
`logoUrl`, and `subTitle`.

Not currently applied: `schoolEmail` and `openingHours`.

Successful response: `200 OK`

Response body is `SchoolResponse`.

### Delete School

```http
DELETE /api/v1/schools/{id}
```

Deletes a school.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `id` | UUID | Existing school id. |

Successful response: `200 OK` with an empty body.

Deleting a school cascades to dependent records according to database foreign
keys.

## Lessons

Lessons are nested under schools for listing and creation.

### List Lessons For School

```http
GET /api/v1/schools/{schoolId}/lessons
```

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `schoolId` | UUID | Existing school id. |

Successful response: `200 OK`

Example response:

```json
[
  {
    "lessonId": "22222222-2222-2222-2222-222222222222",
    "schoolId": "11111111-1111-1111-1111-111111111111",
    "title": "Present Simple",
    "level": "Basic",
    "content": "Lesson overview",
    "category": "GRAMMAR",
    "createdBy": "AdaLovelace"
  }
]
```

### Get Lesson With Units

```http
GET /api/v1/schools/{schoolId}/lessons/{lessonId}
```

Returns one lesson plus its units.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `schoolId` | UUID | Present in the URL but not used by current service code. |
| `lessonId` | UUID | Existing lesson id. |

Successful response: `200 OK`

Example response:

```json
{
  "lesson": {
    "lessonId": "22222222-2222-2222-2222-222222222222",
    "schoolId": "11111111-1111-1111-1111-111111111111",
    "title": "Present Simple",
    "level": "Basic",
    "content": "Lesson overview",
    "category": "GRAMMAR",
    "createdBy": "AdaLovelace"
  },
  "units": [
    {
      "id": "33333333-3333-3333-3333-333333333333",
      "title": "Form",
      "content": "Subject plus base verb.",
      "videoUrl": "https://example.com/video",
      "lessonId": "22222222-2222-2222-2222-222222222222",
      "createdById": "00000000-0000-0000-0000-000000000001"
    }
  ]
}
```

### Create Lesson

```http
POST /api/v1/schools/{schoolId}/lessons
```

Creates a lesson for a school.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `schoolId` | UUID | Existing school id. This is the school used by the service. |

Request body:

```json
{
  "schoolId": "11111111-1111-1111-1111-111111111111",
  "title": "Present Simple",
  "levelId": 1,
  "content": "Lesson overview",
  "category": "GRAMMAR",
  "userId": "00000000-0000-0000-0000-000000000001"
}
```

Fields:

| Field | Type | Notes |
| --- | --- | --- |
| `schoolId` | UUID | Accepted by DTO but ignored; path `schoolId` is used. |
| `title` | string | Required by database schema. |
| `levelId` | number | Required. Existing seeded values are `1`, `2`, and `3`. |
| `content` | string | Required by entity mapping; migration allows null. |
| `category` | string | Database allows `GRAMMAR`, `VOCAB`, `PRACTICE`, `GENERAL`. |
| `userId` | UUID | Required. Must identify an existing user. |

Successful response: `200 OK`

Response body is `LessonResponse`.

### Update Lesson

```http
PUT /api/v1/schools/{schoolId}/lessons/{lessonId}
```

Updates a lesson.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `schoolId` | UUID | Present in the URL but not used by current service code. |
| `lessonId` | UUID | Existing lesson id. |

Request body:

```json
{
  "title": "Present Simple Updated",
  "levelId": 2,
  "content": "Updated lesson overview",
  "category": "PRACTICE"
}
```

Fields:

| Field | Type | Notes |
| --- | --- | --- |
| `title` | string | Optional update field. |
| `levelId` | number | Required by current service code because it is looked up before null checks. |
| `content` | string | Optional update field. |
| `category` | string | Optional update field; database restricts values. |

Successful response: `200 OK`

Response body is `LessonResponse`.

### Delete Lesson

```http
DELETE /api/v1/schools/{schoolId}/lessons/{lessonId}
```

Deletes a lesson.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `schoolId` | UUID | Present in the URL but not used by current service code. |
| `lessonId` | UUID | Existing lesson id. |

Successful response: `204 No Content`

## Units

Units are nested under lessons in the URL.

### List Units For Lesson

```http
GET /api/v1/lessons/{lessonId}/units
```

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `lessonId` | UUID | Existing lesson id. |

Successful response: `200 OK`

Response body is a list of `UnitResponse` objects.

### Get Unit

```http
GET /api/v1/lessons/{lessonId}/units/{unitId}
```

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `lessonId` | UUID | Present in the URL but not used by current service code. |
| `unitId` | UUID | Existing unit id. |

Successful response: `200 OK`

Example response:

```json
{
  "id": "33333333-3333-3333-3333-333333333333",
  "title": "Form",
  "content": "Subject plus base verb.",
  "videoUrl": "https://example.com/video",
  "lessonId": "22222222-2222-2222-2222-222222222222",
  "createdById": "00000000-0000-0000-0000-000000000001"
}
```

### Create Unit

```http
POST /api/v1/lessons/{lessonId}/units
```

Creates a lesson unit.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `lessonId` | UUID | Present in the URL but not used by current service code. |

Request body:

```json
{
  "title": "Form",
  "content": "Subject plus base verb.",
  "videoUrl": "https://example.com/video",
  "lessonId": "22222222-2222-2222-2222-222222222222",
  "createdById": "00000000-0000-0000-0000-000000000001"
}
```

Fields:

| Field | Type | Notes |
| --- | --- | --- |
| `title` | string | Required by database schema. |
| `content` | string | Required by database schema. |
| `videoUrl` | string | Optional. |
| `lessonId` | UUID | Required. This body value is used instead of the path parameter. |
| `createdById` | UUID | Required. Must identify an existing user. |

Successful response: `200 OK`

Response body is `UnitResponse`.

### Update Unit

```http
PUT /api/v1/lessons/{lessonId}/units/{unitId}
```

Updates a unit.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `lessonId` | UUID | Present in the URL but body `lessonId` is used. |
| `unitId` | UUID | Present in the URL but body `id` is used. |

Request body:

```json
{
  "id": "33333333-3333-3333-3333-333333333333",
  "title": "Form Updated",
  "content": "Updated unit content.",
  "videoUrl": "https://example.com/new-video",
  "lessonId": "22222222-2222-2222-2222-222222222222",
  "createdById": "00000000-0000-0000-0000-000000000001"
}
```

Fields:

| Field | Type | Notes |
| --- | --- | --- |
| `id` | UUID | Required by current service code. Identifies the unit to update. |
| `title` | string | Optional update field. |
| `content` | string | Optional update field. |
| `videoUrl` | string | Optional update field. |
| `lessonId` | UUID | Required by current service code. Reassigns the unit lesson. |
| `createdById` | UUID | Required by current service code, but the loaded user is not applied. |

Successful response: `200 OK`

Response body is `UnitResponse`.

### Delete Unit

```http
DELETE /api/v1/lessons/{lessonId}/units/{unitId}
```

Deletes a unit.

Path parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `lessonId` | UUID | Present in the URL but not used by current service code. |
| `unitId` | UUID | Unit id to delete. |

Successful response: `204 No Content`

The current service calls `deleteById` directly, so deleting a non-existent unit
may still return success depending on Spring Data JPA behavior.

