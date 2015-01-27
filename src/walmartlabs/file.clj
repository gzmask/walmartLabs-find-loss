(ns walmartlabs.file
  (:require  [clojure.core.async :as async]
             [clojure.string :as str]
             [clojure.java.io :as io]))

(defn parse-store [s]
  (let [t (str/split (str/trim s) #"#")]
    (when
      (not (or (nil? (first t))
               (nil? (second t))))
    {:name (first t)
     :num (Integer/parseInt (second t))})))

(defn parse-total [s]
  (let [t (str/split (str/trim s) #" ")]
    (when (not (nil? (second t)))
    (Float/parseFloat (second t)))))

(defn parse-products [v]
    (for [p v]
      (let [t (re-find #"(.+)\s+(\d{10})\s+(\d+\.?\d*)" (str/trim p))]
        (when (not (or (nil? (second t))
                       (nil? (nth t 2))
                       (nil? (nth t 3))))
        {:name (second t)
         :id (nth t 2)
         :price (Float/parseFloat (nth t 3))
         }))))

(def file-ducer
  (comp
   ;;remove non-files
   (filter (fn [f]
             (.isFile f)))
   ;;read file into string
   (map
    (fn [f]
      (slurp f)
     ))
   ;;seperate lines
   (map
    (fn [s]
      (str/split-lines s)))
   ;;
   (map
    (fn [v]
      {:store (parse-store (first v))
       :products (parse-products (drop-last (rest v)))
       :count (- (count v) 2)
       :total (parse-total (last v))}
      ))
   ))

(def prod-chan (async/chan 1 file-ducer))

;(def result-chan (async/reduce #(str %1 %2 " ") "" prod-chan))

(defn read-files
  [dir]
  (async/onto-chan prod-chan (file-seq (io/file dir)))
  (async/<!! (async/into [] prod-chan)))

;(read-files "./resources/data")

;(instance? java.io.File (first (file-seq (io/file "./resources/data"))))
