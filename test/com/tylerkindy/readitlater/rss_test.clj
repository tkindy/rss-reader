(ns com.tylerkindy.readitlater.rss-test
  (:require [com.tylerkindy.readitlater.rss :as r]
            [clojure.test :refer [deftest is]]
            [clojure.data.xml :as xml]))

(def empty-feed
  (xml/sexp-as-element
   [:rss {:version "2.0"}
    [:channel
     [:title "Empty blog"]
     [:link "https://blog.example.com"]
     [:description "This is a blog with no posts"]]]))

(def empty-channel (r/get-child empty-feed :channel))

(deftest get-children
  (is (= (r/get-children empty-feed :channel)
         (list (xml/sexp-as-element
                [:channel
                 [:title "Empty blog"]
                 [:link "https://blog.example.com"]
                 [:description "This is a blog with no posts"]]))))
  (is (= (r/get-children (first (r/get-children empty-feed :channel)) :link)
         (list (xml/sexp-as-element
                [:link "https://blog.example.com"])))))

(deftest get-child
  (is (= (r/get-child empty-feed :channel)
         (xml/sexp-as-element
          [:channel
           [:title "Empty blog"]
           [:link "https://blog.example.com"]
           [:description "This is a blog with no posts"]])))
  (is (= (-> empty-feed
             (r/get-child :channel)
             (r/get-child :title))
         (xml/sexp-as-element [:title "Empty blog"]))))

(deftest get-str
  (is (= (r/get-str empty-channel :title)
         "Empty blog"))
  (is (= (r/get-str empty-channel :link)
         "https://blog.example.com")))

(deftest element->map
  (is (= (r/element->map empty-channel [:title :description])
         {:title "Empty blog"
          :description "This is a blog with no posts"}))
  (is (= (r/element->map empty-channel [:title :foo :description])
         {:title "Empty blog"
          :description "This is a blog with no posts"})))
