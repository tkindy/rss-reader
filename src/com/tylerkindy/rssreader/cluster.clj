(ns com.tylerkindy.rssreader.cluster
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:import [org.jsoup Jsoup]))

(defn read-items [in]
  (->> in
       slurp
       read-string
       (map-indexed (fn [id item] (assoc item :id id)))))

(def split-regex #"[\s,.\":]")
(def stopwords (->> (slurp "data/stopwords.txt")
                    str/split-lines
                    (filter (comp not str/blank?))
                    set))

(defn extract-text [html]
  (-> html
      Jsoup/parse
      .body
      .text))

(defn extract-terms [html]
  (->> (str/split (extract-text html) split-regex)
       (filter (comp not str/blank?))
       (map str/lower-case)
       (filter (comp not stopwords))))

(defn build-term-index [items]
  (->> items
       (mapcat (fn [{:keys [id description]}]
                 (->> description
                      extract-terms
                      (map (fn [term] {term #{id}})))))
       (apply merge-with set/union)))

(defn extract-trigrams [html]
  (->> (str/split (extract-text html) split-regex)
       (filter (comp not str/blank?))
       (partition 3 1)))

(defn build-trigram-index [items]
  (->> items
       (mapcat (fn [{:keys [id description]}]
                 (->> description
                      extract-trigrams
                      (map (fn [trigram] {trigram #{id}})))))
       (apply merge-with set/union)))

(defn -main [in]
  (let [items (read-items in)
        term-index (build-term-index items)]))

(comment
  (->> "output/items.edn"
       read-items
       build-term-index
       (map (fn [[term ids]] [term (count ids)]))
       (sort-by (fn [[_ count]] (* -1 count)))
       (take 25)))
