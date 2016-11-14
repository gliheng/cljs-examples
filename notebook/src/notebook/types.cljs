(ns notebook.types)

(defrecord NoteSection [id title])
(defrecord NoteMeta [id title section-id])
(defrecord NoteContent [id title body section-id])
