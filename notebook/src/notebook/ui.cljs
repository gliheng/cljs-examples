(ns notebook.ui
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch
                                   subscribe
                                   reg-event-db
                                   reg-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(reg-sub
 :tab-index
 (fn [db [_ key]]
   (get-in db [:tab-index key])))

(reg-event-db
 :tab-index
 (fn [db [_ key tab]]
   (assoc-in db [:tab-index key] tab)))

(defn Tabs
  [opts children]
  (let [tab-id (:key opts)
        tab-idx (subscribe [:tab-index tab-id])]
    (fn tabs-render [opts children]
      (let [meta-list (map meta children)
            title-list (map :title meta-list)
            key-list (map :key meta-list)
            current (or @tab-idx (first key-list))
            comp-map (zipmap key-list children)
            tabs (map (fn [title key]
                        [:li {:class (if (= key current) "current")
                              :key key
                              :on-click #(dispatch [:tab-index tab-id key])} title])
                      title-list
                      key-list)
            tabs-with-add (if (:on-add opts)
                            (concat tabs [[:li {:key "__add__"
                                                :class "add"
                                                :on-click #((:on-add opts) :tab-index tab-id)}
                                           [:button "Add"]]])
                            tabs)]
        (when (not= 0 (count tabs))
          [:div.Tabs
           [:div.Title
            [:ul tabs-with-add]]
           [:div.Content [(get comp-map current) current]]])))))
