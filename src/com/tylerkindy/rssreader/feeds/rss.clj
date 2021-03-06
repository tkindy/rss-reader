(ns com.tylerkindy.rssreader.feeds.rss
  (:require [clojure.data.xml :as xml]
            [clojure.string :as str]
            [com.tylerkindy.rssreader.html :as html]))

(defn get-children [element tag]
  (->> element
       :content
       (filter (fn [element] (and map? (= (:tag element) tag))))))

(defn get-child [element tag]
  (first (get-children element tag)))

(defn get-str [element tag]
  (let [s (->> (get-child element tag)
               :content
               first)]
    (if s
      (str/trim s)
      s)))

(defn element->map [element keys]
  (->> keys
       (map (fn [key] [key (get-str element key)]))
       (filter (comp not nil? second))
       (into {})))

(defn build-item [element]
  (let [{:keys [description link], :as element}
        (element->map element [:title :link :description])]
    (if description
      (assoc element :description (html/clean description link))
      element)))

(defn parse-rss [input]
  (let [parsed (xml/parse input)
        channel (get-child parsed :channel)
        items (map build-item (get-children channel :item))]
    (-> channel
        (element->map [:title :link :description])
        (assoc :items items))))
