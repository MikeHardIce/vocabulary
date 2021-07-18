(ns vocabularytrainer.gui
  (:require [strigui.core :as gui]
            [vocabularytrainer.ui.stacks :as st]
            [vocabularytrainer.store :as store]
            [vocabularytrainer.practice :as pract]))

(def current-items-on-screen (atom []))

(defn clear-screen []
  (doall (map #(gui/remove! %) @current-items-on-screen)))

(defn create-back-button 
  ([main-menu-f] (create-back-button main-menu-f 100 550))
  ([main-menu-f x y]
  (gui/button "back" "Back" {:x x :y y :color [:white :black] :min-width 150})
  (gui/update! "back" [:events :mouse-clicked] (fn [_] (main-menu-f)))
  (swap! current-items-on-screen conj "back")))

(defn refresh-practice-screen
  [practice item]
  (gui/update! "answer" :value "")
  (gui/update! "question" :value (pract/get-question item))
  (gui/update! "progress" :value (pract/show-all-stages practice)))

(defn view-practice 
  [main-menu-f]
  (clear-screen)
  (gui/update! "title" :value "Practice")
  (let [translations (store/get-translations-for "german" "english")
        practice (atom (pract/load-exercises translations 6))
        item (atom (pract/get-random-practice-item @practice))]
    (gui/label "lbl-progress" "Progression" {:x 50 :y 350 :font-size 15})
    (gui/create (st/->Stack "progress" '(0 0) {:x 100 :y 430}))
    (gui/label "lbl-from-lang" "German" {:x 50 :y 100 :font-size 15})
    (gui/label "question" "" {:x 150 :y 150 :min-width 150 :font-size 20})
    (gui/label "lbl-to-lang" "English" {:x 50 :y 200 :font-size 15})
    (gui/input "answer" "" {:x 150 :y 250 :min-width 200 :font-size 20 :color [:white :black]}) ;; change to focused? once fixed in strigui
    (gui/update! "answer" [:events :key-pressed] (fn [wdg key-code]
                                                   (when (= key-code :enter)
                                                        ;; bug in strigui, replace enter until fixed in strigui
                                                     (if (= (pract/get-answer @item) (clojure.string/replace (:value wdg) #"\n" ""))
                                                       (swap! practice pract/move-item-forward @item)
                                                       (swap! practice pract/move-item-backwards @item))
                                                     (reset! item (pract/get-random-practice-item @practice))
                                                     (refresh-practice-screen @practice @item)
                                                     (when (pract/finished? @practice)
                                                       (main-menu-f)))))
    (swap! current-items-on-screen conj "progress" "question" "answer" "lbl-from-lang" "lbl-to-lang" "lbl-progress")
    (refresh-practice-screen @practice @item))
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
    (reset! current-items-on-screen (vec (flatten (conj @current-items-on-screen voc))))
    (create-back-button main-menu-f)))

(defn view-menu-main []
  (clear-screen)
  (gui/update! "title" :value "Vocabulary Trainer")
  (gui/button "menu-practice" "Practice" {:x 220 :y 115 :color [:white :black]
                                          :min-width 150})
  (gui/button "menu-view" "View" {:x 220 :y 215 :color [:white :black]
                                  :min-width 150})
  (gui/button "menu-add" "Add" {:x 220 :y 165 :color [:white :black]
                                :min-width 150})

  (reset! current-items-on-screen ["menu-practice" "menu-add" "menu-view"])
  (gui/update! "menu-practice" [:events :mouse-clicked] (fn [wdg] (view-practice view-menu-main)))
  (gui/update! "menu-view" [:events :mouse-clicked] (fn [wdg] (view-vocabularies view-menu-main)))
  (gui/update! "menu-add" [:events :mouse-clicked] (fn [wdg] (view-add-vocabularies view-menu-main))))

(defn build-main
  []
  (gui/window! 600 600 "Vocabulary Trainer")
  (gui/label "title" "Vocabulary Trainer" {:x 200 :y 50 
                                            :color [:black]
                                            :font-size 20})
  (view-menu-main))