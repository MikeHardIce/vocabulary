(ns vocabularytrainer.practice)

;; {:max-stage 5 :exercises [{:stage 0 :exercise {:question "" :answer ""}}]}

(defn load-exercises
  "translations - vector of related term parts, for example
   [[english-term0 german-term0] [english-term1 german-term1]]"
  [translations max-stage]
  (let [exercises (map (fn [terms]
                         {:stage 0 :exercise {:question (terms 0) :answer (terms 1)}})
                       translations)]
    {:max-stage max-stage :exercises (vec exercises)}))

(defn show-all-stages
  [practice]
  (let [grouped (group-by :stage (:exercises practice))
        max-stage (:max-stage practice)]
    (for [key (range 0 max-stage)]
      (count (grouped key)))))

(defn get-random-practice-item
  [practice]
  (let [not-completed-items (filter #(< (:stage %) (:max-stage practice)) (:exercises practice))]
    (when (seq not-completed-items)
      (rand-nth not-completed-items))))

(defn get-question
  [item]
  (-> item
      :exercise
      :question))

(defn get-answer
  [item]
  (-> item
      :exercise
      :answer))

(defn correct?
  [practice answered questioned]
  (when-let [practice-entity (filter (fn [item]
                                       (let [q (get-question item)
                                             answ (get-answer item)]
                                         (and (= q questioned) (= answ answered)))) (:exercises practice))]
    (first practice-entity)))

(defn increment-stage
  [practice answered questioned]
  (when-let [practice-entity (correct? practice answered questioned)]
    (let [ex (remove #(= practice-entity %) (:exercises practice))
          elem (assoc practice-entity :stage (inc (:stage practice-entity)))
          new-ex (conj ex elem)]
      (vec new-ex))))