-- Pólizas de prueba
INSERT INTO poliza (id, tipo_poliza, fecha_inicio_vigencia, fecha_fin_vigencia, meses_vigencia, valor_canon_mensual, prima, estado_poliza, fecha_de_creacion, fecha_de_modificacion) VALUES
    ('a1b2c3d4-0000-0000-0000-000000000001', 'INDIVIDUAL', '2024-01-01 00:00:00', '2025-01-01 00:00:00', 12, 500000.00, 50000.00, 'ACTIVA',    '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('a1b2c3d4-0000-0000-0000-000000000002', 'INDIVIDUAL', '2024-01-01 00:00:00', '2024-07-01 00:00:00', 6,  300000.00, 30000.00, 'CANCELADA', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('a1b2c3d4-0000-0000-0000-000000000003', 'COLECTIVA',  '2024-01-01 00:00:00', '2026-01-01 00:00:00', 24, 800000.00, 80000.00, 'ACTIVA',    '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('a1b2c3d4-0000-0000-0000-000000000004', 'COLECTIVA',  '2023-01-01 00:00:00', '2024-01-01 00:00:00', 12, 900000.00, 90000.00, 'RENOVADA',  '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- Riesgos de prueba
INSERT INTO riesgo (id, tipo_riesgo, descripcion, estado_riesgo, fecha_de_creacion, fecha_de_modificacion, poliza_id) VALUES
    ('b1b2c3d4-0000-0000-0000-000000000001', 'INCENDIO',   'Riesgo por incendio',   'ACTIVO',    '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'a1b2c3d4-0000-0000-0000-000000000001'),
    ('b1b2c3d4-0000-0000-0000-000000000002', 'ROBO',       'Riesgo por robo',       'ACTIVO',    '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'a1b2c3d4-0000-0000-0000-000000000003'),
    ('b1b2c3d4-0000-0000-0000-000000000003', 'INUNDACION', 'Riesgo por inundacion', 'ACTIVO',    '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'a1b2c3d4-0000-0000-0000-000000000003'),
    ('b1b2c3d4-0000-0000-0000-000000000004', 'TERREMOTO',  'Riesgo por terremoto',  'CANCELADO', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'a1b2c3d4-0000-0000-0000-000000000002');