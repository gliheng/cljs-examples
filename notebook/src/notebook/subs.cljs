(ns notebook.subs
  (:require [notebook.types :refer [NoteSection NoteMeta NoteContent]]
            [notebook.backend :as backend]
            [notebook.util :as util]
            [re-frame.core :refer [reg-sub
                                   subscribe]]))

(enable-console-print!)

(reg-sub
 :sections
 (fn [db]
   (let [sections (:sections db)]
     (when (nil? sections)
       (backend/get-sections :backend-sections))
     sections)))

(reg-sub
 :note-list
 (fn [db [_ section-id]]
   (let [data (get-in db [(str "section-" section-id) :meta])]
     (when (nil? data)
       (backend/get-note-list :backend-note-list section-id))
     data)))

(reg-sub
 :note-content
 (fn [db [_ note-id]]
   (let [content (get-in db [(str "content-" note-id)])]
     (when (nil? content)
       (backend/get-note-content :backend-note-content note-id))
     content)))

(reg-sub
 :current-note
 (fn [db [_ section-id]]
   (get-in db [(str "section-" section-id) :current])))
