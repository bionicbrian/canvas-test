(ns canvas-test.core
    (:require [goog.dom :as dom]))

(enable-console-print!)

(def actors 
  [{:name "Brian" :x {:current 0 :new 700} :y {:current 0 :new 700}}
   {:name "Finn" :x {:current 0 :new 700} :y {:current 0 :new 700}}])

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
  (dorun (map (partial fill-actor-rect ctx) actors)))

(defn split-distance [current new]
  (+ current (* (- new current) 0.1)))

(defn update-actor [{:keys [x y] :as actor}]
  (reduce
    (fn [m k] (update-in m [k :current] split-distance (get-in m [k :new])))
    actor
    (keys (select-keys actor [:x :y]))))

(defn update [actors]
  (map #(update-actor %) actors))

(defn loop-game [canvas ctx actors]
  (let [new-actors (update actors)]
    (if (should-update? new-actors)
      (do
        (render canvas ctx new-actors)
        (.requestAnimationFrame js/window (partial loop-game canvas ctx new-actors))))))
        
(render canvas ctx actors)
(loop-game canvas ctx actors)
