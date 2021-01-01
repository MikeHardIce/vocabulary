(ns vocabularytrainer.gui
  (:require [strigui.core :as gui]
            [vocabularytrainer.store :as store]))

(def current-items (atom []))

(defn clear-screen []
  (doall (map (fn [item] (gui/remove! item) (println "remove")) @current-items)))

(defn- list-vocabularies []
  (loop [y 100
        v (store/get-vocables)
        index 0
        widget-names []]
    (if (seq v)
      (do 
        (gui/label (str "voc" index) (:term (first v)) {:x 100 :y y :color [:black]})
        (gui/label (str "lang" index) (:name (first v)) {:x 400 :y y :color [:black]})
        (recur (+ y 50) (rest v) (inc index) (conj widget-names (str "voc" index) (str "lang" index))))
      widget-names)))

(defn show-list [main-menu-f]
  (clear-screen)
  (gui/update! "title" :value "View")
  (let [voc (list-vocabularies)]
    (gui/button "back" "Back" {:x 100 :y 500 :color [:white :black] :min-width 150})
    (gui/update! "back" [:events :mouse-clicked] (fn [wdg] (main-menu-f)))
    (reset! current-items (conj voc "back"))))

(defn show-menu-main []
  (clear-screen)
  (gui/update! "title" :value "Vocabulary Trainer")
  (gui/button "menu-practice" "Practice" {:x 220 :y 115 :color [:white :black] 
                                                  :min-width 150})
  (gui/button "menu-add" "Add" {:x 220 :y 165 :color [:white :black] 
                                              :min-width 150})
  (gui/button "menu-view" "View" {:x 220 :y 215 :color [:white :black] 
                                                :min-width 150})
  (reset! current-items ["menu-practice" "menu-add" "menu-view"])
  (gui/update! "menu-view" [:events :mouse-clicked] (fn [wdg] (show-list show-menu-main))))

(defn build-main
  []
  (gui/window 600 600)
  (gui/label "title" "Vocabulary Trainer" {:x 200 :y 50 
                                            :color [:black]
                                            :font-size 20})
  (show-menu-main))