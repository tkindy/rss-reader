(ns com.tylerkindy.rssreader.html)

(def safelist (org.jsoup.safety.Safelist/relaxed))

(defn clean [html baseUri]
  (org.jsoup.Jsoup/clean html baseUri safelist))
