(ns notebook.util)

(def idx (atom 0))
(defn new-id
  [prefix]
  (swap! idx inc)
  (str prefix "-" @idx))

