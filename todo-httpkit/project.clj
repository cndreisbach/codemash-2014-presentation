(def boot-config (read-string (slurp "boot.edn")))

(defproject todo-httpkit "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "0.3.4"]]
  :dependencies ~(:dependencies boot-config)
  :source-paths ~(:source-paths boot-config)
  :main todo.main
  :cljsbuild
  {:builds [{:source-paths ["src/clj" "src/cljs"]}]
   :crossovers [todo.todo]
   :crossover-path "src/crossover-cljs"})
