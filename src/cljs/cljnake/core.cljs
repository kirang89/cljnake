(ns cljnake.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [cljnake.events :as events]
            [cljnake.views :as views]
            [cljnake.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "Starting app in development mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/game-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
