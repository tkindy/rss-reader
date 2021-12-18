(ns com.tylerkindy.readitlater.rss
  (:require [clojure.data.xml :as xml]))

(defn get-channel [feed]
  (-> feed
      :content
      first))

(defn get-str [element tag]
  (->> element
       :content
       (filter (fn [element] (and map? (= (:tag element) tag))))
       first
       :content
       first))
