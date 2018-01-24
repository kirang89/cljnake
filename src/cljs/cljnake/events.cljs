(ns cljnake.events
  (:require [re-frame.core :as rf]
            [cljnake.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))
