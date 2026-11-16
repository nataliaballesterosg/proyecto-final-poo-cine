-- =====================================================================
-- Esquema y datos iniciales del Cine
-- PostgreSQL ejecuta este archivo automaticamente la PRIMERA vez que
-- arranca con un volumen vacio (carpeta /docker-entrypoint-initdb.d).
--
-- Datos generados (11.210 registros en total; el enunciado pide minimo 10.000):
--      200 peliculas
--       10 salas
--    1.000 funciones
--   10.000 reservas de sillas
-- setseed() hace que los datos sean siempre los mismos -> pruebas repetibles.
-- =====================================================================

CREATE TABLE peliculas (
    id                BIGSERIAL PRIMARY KEY,
    titulo            VARCHAR(150) NOT NULL,
    genero            VARCHAR(40)  NOT NULL,
    duracion_minutos  INTEGER      NOT NULL,
    clasificacion     VARCHAR(10)  NOT NULL,
    anio              INTEGER
);

CREATE TABLE salas (
    id               BIGSERIAL PRIMARY KEY,
    nombre           VARCHAR(40) NOT NULL,
    filas            INTEGER     NOT NULL,
    sillas_por_fila  INTEGER     NOT NULL
);

CREATE TABLE funciones (
    id           BIGSERIAL PRIMARY KEY,
    pelicula_id  BIGINT    NOT NULL REFERENCES peliculas(id) ON DELETE CASCADE,
    sala_id      BIGINT    NOT NULL REFERENCES salas(id),
    fecha_hora   TIMESTAMP NOT NULL,
    precio       INTEGER   NOT NULL
);

CREATE TABLE reservas (
    id             BIGSERIAL PRIMARY KEY,
    funcion_id     BIGINT      NOT NULL REFERENCES funciones(id) ON DELETE CASCADE,
    fila           INTEGER     NOT NULL,
    numero         INTEGER     NOT NULL,
    cliente        VARCHAR(80) NOT NULL,
    fecha_reserva  TIMESTAMP   NOT NULL,
    -- REGLA DE NEGOCIO: una silla solo puede reservarse una vez por funcion
    CONSTRAINT silla_unica UNIQUE (funcion_id, fila, numero)
);

SELECT setseed(0.42);

-- -------------------------------------------------------------- peliculas
INSERT INTO peliculas (titulo, genero, duracion_minutos, clasificacion, anio)
SELECT
    (ARRAY['El regreso','La sombra','El guardian','La leyenda','El misterio','La noche','El viaje',
           'La ciudad','El secreto','La promesa','El reino','La isla','El destino','La tormenta','El eco'])[1 + floor(random()*15)::int] || ' ' ||
    (ARRAY['del tiempo','del mar','del norte','de cristal','de otoño','sin fin','de fuego','escondido',
           'de medianoche','de las estrellas'])[1 + floor(random()*10)::int] || ' ' || n,
    (ARRAY['Accion','Comedia','Drama','Terror','Animacion','Ciencia ficcion','Romance','Suspenso'])[1 + floor(random()*8)::int],
    80 + floor(random()*90)::int,
    (ARRAY['TP','+7','+12','+15','+18'])[1 + floor(random()*5)::int],
    1990 + floor(random()*36)::int
FROM generate_series(1, 200) AS n;

-- ------------------------------------------------------------------ salas
-- Salas de 8 a 12 filas (A..L) y de 10 a 14 sillas por fila
INSERT INTO salas (nombre, filas, sillas_por_fila)
SELECT 'Sala ' || n, 8 + (n % 5), 10 + (n % 3) * 2
FROM generate_series(1, 10) AS n;

-- -------------------------------------------------------------- funciones
INSERT INTO funciones (pelicula_id, sala_id, fecha_hora, precio)
SELECT
    1 + floor(random()*200)::int,
    1 + floor(random()*10)::int,
    TIMESTAMP '2026-12-01 12:00' + floor(random()*30)::int * INTERVAL '1 day'
                                 + floor(random()*5)::int * INTERVAL '150 minutes',
    (ARRAY[12000, 15000, 18000, 22000])[1 + floor(random()*4)::int]
FROM generate_series(1, 1000);

-- --------------------------------------------------------------- reservas
-- Se eligen 10.000 sillas al azar entre todas las sillas de todas las funciones
-- (asi nunca se repite una silla y se respeta la restriccion silla_unica).
INSERT INTO reservas (funcion_id, fila, numero, cliente, fecha_reserva)
SELECT funcion_id, fila, numero,
       'cliente' || (1 + floor(random()*3000)::int),
       TIMESTAMP '2026-11-01' + random() * INTERVAL '30 days'
FROM (
    SELECT f.id AS funcion_id, fi.fila, nu.numero
    FROM funciones f
    JOIN salas s ON s.id = f.sala_id
    CROSS JOIN LATERAL generate_series(1, s.filas) AS fi(fila)
    CROSS JOIN LATERAL generate_series(1, s.sillas_por_fila) AS nu(numero)
    ORDER BY random()
    LIMIT 10000
) AS sillas;

-- ---------------------------------------------------------------- indices
CREATE INDEX idx_funciones_pelicula ON funciones(pelicula_id);
CREATE INDEX idx_funciones_sala     ON funciones(sala_id);
-- (la restriccion silla_unica ya crea un indice sobre reservas(funcion_id, fila, numero))

ANALYZE;
