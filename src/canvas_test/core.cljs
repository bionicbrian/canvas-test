(ns canvas-test.core
    (:require [goog.dom :as dom]))

(enable-console-print!)

(def actors (atom [{:name "Brian" :x {:current 0 :new 700} :y {:current 0 :new 700}}
                   {:name "Finn" :x {:current 0 :new 700} :y {:current 0 :new 700}}]))

(def canvas (.createElement js/document "canvas"))
(def ctx (.getContext canvas "2d"))

(set! (.-width canvas) (.-innerWidth js/window))
(set! (.-height canvas) (.-innerHeight js/window))

(.appendChild (.-body js/document) canvas)

(defn is-greater-than-threshold? [dim]
  (> (Math/abs (- (:current dim) (:new dim))) 0.5))

(defn should-update? [actors]
  (some (fn [{:keys [x y]}] (some is-greater-than-threshold? [x y])) actors))

(defn fill-actor-rect [ctx {:keys [x y]}]
  (.fillRect ctx (:current x) (:current y) 50 50))

(defn render [canvas ctx actors]
  (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
  (dorun (map (partial fill-actor-rect ctx) @actors)))

(defn split-distance [current new]
  (+ current (* (- new current) 0.1)))

(defn move-distance [m k]
  (if (is-greater-than-threshold? (k m))
      (update-in m [k :current] split-distance (get-in m [k :new]))
      (update-in m [k :new] #(rand 500))))

(defn update-actor [{:keys [x y] :as actor}]
  (reduce move-distance actor (keys (select-keys actor [:x :y]))))

(defn update [actors]
  (map #(update-actor %) actors))

(defn loop-game []
  (render canvas ctx actors)
  (swap! actors update)
  (.requestAnimationFrame js/window loop-game))

(loop-game)
