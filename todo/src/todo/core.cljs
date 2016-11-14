(ns todo.core
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [dispatch
                                   subscribe
                                   reg-event-db
                                   reg-event-fx
                                   reg-sub
                                   inject-cofx
                                   reg-cofx]]))


(defrecord Todo [id txt])

(enable-console-print!)

;; reg-cofx is used to generate a self increasing id.
(defonce seq-id (atom 0))
(reg-cofx
 :uniq-seq
 (fn [cofx _]
   (let [ret (assoc cofx :seq @seq-id)]
     (swap! seq-id inc)
     ret)))

(reg-event-fx
 :todo-add
 [(inject-cofx :uniq-seq)]
 (fn [{:keys [db seq]} [_ txt]]
   {:db (update-in db [:list] conj (Todo. seq txt))}))

(reg-event-db
 :todo-remove
 (fn [db [_ todo]]
   (update-in db [:list] (partial remove #(= todo %)))))

(reg-sub
 :todo-list
 (fn [db _]
   (:list db)))

(defn todo-item
  [todo]
  [:li (.-txt todo)
   [:a {:href "javascript:void(0)"
        :title "click to remove"
        :on-click #(dispatch [:todo-remove todo])} "x"]])

(defn todo-list
  []
  (let [todos (subscribe [:todo-list])]
    (fn []
      (println "render list" @todos)
      (if (seq @todos)
        [:ul (for [todo @todos] ^{:key (.-id todo)} [todo-item todo])]
        [:div "Empty todo list!"]))))

(defn todo-app []
  (let [ipt (atom "")]
    (fn []
      [:div.list
       [:div.add
        [:input {:placeholder "What do you want to do?"
                 :value @ipt
                 :on-change #(reset! ipt (-> % .-target .-value))}]
        [:button {:on-click #(dispatch [:todo-add @ipt])} "Add"]]
       [todo-list]])))

(reagent/render-component [todo-app]
                          (. js/document (getElementById "app")))
