-- Persist the MIME type of activity images (ac_calendario_actividades.calact_img).
--
-- ActivityImageServlet (the /foto-actividad endpoint that previews the image in "Mantenimiento de
-- Actividades") needs the image's MIME type to set Content-Type correctly. The type was only known
-- right after a fresh upload in the same session (MbActividad.cargaImagen captures the uploaded
-- item's content type); when editing an existing activity's image without re-uploading, the type
-- was unknown and the servlet fell back to a guessed image/png, mis-serving JPEG/GIF images.
--
-- Additive and backward-compatible (shared MySQL DB): a new nullable column. Existing rows keep
-- calact_mime = NULL; the servlet still falls back to image/png for them. Any activity saved or
-- edited after this migration persists its real MIME type, so the guess only ever affects legacy
-- rows that are never re-saved.

ALTER TABLE ac_calendario_actividades
    ADD COLUMN calact_mime VARCHAR(100) NULL AFTER calact_img;
