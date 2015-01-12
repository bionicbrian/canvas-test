(defproject canvas-test "0.1.0-SNAPSHOT"
  :description "Just playing with CLJS and Canvas"
  :url "http://bionicbrian.com"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "canvas-test"
              :source-paths ["src"]
              :compiler {
                :output-to "canvas_test.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
