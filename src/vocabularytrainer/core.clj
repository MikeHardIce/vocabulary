(ns vocabularytrainer.core
  (:require [vocabularytrainer.gui :as gui])
  (:gen-class))

(defn -main
  ""
  [& args]
  (gui/build-main))