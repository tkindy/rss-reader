(ns com.tylerkindy.readitlater.rss
  (:require [clojure.data.xml :as xml]))

(defn get-children [element tag]
  (->> element
       :content
       (filter (fn [element] (and map? (= (:tag element) tag))))))

(defn get-child [element tag]
  (first (get-children element tag)))

(defn get-str [element tag]
  (->> (get-child element tag)
       :content
       first))
