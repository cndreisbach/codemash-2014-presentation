(def boot-config (read-string (slurp "boot.edn")))

(defproject todo-liberator "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies ~(:dependencies boot-config)
  :main todo.main)
