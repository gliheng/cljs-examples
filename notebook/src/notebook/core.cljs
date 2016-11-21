(ns notebook.core
  (:require [reagent.core :as reagent]
            [notebook.ui :as ui]
            [notebook.subs]
            [notebook.events]
            [re-frame.core :refer [dispatch subscribe]]
            [devtools.core :as devtools]))

(devtools/install!)
(enable-console-print!)

(defn note-content
  [section-id]
  (let [content (subscribe [:current-note-content section-id])]
    (fn []
      (let [change-title (fn [title]
                           (dispatch [:change-title @content title]))
            change-body (fn [body]
                          (dispatch [:change-body @content body]))]
        (if (or (nil? content)
                (nil? @content))
          [:div.note-content.loading "loading!"]

          [:div.note-content
           [:div.title [:input {:type "text"
                                :class "title-field"
                                :value (:title @content)
                                :on-change #(change-title (-> % .-target .-value))}]]
           [:div.content [:textarea {:class "body-field"
                                     :value (:body @content)
                                     :on-change #(change-body (-> % .-target .-value))}]]])))))

(defn note-list
  [section-id]
  (let [current (subscribe [:current-note section-id])
        notelist (subscribe [:note-list section-id])]
    (fn [section-id]
      (let [current @current
            notelist @notelist]
        (if notelist
          [:div.note-list
           [:button {:on-click #(dispatch [:add-page section-id])} "Add Page"]
           [:ul (for [item notelist] [:li {:key (:id item)
                                            :class (if (= (:id current) (:id item)) "current" "")
                                            :on-click #(dispatch [:set-current-note section-id item])} (:title item)])]]
          [:div.note-list.loading "loading"])))))

(defn note-section
  [section-id]
  (fn [section-id]
    [:div.note-section
     [note-content section-id]
     [note-list section-id]]))

(defn app
  []
  (let [sections (subscribe [:sections])]
    (fn []
      (let [tabs (map #(with-meta note-section
                         {:id (:id %)
                          :key (:id %)
                          :title (:title %)})
                      @sections)]
        [ui/Tabs {:key "section"
                  :on-add (fn [tab-event tab-id]
                            (dispatch [:add-section
                                       "New Section"
                                       tab-event
                                       tab-id]))}
         tabs]))))

(reagent/render-component [app]
                          (. js/document (getElementById "app")))
