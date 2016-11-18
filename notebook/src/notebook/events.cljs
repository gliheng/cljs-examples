(ns notebook.events
  (:require [re-frame.core :refer [reg-event-db
                                   reg-event-fx]]
            [notebook.util :refer [new-id]]
            [notebook.types :refer [NoteSection NoteMeta NoteContent]]))

(reg-event-db
 :backend-sections
 (fn [db [_ sections]]
   (assoc db :sections sections)))

(reg-event-fx
 :backend-note-list
 (fn [{db :db} [_ section-id data]]
   (let [sec (str "section-" section-id)]
     {:db (assoc-in db [sec :meta] data)
      :dispatch [:set-current-note section-id (first data)]})))

(reg-event-db
 :backend-note-content
 (fn [db [_ note-id data]]
   (assoc-in db [(str "content-" note-id)] data)))

(reg-event-fx
 :add-section
 (fn [{db :db} [_ section-name tab-event tab-id]]
   (let [section-id (new-id "s")]
     {:db (-> db
              (update-in [:sections] conj (NoteSection. section-id section-name))
              (assoc-in [(str "section-" section-id) :meta] []))
      ;; also add a new page and jump to section
      :dispatch-n [[:add-page section-id]
                   [tab-event tab-id section-id]]})))

(reg-event-fx
 :add-page
 (fn [{db :db} [_ section-id]]
   (let [note-id (new-id "n")
         new-meta (NoteMeta. note-id "New Note" section-id)
         new-content (NoteContent. note-id "New Note" "" section-id)]
     {:db (-> db
              (assoc (str "content-" note-id) new-content)
              (update-in [(str "section-" section-id) :meta] conj new-meta))
      :dispatch [:set-current-note section-id new-meta]})))

(reg-event-db
 :set-current-note
 (fn [db [_ section-id meta]]
   (assoc-in db [(str "section-" section-id) :current] meta)))

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

(reg-event-db
 :change-title
 (fn [db [_ note title]]
   (let [note-id (:id note)
         section-id (:section-id note)
         idx (find-idx-in-list (get-in db [(str "section-" section-id) :meta]) :id note-id)]
     (-> db
         (assoc-in [(str "content-" note-id) :title] title)
         (assoc-in [(str "section-" section-id) :meta idx :title] title)))))

(reg-event-db
 :change-body
 (fn [db [_ note body]]
   (let [note-id (:id note)]
     (assoc-in db [(str "content-" note-id) :body] body))))
