#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.3.1"

;; Copyright (c) Alan Dipert and Micha Niskin. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(set-env!
 :project       'hoplon-demo-todofrp
 :version       "1.0.0-SNAPSHOT"
 :description   "TodoFRP using hoplon and http-kit." 
 :license       {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}
 :dependencies '[[tailrecursion/boot.task   "2.1.3"]
                 [tailrecursion/hoplon      "5.8.3"]
                 [tailrecursion/boot.notify "2.0.1"]                 
                 [org.clojure/clojurescript "0.0-2202"]
                 [cljs-ajax                 "0.2.3"]
                 [http-kit "2.1.13"]
                 [compojure "1.1.5"]
                 [ring/ring-devel "1.1.8"]
                 [ring/ring-core "1.1.8"]                 
                 [liberator "0.10.0"]
                 [clj-time "0.6.0"]]  
 :out-path       "resources/public"
 :src-paths     #{"src" "resources"})

;; Static resources (css, images, etc.):
(add-sync! (get-env :out-path) #{"resources/assets"})

(require '[tailrecursion.hoplon.boot :refer :all])
(require 'todo.main)

(deftask development
  "Build my-project for development."
  []
  (comp (watch) (hoplon {:prerender false :pretty-print true})))

(deftask production
  "Build my-project for production."
  []
  (hoplon {:optimizations :advanced}))

(deftask server
  []
  (fn [continue]
    (fn [event]
      (todo.main/-main)
      (continue event))))

(deftask run-server
  []
  (comp (watch) (server) (hoplon {:prerender false :pretty-print true})))

