(ns vocabularytrainer.practice)

;; {:max-stage 5 :exercises [{:stage 0 :exercise {:question "" :answer ""}}]}
(def current-practice (atom {:max-stage 5 :exercises []}))

(defn load-exercises
  "translations - vector of related term parts, for example
   [[english-term0 german-term0] [english-term1 german-term1]]"
  [translations max-stage]
  (let [exercises (map (fn [terms]
                         {:stage 0 :exercise {:question (terms 0) :answer (terms 1)}})
                       translations)]
    (reset! current-practice {:max-stage max-stage :exercises (vec exercises)})))

(defn get-current-stages
  []
  (let [grouped (group-by :stage (:exercises @current-practice))
        max-stage (:max-stage @current-practice)]
    (for [key (range 0 max-stage)]
      (count (grouped key)))))

(defn question
  [item]
  (-> item
      :exercise
      :question))

(defn answer
  [item]
  (-> item
      :exercise
      :answer))

(defn correct?
  [answered questioned]
  (when-let [practice-entity (filter (fn [item]
                                       (let [q (question item)
                                             answ (answer item)]
                                         (and (= q questioned) (= answ answered)))) (:exercises @current-practice))]
    (first practice-entity)))

(defn inc-stage
  [answered questioned]
  (when-let [practice-entity (correct? answered questioned)]
    (let [ex (remove #(= practice-entity %) (:exercises @current-practice))
          elem (assoc practice-entity :stage (inc (:stage practice-entity)))
          new-ex (conj ex elem)]
      (reset! current-practice (vec new-ex)))))