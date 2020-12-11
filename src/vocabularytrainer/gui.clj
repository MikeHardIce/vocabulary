(ns vocabularytrainer.gui
  (:require [strigui.core :as gui]))

(defn build-main
  []
  (gui/window 600 600)
  (gui/label "title" "Welcome to Strigui" {:x 300 :y 50 :color [:black] :align [:center]})
  (gui/label "question" "test" {:x 150 :y 150 :color [:black] :align [:left]})
  (gui/input "answer" "" {:x 150 :y 200 :color [:white :black] :min-width 300})
  (gui/stacks "progress" '(10 0 0 0 0) {:x 100 :y 300}))