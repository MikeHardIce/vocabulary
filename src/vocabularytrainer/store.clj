(ns vocabularytrainer.store
  (:require [clojure.java.jdbc :as db]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/vocabularies.db"})

(defn create-db []
  (try (db/db-do-commands db [(db/create-table-ddl :languages
                                                   ;; needs to be :integer (not :int) otherwise
                                                   ;; it doesn't autoincrement on insert
                                                   [[:id :integer :primary :key]
                                                    [:name :text :unique]]
                                                   {:conditional? true})
                              (db/create-table-ddl :vocables
                                                   [[:id :integer :primary :key]
                                                    [:lang_id :int]
                                                    [:term :text] 
                                                    ;;foreign key doesn't work atm bec I cannot set
                                                    ;; PRAGMA foreign_keys = ON
                                                    ["FOREIGN KEY(lang_id) REFERENCES language(id) ON DELETE CASCADE"]]
                                                   {:conditional? true})
                              "CREATE UNIQUE INDEX IF NOT EXISTS vocables_lang_term ON vocables(lang_id, term)"
                              (db/create-table-ddl :translations 
                                                   [[:vocable1_id :integer]
                                                    [:vocable2_id :integer]]
                                                   {:conditional? true})
                              "CREATE UNIQUE INDEX IF NOT EXISTS idx_trans ON translations (vocable1_id, vocable2_id)"])
       (catch Exception e
         (println (.getMessage e)))))

(defn- get-lang-id [name]
  (-> (db/query db (str "Select id from languages where name = '" name "'"))
      first
      :id))

(defn- get-vocable-id [term lang]
  (-> (db/query db (str "Select v.id from vocables v
                        INNER JOIN languages l ON v.lang_id = l.id 
                         where l.name = '" lang "' and v.term = '" term "'"))
      first
      :id))

(defn get-translation-for [term from-lang to-lang]
  (let [voc-id (get-vocable-id term from-lang)]
    (db/query db (str "Select distinct v.term from vocables v
                      where v.id in (Select t.vocable2_id 
                      from translations t where t.vocable1_id = " voc-id
                         " UNION 
                      Select t.vocable1_id from translations t where t.vocable2_id = " voc-id ")"))))

(defn remove-translation [vocable1 vocable2]
  (db/delete! db :translations ["vocable1_id = ?" (min vocable1 vocable2) "vocable2_id = ?" (max vocable1 vocable2)]))

(defn remove-language [name]
  (let [lang-id (get-lang-id name)]
    (when (and (some? lang-id) (>= lang-id 0))
      (when (pos-int? (first (db/delete! db :languages ["id = ?" lang-id])))
        (db/delete! db :vocables ["lang_id = ?" lang-id])))))

(defn add-translation [vocable1 vocable2]
  (db/insert! db :translations {:vocable1_id (min vocable1 vocable2) :vocable2_id (max vocable1 vocable2)}))
  
  ;;(db/db-do-commands db ["PRAGMA foreign_keys = ON"
  ;;                       (str "DELETE FROM languages WHERE name = '" name "'")]))
(defn add-language [name]
  (db/insert! db :languages {:name name}))

(defn add-vocable [language term]
  (let [lang-id (get-lang-id language)]
    (when (and (some? lang-id) (>= lang-id 0) (> (count term) 0))
      (db/insert! db :vocables {:lang_id lang-id :term term}))))

(defn- do-test []
  (create-db)
  (add-language "English")
  (add-language "German")
  (add-vocable "German" "hier")
  (add-vocable "German" "alle")
  (add-vocable "English" "here")
  (add-vocable "English" "hello")
  (add-translation 1 3))