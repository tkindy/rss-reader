(ns com.tylerkindy.rssreader.cluster
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:import [org.jsoup Jsoup]))

(defn read-items [in]
  (->> in
       slurp
       read-string))

(def split-regex #"(?:\s|,|(?:\.\s)|\"|:|\|)")
(def stopwords (->> (slurp "data/stopwords.txt")
                    str/split-lines
                    (filter (comp not str/blank?))
                    set))

(defn extract-text [html]
  (-> html
      Jsoup/parse
      .body
      .text))

(defn build-index [extract items]
  (->> items
       (mapcat (fn [{:keys [link], :as item}]
                 (->> item
                      extract
                      (map (fn [k] {k #{link}})))))
       (apply merge-with set/union)))

(defn extract-terms [html]
  (->> (str/split (extract-text html) split-regex)
       (filter (comp not str/blank?))
       (map str/lower-case)
       (filter (comp not stopwords))))


(defn build-term-index [items]
  (build-index (fn [{:keys [title]}] (extract-terms title))
               items))

(defn extract-ngrams [n html]
  (->> (str/split (extract-text html) split-regex)
       (filter (comp not str/blank?))
       (map str/lower-case)
       (partition n 1)
       (filter (fn [ngram] (not-every? stopwords ngram)))))

(defn extract-trigrams [html]
  (extract-ngrams 3 html))

(defn build-trigram-index [items]
  (build-index (fn [{:keys [title]}] (extract-trigrams title))
               items))

(defn build-ngram-index [n items]
  (build-index (fn [{:keys [title]}] (extract-ngrams n title))
               items))

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
