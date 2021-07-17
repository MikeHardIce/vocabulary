(ns vocabularytrainer.practice)

;; {:max-stage 5 :exercises [{:stage 0 :exercise {:question "" :answer ""}}]}

(defn load-exercises
  "translations - vector of related term parts, for example
   [[english-term0 german-term0] [english-term1 german-term1]]"
  [translations max-stage]
  (let [exercises (map (fn [terms]
                         {:stage 0 :exercise {:question (:question terms) :answer (:answer terms)}})
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

(defn- update-items-stage
  [practice item func]
  (let [exercises (filter #(not= % item) (:exercises practice))
        practice (assoc practice :exercises (conj exercises (update item :stage func)))]
    practice))

(defn move-item-forward
  [practice item]
  (if (< (:stage item) (:max-stage practice))
    (update-items-stage practice item inc)
    practice))

(defn move-item-backwards
  [practice item]
  (if (> (:stage item) 0)
    (update-items-stage practice item dec)
    practice))

(defn finished?
  [practice]
  (let [exercises (:exercises practice)
        at-max-stage (filter #(<= (dec (:max-stage practice)) (:stage %)) exercises)]
    (= (count at-max-stage) (count exercises))))