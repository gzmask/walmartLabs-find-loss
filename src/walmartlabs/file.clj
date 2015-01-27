(ns walmartlabs.file
  (:require  [clojure.core.async :as async]
             [clojure.string :as str]
             [clojure.java.io :as io]))

(defn parse-store [s]
  (let [t (str/split (str/trim s) #"#")]
    (when
      (not (or (nil? (first t))
               (nil? (second t))))
     (keyword (second t)))))

(defn parse-total [s]
  (let [t (str/split (str/trim s) #" ")]
    (when (not (nil? (second t)))
    (Float/parseFloat (second t)))))

(defn parse-products [v]
  (for [p (group-by first
                    (filter (complement nil?)
                            (for [p v]
                              (let [t (re-find #"(.+)\s+(\d{10})\s+(\d+\.?\d*)" (str/trim p))]
                                (when (not (or (nil? (second t)) (nil? (nth t 2)) (nil? (nth t 3))))
                                  [(keyword (nth t 2))
                                   (Float/parseFloat (nth t 3))])))))]
    {(first p)
     (for [pri (second p)]
       (second pri))}))

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
      {(parse-store (first v))
       (parse-products (drop-last (rest v)))
       ;:total (parse-total (last v))
       }
      ))
   ))

(defn f-map [f m]
  (reduce (fn [altered-map [k v]] (assoc altered-map k (f v))) {} m))

(defn filter-map [f m]
  (select-keys m (for [[k v] m :when (f v)] k)))

(defn read-correct-price
  [f]
  (let [file-str (slurp f)
        str-v (str/split-lines file-str)
        price-v (for [s str-v] (str/split s #","))
        ]
    (into (sorted-map)
          (for [v price-v]
            [(keyword (first v))
             (Float/parseFloat (second v))]
      ))))

(defn get-errors
  [price stores]
  (for [store (keys stores)]
    [(name store)
     (reduce +
      (for [product (keys (store stores))]
        (reduce + (map (fn [p] (- p (product price))) (product (store stores)))))
      )]))

;;read files and parses them.
(defn read-files
  [dir pri thread-num]
  (let [files (file-seq (io/file dir))
        price-map (read-correct-price (io/file (str dir pri)))
        file-count (quot (count files) thread-num)
        files-v (partition-all file-count files)
        file-chans (take (count files-v) (repeat (async/chan 1 file-ducer)))
        _ (doseq [n (range (count files-v))] (async/onto-chan (nth file-chans n) (nth files-v n)))
        prod-chan (async/merge file-chans)
        r-map (async/<!! (async/into [] prod-chan))
        t-map (apply merge-with concat r-map) ;merge duplicated stores
        store-map (filter-map (complement nil?) (f-map #(apply merge-with concat %) t-map)) ;merge duplicated products
        ]
    (println "store, plusminus")
    (doseq [w (get-errors price-map store-map)]
      (println (format "%s, %.2f" (first w) (second w)))
      )
  ))
