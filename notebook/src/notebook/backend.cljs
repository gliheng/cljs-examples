(ns notebook.backend
  (:require [cljs.core.async :as async :refer [timeout put! chan <! close!]]
            [notebook.types :refer [NoteSection NoteMeta NoteContent]])
  (:require-macros [cljs.core.async.macros :as async-macros :refer [go]]))

(defonce mock-section-data
  [(NoteSection. "AAA" "Java")
   (NoteSection. "BBB" "Unix")])

(defonce mock-section-list
  {"AAA" [(NoteMeta. "1" "Generics tip" "AAA")
               (NoteMeta. "2" "Nio guidelines" "AAA")
               (NoteMeta. "3" "JVM optimisation" "AAA")]
   "BBB" [(NoteMeta. "11" "Threads" "BBB")
               (NoteMeta. "12" "Process" "BBB")
               (NoteMeta. "13" "IPC" "BBB")]})

(defonce mock-content-data
  {"1" (NoteContent. "1" "Generics tip" "Hello" "AAA")
   "2" (NoteContent. "2" "Nio guidelines" "Something important" "AAA")
   "3" (NoteContent. "3" "JVM optimisation" "This is a hard one" "AAA")
   "11" (NoteContent. "11" "Threads" "Bullshit" "BBB")
   "12" (NoteContent. "12" "Process" "Blah, blah" "BBB")
   "13" (NoteContent. "13" "IPC" "lalala" "BBB")})

(defn get-sections
  []
  (let [out (chan)]
    (go (do (<! (timeout 10))
            (put! out mock-section-data)))
    out))

(defn get-note-list
  [id]
  (let [out (chan)]
    (go (do (<! (timeout 400))
            (put! out (get mock-section-list id))))
    out))

(defn get-note-content
  [id]
  (let [out (chan)]
    (go (do (<! (timeout 400))
            (put! out (get mock-content-data id))))
    out))

(defn change-title
  ""
  [id title])

(defn change-body
  ""
  [id body]
  )

