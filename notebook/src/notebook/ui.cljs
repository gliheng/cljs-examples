(ns notebook.ui
  (:require [reagent.core :as reagent]))


(defn Tabs
  [state children opts]
  (if (seq children)
    (let [meta-list (map meta children)
          title-list (map :title meta-list)
          key-list (map :key meta-list)
          current (or (:current @state) (first key-list))
          comp-map (zipmap key-list children)
          tabs (map (fn [title key]
                      [:li {:class (if (= key current) "current")
                            :key key
                            :on-click (fn [] (swap! state assoc :current key))} title])
                    title-list
                    key-list)
          tabs-with-add (if (:on-add opts)
                          (concat tabs [[:li {:key "__add__"
                                              :class "add"
                                              :on-click (:on-add opts)} [:button "Add"]]])
                          tabs)]
      [:div.Tabs
       [:div.Title
        [:ul tabs-with-add]]
       [:div.Content [(get comp-map current) (reagent/cursor state [:content]) current]]])
    nil))
