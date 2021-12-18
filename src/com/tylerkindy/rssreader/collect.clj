(ns com.tylerkindy.rssreader.collect
  (:require [clojure.java.io :as io]
            [com.tylerkindy.rssreader.feeds.rss :as rss]
            [clojure.pprint :refer [pprint]]))

(def feeds ["https://www.gameinformer.com/rss.xml"
            "http://rss.cnn.com/rss/cnn_us.rss"
            "https://feeds.npr.org/1001/rss.xml"
            "https://www.theverge.com/rss/index.xml"
            "https://kotaku.com/rss"
            "http://feeds.feedburner.com/ign/all"])

(defn -main [out]
  (let [items (->> feeds
                   (map (fn [feed] (rss/parse-rss (io/reader (java.net.URL. feed)))))
                   (mapcat :items))]
    (with-open [w (io/writer out)]
      (pprint items w))))
