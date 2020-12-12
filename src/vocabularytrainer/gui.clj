(ns vocabularytrainer.gui
  (:require [strigui.core :as gui]))


(def current-items (atom []))

(defn clear-screen []
  (doall (map (fn [item] (gui/remove! item) (println "remove")) @current-items)))

(defn show-list []
  (clear-screen)
  (gui/update! "title" :value "View")
  (gui/label "voc1" "blabla" {:x 100 :y 100 :color [:black]})
  (gui/label "voc2" "blablabla" {:x 200 :y 100 :color [:black]})
  (reset! current-items ["voc1" "voc2"]))

(defn show-menu-main []
  (clear-screen)
  (gui/update! "title" :value "Vocabulary Trainer")
  (gui/button "menu-practice" "Practice" {:x 220 :y 115 :color [:back :black] 
                                                  :min-width 150})
  (gui/button "menu-add" "Add" {:x 220 :y 165 :color [:back :black] 
                                              :min-width 150})
  (gui/button "menu-view" "View" {:x 220 :y 215 :color [:back :black] 
                                                :min-width 150})
  (reset! current-items ["menu-practice" "menu-add" "menu-view"])
  (gui/update! "menu-view" [:events :mouse-clicked] (fn [wdg] (show-list))))

(defn build-main
  []
  (gui/window 600 600)
  (gui/label "title" "Vocabulary Trainer" {:x 300 :y 50 
                                            :color [:black] 
                                            :align [:center]
                                            :font-size 20})
  (show-menu-main))

