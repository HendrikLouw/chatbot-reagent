(ns chatbot.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

;; -------------------------
;; Views

(defn add-to-history [chatbot-state input-value]
  (swap! chatbot-state update-in [:history] #(conj % input-value)))

(defn clear-chatbot-input [chatbot-state]
  (let [input (.getElementById js/document "chatbot-input")]
    (set! (.-value input) "")
    (swap! chatbot-state update-in [:input] #(.-value input))))

(defn handle-chatbot-input [e chatbot-state]
  (let [keyName (.-key e)
        input-value (.-value (.getElementById js/document "chatbot-input"))]
    (cond (= keyName "Enter") ((add-to-history chatbot-state input-value)
                               (clear-chatbot-input chatbot-state)))))

(defn chatbot-greeting [] [:h2 "Welcome to chatbot"])

(defn history-item [item] [:li item])

(defn chatbot-history [chatbot-state]
  (let [history (:history @chatbot-state)]
    (into [:ul] (map history-item history))))

(defn chatbot-input [chatbot-state]
  [:div.input-field
   [:input {:id :chatbot-input :type :text :on-key-up #(handle-chatbot-input % chatbot-state)}]])

(defn chatbot []
  (let [chatbot-state (reagent/atom {:history ["Hello, how are you doing?"]
                                     :input nil})]
    (fn []
      [:div.chatbot
       [chatbot-greeting]
       [chatbot-history chatbot-state]
       [chatbot-input chatbot-state]])))

(defn home-page []
  [chatbot])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
