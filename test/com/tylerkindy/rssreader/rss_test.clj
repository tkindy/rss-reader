(ns com.tylerkindy.rssreader.rss-test
  (:require [com.tylerkindy.rssreader.rss :as r]
            [clojure.test :refer [deftest is]]
            [clojure.data.xml :as xml]
            [clojure.string :as str]))

(defn clean-content [element]
  (let [content (:content element)]
    (if content
      (assoc element
             :content
             (->> content
                  (filter (fn [c]
                            (or (not (string? c))
                                (not (str/blank? c)))))
                  (map (fn [c]
                         (if (xml/element? c)
                           (clean-content c)
                           c)))))
      element)))

(defn parse-xml [input]
  (let [parsed (xml/parse input)]
    (clean-content parsed)))

(def empty-feed (parse-xml (java.io.FileReader. "test/examples/empty.xml")))
(def empty-channel (r/get-child empty-feed :channel))

(deftest get-children
  (is (= (r/get-children empty-feed :channel)
         (list (xml/sexp-as-element
                [:channel
                 [:title "Empty blog"]
                 [:link "https://blog.example.com/empty"]
                 [:description "This is a blog with no posts"]]))))
  (is (= (r/get-children (first (r/get-children empty-feed :channel)) :link)
         (list (xml/sexp-as-element
                [:link "https://blog.example.com/empty"])))))

(deftest get-child
  (is (= (r/get-child empty-feed :channel)
         (xml/sexp-as-element
          [:channel
           [:title "Empty blog"]
           [:link "https://blog.example.com/empty"]
           [:description "This is a blog with no posts"]])))
  (is (= (-> empty-feed
             (r/get-child :channel)
             (r/get-child :title))
         (xml/sexp-as-element [:title "Empty blog"]))))

(deftest get-str
  (is (= (r/get-str empty-channel :title)
         "Empty blog"))
  (is (= (r/get-str empty-channel :link)
         "https://blog.example.com/empty")))

(deftest element->map
  (is (= (r/element->map empty-channel [:title :description])
         {:title "Empty blog"
          :description "This is a blog with no posts"}))
  (is (= (r/element->map empty-channel [:title :foo :description])
         {:title "Empty blog"
          :description "This is a blog with no posts"})))

(deftest build-item
  (is (= (r/build-item (xml/sexp-as-element
                        [:item
                         [:title "Dangerous item"]
                         [:link "https://blog.example.com/dangerous"]
                         [:description "<p>Some text</p><script>console.log('And a script!')</script><p>More text</p>"]]))
         {:title "Dangerous item"
          :link "https://blog.example.com/dangerous"
          :description "<p>Some text</p>\n<p>More text</p>"})
      "Cleans dangerous HTML"))

(deftest parse-rss
  (is (= (r/parse-rss (java.io.FileReader. "test/examples/empty.xml"))
         {:title "Empty blog" :link "https://blog.example.com/empty"
          :description "This is a blog with no posts"
          :items (list)}))
  (is (= (r/parse-rss (java.io.FileReader. "test/examples/blog.xml"))
         {:title "My blog" :link "https://blog.example.com/blog"
          :description "Here's a real blog"
          :items (list
                  {:title "Cool post"
                   :link "https://blog.example.com/blog/cool-post"
                   :description "Here's how I wrote this really cool post."}
                  {:title "Another really cool post"
                   :link "https://blog.example.com/blog/another"
                   :description "Another one."})})))
