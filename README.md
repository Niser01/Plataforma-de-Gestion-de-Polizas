# API de Gestión de Pólizas

API REST desarrollada con Spring Boot para la gestión de pólizas y riesgos de seguros.

---

## Tecnologías

- Java 21
- Spring Boot 3.x
- H2 (base de datos en memoria)
- Maven
- Docker / Docker Compose

---

## Estructura del proyecto

```
src/main/java/
- controller/
    -- ApiController.java
- dto/
    -- AddRiesgoDTO.java
    -- CoreMockDTO.java
- entities/
    -- Poliza.java
    -- Riesgo.java
- repositories/
    -- PolizaRepository.java
    -- RiesgoRepository.java
- security/
    -- ApiKeyFilter.java
- service/
    -- ApiService.java

src/main/resources/
- application.properties
- data.sql
```

---

## Requisitos previos

### Opción A – Docker (recomendada, no requiere Java ni Maven)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Opción B – Maven local
- Java 21+
- Maven 3.9+

---

## Instalación y ejecución

### Opción A – Docker Compose **Recomendada**

1. Clonar el repositorio:
```bash
git clone https://github.com/Niser01/Plataforma-de-Gestion-de-Polizas.git
cd Plataforma-de-Gestion-de-Polizas
```
 
2. Levantar el contenedor:
```bash
docker-compose up --build
```
 
3. La API estará disponible en:
```
http://localhost:8080
```
 
> El archivo `.env` ya viene incluido en el repositorio con los valores por defecto. Si se requiere modificar las variables de entorno, editar ese archivo antes de ejecutar el paso anterior.
 
Para detener el contenedor:
```bash
docker-compose down
```
 
---
 
### Opción B – Maven local
 
1. Clonar el repositorio:
```bash
git clone https://github.com/Niser01/Plataforma-de-Gestion-de-Polizas.git
cd Plataforma-de-Gestion-de-Polizas
```
 
2. Compilar y ejecutar:
```bash
mvn spring-boot:run
```
 
3. La API estará disponible en:
```
http://localhost:8080
```
 
---

## Variables de entorno

> **Nota importante:** El archivo `.env` se incluye en este repositorio **únicamente para facilitar el despliegue y prueba del sistema**. En un entorno real (desarrollo, staging, producción) este archivo **no se incluiría en el repositorio** — las variables se configurarían directamente como variables de entorno del servidor o mediante un gestor de secretos (AWS Secrets Manager, Vault, etc.), y `.env` estaría en `.gitignore`.

El archivo `.env` en la raíz del proyecto contiene las siguientes variables, que pueden modificarse sin tocar el código:

```env
api.key.header=x-api-key
api.key.value=123456
```

| Variable         | Descripción                        | Valor por defecto |
|------------------|------------------------------------|-------------------|
| api.key.header   | Nombre del header de autenticación | `x-api-key`       |
| api.key.value    | Valor esperado del API Key         | `123456`          |

Un archivo `.env.example` está incluido en el repositorio como referencia de las variables requeridas.

---

## Seguridad

Todos los endpoints requieren el siguiente header en cada petición:

```
x-api-key: 123456
```

Sin este header la API retorna `401 Unauthorized`.

---

## Base de datos
 
La aplicación usa H2 (base de datos en memoria). Al iniciar el sistema se cargan automáticamente datos de prueba desde `src/main/resources/data.sql`.
 
---

## Datos de prueba precargados

| ID                                     | Tipo       | Estado    | Riesgos        |
|----------------------------------------|------------|-----------|----------------|
| a1b2c3d4-0000-0000-0000-000000000001   | INDIVIDUAL | ACTIVA    | 1 (ACTIVO)     |
| a1b2c3d4-0000-0000-0000-000000000002   | INDIVIDUAL | CANCELADA | 1 (CANCELADO)  |
| a1b2c3d4-0000-0000-0000-000000000003   | COLECTIVA  | ACTIVA    | 2 (ACTIVOS)    |
| a1b2c3d4-0000-0000-0000-000000000004   | COLECTIVA  | RENOVADA  | 0              |

---

## Endpoints
 
### GET /polizas
Lista pólizas filtradas por tipo y estado.
 
**Query params:**
| Parámetro | Valores aceptados                  | Requerido |
|-----------|------------------------------------|-----------|
| tipo      | `INDIVIDUAL`, `COLECTIVA`          | Sí        |
| estado    | `ACTIVA`, `CANCELADA`, `RENOVADA`  | Sí        |
 
**Ejemplo exitoso:**
```
GET http://localhost:8080/polizas?tipo=INDIVIDUAL&estado=ACTIVA
Headers: x-api-key: 123456
```
 
**Ejemplo con error — tipo inválido:**
```
GET http://localhost:8080/polizas?tipo=INVALIDO&estado=ACTIVA
Headers: x-api-key: 123456
```
 
---
 
### GET /polizas/{id}/riesgos
Lista los riesgos asociados a una póliza.
 
**Ejemplo exitoso:**
```
GET http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000003/riesgos
Headers: x-api-key: 123456
```
 
**Ejemplo con error — póliza no encontrada:**
```
GET http://localhost:8080/polizas/00000000-0000-0000-0000-000000000000/riesgos
Headers: x-api-key: 123456
```
 
---
 
### POST /polizas/{id}/renovar
Renueva una póliza incrementando el canon mensual y la prima según el IPC indicado.
 
**Reglas de negocio:**
- No se puede renovar una póliza con estado `CANCELADA`.
 
**Query param:**
| Parámetro | Descripción                        | Requerido |
|-----------|------------------------------------|-----------|
| ipc       | Porcentaje de incremento (ej: 5.5) | Sí        |
 
**Ejemplo exitoso:**
```
POST http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000001/renovar?ipc=5.5
Headers: x-api-key: 123456
```
 
**Ejemplo con error — póliza cancelada no se puede renovar:**
```
POST http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000002/renovar?ipc=5.5
Headers: x-api-key: 123456
```
 
---
 
### POST /polizas/{id}/cancelar
Cancela una póliza y todos sus riesgos asociados.
 
**Reglas de negocio:**
- No se puede cancelar una póliza que ya está `CANCELADA`.
- La cancelación de la póliza cancela automáticamente todos sus riesgos.
 
**Ejemplo exitoso:**
```
POST http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000001/cancelar
Headers: x-api-key: 123456
```
 
**Ejemplo con error — póliza ya cancelada:**
```
POST http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000002/cancelar
Headers: x-api-key: 123456
```
 
---
 
### POST /polizas/{id}/riesgos
Agrega un nuevo riesgo a una póliza.
 
**Reglas de negocio:**
- Solo pólizas de tipo `COLECTIVA` pueden agregar riesgos por este endpoint.
- No se pueden agregar riesgos a una póliza `CANCELADA`.
 
**Body (JSON):**
```json
{
  "tipoRiesgo": "SISMO",
  "descripcion": "Riesgo por actividad sísmica"
}
```
 
**Ejemplo exitoso:**
```
POST http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000003/riesgos
Headers: x-api-key: 123456
         Content-Type: application/json
```
 
**Ejemplo con error — póliza de tipo INDIVIDUAL:**
```
POST http://localhost:8080/polizas/a1b2c3d4-0000-0000-0000-000000000001/riesgos
Headers: x-api-key: 123456
         Content-Type: application/json
```
 
---
 
### POST /riesgos/{id}/cancelar
Cancela un riesgo específico.
 
**Reglas de negocio:**
- No se puede cancelar un riesgo que ya está `CANCELADO`.
 
**Ejemplo exitoso:**
```
POST http://localhost:8080/riesgos/b1b2c3d4-0000-0000-0000-000000000002/cancelar
Headers: x-api-key: 123456
```
 
**Ejemplo con error — riesgo ya cancelado:**
```
POST http://localhost:8080/riesgos/b1b2c3d4-0000-0000-0000-000000000004/cancelar
Headers: x-api-key: 123456
```
 
---
 
### POST /core-mock/evento
Endpoint mock que simula el envío de un evento al sistema CORE. Su único propósito es registrar en logs que la operación fue intentada.
 
**Body (JSON):**
```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```
 
**Ejemplo exitoso:**
```
POST http://localhost:8080/core-mock/evento
Headers: x-api-key: 123456
         Content-Type: application/json
```
 
**Ejemplo con error — sin API Key:**
```
POST http://localhost:8080/core-mock/evento
```
 

---

## Formato de respuesta

Todos los endpoints retornan la misma estructura:

```json
{
  "estadoObtenido": "OPERACION_EXITOSA",
  "mensajeError": null,
  "datosSalida": { }
}
```

**Códigos HTTP:**

| Situación              | HTTP |
|------------------------|------|
| Operación exitosa      | 200  |
| Recurso no encontrado  | 404  |
| Datos inválidos        | 400  |
| API Key ausente        | 401  |
| Error interno          | 500  |