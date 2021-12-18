(ns com.tylerkindy.readitlater.rss-test
  (:require [com.tylerkindy.readitlater.rss :as r]
            [clojure.test :refer [deftest is]]
            [clojure.data.xml :as xml]))

(def marco-feed (xml/parse (java.io.FileReader. "test/examples/marco-org.xml")))

(def empty-feed
  (xml/sexp-as-element
   [:rss {:version "2.0"}
    [:channel
     [:title "Empty blog"]
     [:link "https://blog.example.com"]
     [:description "This is a blog with no posts"]]]))

(deftest get-str
  (is (= (r/get-str (r/get-channel marco-feed) :title)
         "Marco.org"))
  (is (= (r/get-str (r/get-channel empty-feed) :link)
         "https://blog.example.com")))
