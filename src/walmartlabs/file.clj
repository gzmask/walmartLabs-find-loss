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
  (for [p (group-by first
    (filter (complement nil?)
      (for [p v]
        (let [t (re-find #"(.+)\s+(\d{10})\s+(\d+\.?\d*)" (str/trim p))]
          (when (not (or (nil? (second t))
                         (nil? (nth t 2))
                         (nil? (nth t 3))))
          [;:name (second t)
           ;:id (nth t 2)
           ;:price (Float/parseFloat (nth t 3))
           (keyword (nth t 2)) (Float/parseFloat (nth t 3))])))))]
    [(first p)
     (for [pri (second p)]
       (second pri)
       )]
    ))

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
   ;;parse the data into maps
   (map
    (fn [v]
      {:store (parse-store (first v))
       :products (parse-products (drop-last (rest v)))
       :total (parse-total (last v))}
      ))
   ))

;(def prod-chan (async/chan 1 file-ducer))

;(def result-chan (async/reduce #(str %1 %2 " ") "" prod-chan))

;;read files and parses them.
(defn read-files
  [dir thread-num]
  (let [files (file-seq (io/file dir))
        file-count (quot (count files) thread-num)
        files-v (partition-all file-count files)
        file-chans (take (count files-v) (repeat (async/chan 1 file-ducer)))
        _ (doseq [n (range (count files-v))] (async/onto-chan (nth file-chans n) (nth files-v n)))
        prod-chan (async/merge file-chans)]
  (async/<!! (async/into [] prod-chan))))

;(read-files "./resources/data")

;(instance? java.io.File (first (file-seq (io/file "./resources/data"))))
