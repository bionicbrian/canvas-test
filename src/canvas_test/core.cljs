(ns canvas-test.core
    (:require [goog.dom :as dom]))

(enable-console-print!)

(def actors 
  [{:x {:current 0 :new 700} :y {:current 0 :new 700}}
   {:x {:current 0 :new 700} :y {:current 0 :new 700}}])

(def canvas (.createElement js/document "canvas"))
(def ctx (.getContext canvas "2d"))

(set! (.-width canvas) (.-innerWidth js/window))
(set! (.-height canvas) (.-innerHeight js/window))

(.appendChild (.-body js/document) canvas)

(defn should-update? [actors]
  (some (fn [{:keys [x y]}] (or (not= (:current x) (:new x)) (not= (:current y) (:new y)))) actors))

(defn fill-actor-rect [ctx {:keys [x y]}]
  (.fillRect ctx (:current x) (:current y) 50 50))

(defn render [canvas ctx actors]
  (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
  (dorun (map (partial fill-actor-rect ctx) actors)))

(defn split-distance [new current]
  (+ current (* (- new current) 0.1)))

(defn update-actor [{:keys [x y] :as actor}]
  (reduce
    (fn [m k] (update-in m [k :current] split-distance (get-in m [k :new])))
    actor
    (keys actor)))

(defn update [actors]
  (map #(update-actor %) actors))

(render canvas ctx actors)

(defn loop-game [canvas ctx actors]
  (let [new-actors (update actors)]
    (if (should-update? new-actors)
        (do
          (render canvas ctx new-actors)))
    (.requestAnimationFrame js/window (partial loop-game canvas ctx new-actors))))
        
(loop-game canvas ctx actors)
