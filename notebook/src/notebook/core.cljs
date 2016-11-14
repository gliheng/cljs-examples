(ns notebook.core
  (:require [reagent.core :as reagent :refer [atom cursor]]
            [cljs.core.async :as async :refer [timeout put! chan <! close!]]
            [notebook.ui :as ui]
            [notebook.store :as store])
  (:require-macros [cljs.core.async.macros :as async-macros :refer [go-loop]]
                   [reagent.ratom :refer [reaction]]))

(enable-console-print!)

(defonce app-state (atom {}))

(defn note-content
  [state current-stream]
  (let [current (cursor state [:current])
        content (reaction (when-let [c @current-stream]
                            (reset! current c)
                            @(store/get-note-content (:id c))))
        change-title (fn [title]
                       (store/change-title @content title))
        change-body (fn [body]
                      (store/change-body @content body))]
    (fn []
      (cond
        (= (:id @current) (:id @content))
        [:div.note-content
         [:div.title [:input {:type "text"
                              :class "title-field"
                              :value (:title @content)
                              :on-change #(change-title (-> % .-target .-value))}]]
         [:div.content [:textarea {:class "body-field"
                                   :value (:body @content)
                                   :on-change #(change-body (-> % .-target .-value))}]]]
        :else
        [:div.note-content.loading "loading!"]))))

(defn note-list
  [state current-stream section-id]
  (let [notelist (store/get-note-list section-id)
        current (cursor state [:current])
        current2 (reaction (let [c (cond
                                     (and @current (= (:section-id @current) section-id)) @current
                                     (seq @notelist) (first @notelist) ; if current is nil, take the first as current
                                     :else :nil)]
                             (reset! current-stream c)
                             c))]
    (fn []
      (if @notelist
        (let [-current @current2]
          [:div.note-list
           [:button {:on-click #(reset! current (store/add-note section-id))} "Add Page"]
           [:ul (for [item @notelist] [:li {:key (:id item)
                                            :class (if (= (:id -current) (:id item)) "current" "")
                                            :on-click #(reset! current item)} (:title item)])]])
        [:div.note-list.loading "loading"]))))

(defn note-section
  [state section-id]
  (let [current-stream (atom nil)]
    (fn []
      [:div.note-section
       [note-content (cursor state [:detail]) current-stream]
       [note-list (cursor state [:list]) current-stream section-id]])))

(defn app
  []
  (let [sections @(store/get-sections)
        tabs (map #(with-meta note-section
                     {:id (:id %)
                      :key (:id %)
                      :title (:title %)}) sections)]
    [ui/Tabs app-state tabs
     {:on-add #(store/add-section)}]))

(reagent/render-component [app]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
