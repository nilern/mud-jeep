(ns mud-jeep.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [rewrite-clj.node :as node]
            [rewrite-clj.zip :as zr])
  (:import [java.io File]))

(defn convert-ns [loc]
  (if (zr/list? loc)
    (let [op-loc (zr/down loc)]
      (if (= (zr/sexpr op-loc) 'ns)
        (-> loc
            (zr/find-value zr/next :require)
            (zr/insert-right (node/spaces 10))
            (zr/insert-right (node/newline-node "\n"))
            (zr/insert-right '[clojure.test :refer [deftest]])
            zr/up
            zr/up)
        loc))
    loc))

(def ^:dynamic factoid? #{'fact 'facts})

;; Originally copied from https://github.com/circleci/translate-midje:
(defn munge-name [name]
  (-> name
      (str/replace " " "-")
      (str/replace "." "")
      (str/replace "`" "")
      (str/replace "'" "")
      (str/replace "(" "")
      (str/replace ")" "")
      (str/replace "/" "")
      (str/replace "," "-")
      (str/replace "[" "")
      (str/replace "]" "")
      (str/replace #"^(\d+)" "_$1")
      (str/replace #"^:" "")
      (str/replace #"-+" "-")))

(defn convert-factoid [loc]
  (if (zr/list? loc)
    (let [op-loc (zr/down loc)]
      (if (factoid? (zr/sexpr op-loc))
        (let [op-loc (zr/replace op-loc 'deftest)
              descr (zr/sexpr (zr/right op-loc))
              test-name (cond
                          (symbol? descr) (symbol (str (name descr) "-test"))
                          (string? descr) (symbol (str (munge-name descr) "-test"))
                          :else (gensym 'test))]
          (zr/replace loc (node/list-node [(node/coerce 'deftest) (node/spaces 1) (node/coerce test-name)
                                           (node/newline-node "\n")
                                           (zr/node loc)])))
        loc))
    loc))

(defn convert-file [file]
  (->> file
       zr/of-file
       zr/up
       (zr/map (comp convert-ns convert-factoid))))

(defn convert-file-inplace! [file]
  (let [converted (convert-file file)]
    (with-open [w (io/writer file)]
      (zr/print-root converted w))))

(defn convert-dir-inplace! [dir]
  (doseq [^File file (file-seq dir)
          :when (.isFile file)]
    (convert-file-inplace! file)))
