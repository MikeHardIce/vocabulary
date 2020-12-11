(ns vocabularytrainer.core
  (:require [vocabularytrainer.gui :as gui]
            [vocabularytrainer.store :as store]))

(defn main
  ""
  [& args]
  (gui/build-main))
