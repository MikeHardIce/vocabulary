(ns vocabularytrainer.gui
  (:require [strigui.core :as gui]
            [vocabularytrainer.store :as store]))

(def current-items-on-screen (atom []))

(defn clear-screen []
  (doall (map #(gui/remove! %) @current-items-on-screen)))

(defn create-back-button 
  ([main-menu-f] (create-back-button main-menu-f 100 500))
  ([main-menu-f x y]
  (gui/button "back" "Back" {:x x :y y :color [:white :black] :min-width 150})
  (gui/update! "back" [:events :mouse-clicked] (fn [wdg] (main-menu-f)))
  (swap! current-items-on-screen conj "back")))

(defn view-practice [main-menu-f]
  (clear-screen)
  (gui/update! "title" :value "Practice")
  (create-back-button main-menu-f))

(defn view-add-vocabularies [main-menu-f]
  (clear-screen)
  (gui/update! "title" :value "Add Vocabularies")
  (create-back-button main-menu-f))

(defn list-vocabularies []
  (loop [y 100
        v (store/get-vocables)
        index 0
        widget-names []]
    (if (seq v)
      (do 
        (gui/label (str "voc" index) (:term (first v)) {:x 100 :y y :color [:black]})
        (gui/label (str "lang" index) (:name (first v)) {:x 400 :y y :color [:black]})
        (recur (+ y 20) (rest v) (inc index) (conj widget-names (str "voc" index) (str "lang" index))))
      widget-names)))

(defn view-vocabularies [main-menu-f]
  (clear-screen)
  (gui/update! "title" :value "View")
  (let [voc (list-vocabularies)]
    (println voc)
    (reset! current-items-on-screen (vec (flatten (conj @current-items-on-screen voc))))
    (create-back-button main-menu-f)))

(defn show-menu-main []
  (clear-screen)
  (gui/update! "title" :value "Vocabulary Trainer")
  (gui/button "menu-practice" "Practice" {:x 220 :y 115 :color [:white :black] 
                                                  :min-width 150})
  (gui/button "menu-view" "View" {:x 220 :y 215 :color [:white :black] 
                                                :min-width 150})
  (gui/button "menu-add" "Add" {:x 220 :y 165 :color [:white :black] 
                                              :min-width 150})
  
  (reset! current-items-on-screen ["menu-practice" "menu-add" "menu-view"])
  (gui/update! "menu-practice" [:events :mouse-clicked] (fn [wdg] (view-practice show-menu-main)))
  (gui/update! "menu-view" [:events :mouse-clicked] (fn [wdg] (view-vocabularies show-menu-main)))
  (gui/update! "menu-add" [:events :mouse-clicked] (fn [wdg] (view-add-vocabularies show-menu-main))))

(defn build-main
  []
  (gui/window 600 600)
  (gui/label "title" "Vocabulary Trainer" {:x 200 :y 50 
                                            :color [:black]
                                            :font-size 20})
  (show-menu-main))