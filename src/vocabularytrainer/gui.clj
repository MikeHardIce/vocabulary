(ns vocabularytrainer.gui
  (:require [strigui.core :as gui]
            [vocabularytrainer.ui.stacks :as st]
            [vocabularytrainer.store :as store]
            [vocabularytrainer.practice :as pract]))

(defonce default-args {:x 220 :y 115 :color [:white :black]
                       :width 150 :can-tab? true})

(defn clear-screen-handler
  [group]
  (fn [] (gui/remove-group! group)))

(defn create-back-button 
  ([group main-menu-f] (create-back-button group main-menu-f 100 550))
  ([group main-menu-f x y]
  (gui/button! "back" "Back" (assoc default-args :x x :y y :group group))
  (gui/update! "back" [:events :mouse-clicked] (fn [_] (main-menu-f)))))

(defn refresh-practice-screen
  [practice item]
  (gui/update! "answer" :value "")
  (gui/update! "question" :value (pract/get-question item))
  (gui/update! "progress" :value (pract/show-all-stages practice)))

(defn view-practice 
  [main-menu-f clear-screen]
  (clear-screen)
  (gui/update! "title" :value "Practice")
  (let [translations (store/get-translations-for "german" "english")
        practice (atom (pract/load-exercises translations 6))
        item (atom (pract/get-random-practice-item @practice))]
    (gui/label! "lbl-progress" "Progression" {:x 50 :y 350 :font-size 15 :group "practice"})
    (gui/create! (st/->Stack "progress" '(0 0) {:x 100 :y 430 :group "practice"}))
    (gui/label! "lbl-from-lang" "German" {:x 50 :y 100 :font-size 15 :group "practice"})
    (gui/label! "question" "" {:x 150 :y 150 :width 250 :font-size 20 :group "practice"})
    (gui/label! "lbl-to-lang" "English" {:x 50 :y 200 :font-size 15 :group "practice"})
    (gui/input! "answer" "" {:x 150 :y 250 :width 200 :font-size 20 :selected? true :can-tab? true :color [:white :black] :group "practice"})
    (gui/update! "answer" [:events :key-pressed] (fn [wdg key-code]
                                                   (when (= key-code :enter)
                                                     (if (= (pract/get-answer @item) (clojure.string/replace (:value wdg) #"\n" ""))
                                                       (swap! practice pract/move-item-forward @item)
                                                       (swap! practice pract/move-item-backwards @item))
                                                     (reset! item (pract/get-random-practice-item @practice))
                                                     (refresh-practice-screen @practice @item)
                                                     (when (pract/finished? @practice)
                                                       (main-menu-f)))))
    (refresh-practice-screen @practice @item))
  (create-back-button "practice" main-menu-f))

(defn view-add-vocabularies [main-menu-f clear-screen]
  (clear-screen)
  (gui/update! "title" :value "Add Vocabularies")
  (create-back-button main-menu-f "add"))

(defn list-vocabularies [group]
  (loop [y 100
        v (store/get-vocables)
        index 0
        widget-names []]
    (if (seq v)
      (do 
        (gui/label! (str "voc" index) (:term (first v)) {:x 100 :y y :color [:black] :group group})
        (gui/label! (str "lang" index) (:name (first v)) {:x 400 :y y :color [:black] :group group})
        (recur (+ y 20) (rest v) (inc index) (conj widget-names (str "voc" index) (str "lang" index))))
      widget-names)))

(defn view-vocabularies [main-menu-f clear-screen]
  (clear-screen)
  (gui/update! "title" :value "View")
  (list-vocabularies "view")
  (create-back-button main-menu-f "view"))

(defn view-menu-main [clear-screen]
  (clear-screen)
  (gui/update! "title" :value "Vocabulary Trainer")
  (gui/button! "menu-practice" "Practice" (assoc default-args :y 115 :group "main"))
  (gui/button! "menu-add" "Add" (assoc default-args :y 165 :group "main"))
  (gui/button! "menu-view" "View" (assoc default-args :y 215 :group "main"))
  (gui/button! "menu-exit" "Exit" (assoc default-args :y 265 :group "main"))

  (gui/update! "menu-practice" [:events :mouse-clicked] (fn [wdg] (view-practice #(view-menu-main (clear-screen-handler "practice")) 
                                                                                 (clear-screen-handler "main"))))
  (gui/update! "menu-view" [:events :mouse-clicked] (fn [wdg] (view-vocabularies #(view-menu-main (clear-screen-handler "view"))  
                                                                                 (clear-screen-handler "main"))))
  (gui/update! "menu-add" [:events :mouse-clicked] (fn [wdg] (view-add-vocabularies #(view-menu-main (clear-screen-handler "add")) 
                                                                                    (clear-screen-handler "main"))))
  (gui/update! "menu-exit" [:events :mouse-clicked] (fn [wdg] (gui/close-window))))

(defn build-main
  []
  (gui/window! 600 600 "Vocabulary Trainer")
  (gui/label! "title" "Vocabulary Trainer" {:x 200 :y 50 
                                            :color [:black]
                                            :font-size 20})
  (view-menu-main #()))