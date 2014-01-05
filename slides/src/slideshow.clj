(ns slideshow)

(defmacro slideshow
  [& rest]
  `(html
    (head
     (link :rel "stylesheet"
           :href "reveal.js/css/reveal.css")
     (link :rel "stylesheet"
           :href "reveal.js/css/theme/sky.css"
           :id "theme")
     (link :rel "stylesheet"
           :href "reveal.js/plugin/highlight/github.min.css"))
    (body
     (slides
      ~@rest))))
