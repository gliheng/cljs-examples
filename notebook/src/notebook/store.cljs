(ns notebook.store
  (:require [reagent.core :as reagent :refer [atom cursor]]
            [cljs.core.async :as async :refer [<!]]
            [notebook.types :refer [NoteSection NoteMeta NoteContent]]
            [notebook.backend :as backend]
            [notebook.util :as util]
            [cljs.core.async :as async :refer [timeout put! chan <! close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)


(defonce sections (atom nil))
(defonce metas (atom {}))
(defonce contents (atom {}))


(defn get-sections
  []
  (if (nil? @sections)
    (let [section-chan (backend/get-sections)]
      (go (reset! sections (<! section-chan)))))
  sections)


(defn get-note-list
  [id]
  (if ((comp not contains?) @metas id)
    (let [list-chan (backend/get-note-list id)]
      (go (swap! metas assoc id (<! list-chan)))))
  (cursor metas [id]))


(defn get-note-content
  [id]
  (if ((comp not contains?) @contents id)
    (let [content-chan (backend/get-note-content id)]
      (go (swap! contents assoc id (<! content-chan)))))
  (cursor contents [id]))


(defn- find-idx-in-list
  [list key val]
  (loop [idx 0
         list list]
    (if (seq list)
      (if (= (key (first list)) val)
        idx
        (recur (inc idx)
               (rest list)))
      -1)))


(defn change-title
  [note title]
  (let [id (:id note)
        section-id (:section-id note)
        idx (find-idx-in-list (get @metas section-id) :id id)]
    (swap! contents assoc-in [id :title] title)
    (swap! metas assoc-in [section-id idx :title] title)))


(defn change-body
  [note body]
  (let [id (:id note)]
    (swap! contents assoc-in [id :body] body)))


(defn add-note
  [section-id]
  (let [note-id (util/new-id "note")
        note-title "New Note"
        note (NoteMeta. note-id note-title section-id)]
    (swap! contents assoc note-id (NoteContent. note-id note-title "" section-id))
    (swap! metas assoc section-id (into [note] (get @metas section-id)))
    note))


(defn add-section
  []
  (let [section-id (util/new-id "section")
        section (NoteSection. section-id "New Section")]
    (swap! sections conj section)
    (add-note section-id)
    section))
