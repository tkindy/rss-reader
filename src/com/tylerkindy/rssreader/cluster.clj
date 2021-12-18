(ns com.tylerkindy.rssreader.cluster
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:import [org.jsoup Jsoup]))

(defn read-items [in]
  (->> in
       slurp
       read-string
       (map-indexed (fn [id item] (assoc item :id id)))))

(def split-regex #"[\s,.\"]")
(def stopwords (->> (slurp "data/stopwords.txt")
                    str/split-lines
                    (filter (comp not str/blank?))
                    set))

(defn extract-terms [html]
  (let [text (-> html
                 Jsoup/parse
                 .body
                 .text)]
    (->> (str/split text split-regex)
         (filter (comp not str/blank?))
         (map str/lower-case)
         (filter (comp not stopwords)))))

(defn build-term-index [items]
  (->> items
       (mapcat (fn [{:keys [id description]}]
                 (->> description
                      extract-terms
                      (map (fn [term] {term #{id}})))))
       (apply merge-with set/union)))

(defn -main [in]
  (let [items (read-items in)
        term-index (build-term-index items)]))
