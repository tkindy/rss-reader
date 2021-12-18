(ns com.tylerkindy.rssreader.html-test
  (:require [com.tylerkindy.rssreader.html :as h]
            [clojure.test :refer [deftest is]]
            [clojure.string :as str]))

(defn trim-lines [s]
  (->> s
       str/split-lines
       (map str/trim)
       (filter (comp not str/blank?))
       (str/join "\n")))

(deftest clean
  (is (= (trim-lines (h/clean (slurp "test/examples/script.html") "https://example.com"))
         (trim-lines (slurp "test/examples/script-cleaned.html")))))
