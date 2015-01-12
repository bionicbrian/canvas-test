(ns canvas-test.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [goog.dom :as dom]
              [goog.events :as events]
              [cljs.core.async :refer [put! chan <!]]))

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
      (update-in m [k :current] split-distance (get-in m [k :new]))))

(defn update-actor [{:keys [x y] :as actor}]
  (reduce move-distance actor (keys (select-keys actor [:x :y]))))

(defn update [actors]
  (map #(update-actor %) actors))

(defn update-actor-with-points [actor x y]
  (let [new-points {:x x :y y}]
    (.log js/console "Gonna update the actor with points")
    (.log js/console (str "THe x is " x " and the y is " y))
    (reduce (fn [m k] (update-in m [k :new] (k new-points))) actor (keys (select-keys actor [:x :y])))))

(defn update-coords [actors e]
  (let [x (.-offsetX e) y (.-offsetY e)]
    (swap! actors #(map (fn [actor] (update-actor-with-points actor x y)) %))))

(defn loop-game [chan]
  (go (while true
    (let [e (<! chan)]
      ; (update-coords actors e))))
      (.log js/console "Clickin"))))
  (defn looper []
    (if (should-update? @actors)
        (do
          (render canvas ctx actors)
          (swap! actors update)
          (.requestAnimationFrame js/window looper))))
  (looper))

(defn listen [el type]
  (let [c (chan)]
    (events/listen el type #(put! c %))
    c))

(defn init-game []
  (loop-game (listen js/document "click")))

(init-game)
