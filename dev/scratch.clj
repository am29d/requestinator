(do
  (refresh)
  (let [dir "/tmp/requestinator/petstore-full/results/2016-08-29T10:37:06"
        now (java.util.Date.)
        dest (str dir
                  "/reports/"
                  (format "%TFT%TT" now now))]
    (report/report {:fetch-f (ser/file-fetcher dir)
                    :record-f (ser/file-recorder dest)})
    (clojure.java.shell/sh "open" (str dest "/main/html/index.html"))))

(with-open [rw (recorder-writer (file-recorder "/tmp/requestinator-test/reports/2016-07-25T13:38:18")
                                "test.txt")]
  (.write rw "Hi there"))

(+ 1 2)

(let [dir "/tmp/requestinator/results/2016-08-08T15:21:16"
      fetch-f (ser/file-fetcher dir)]
  (->  "index.transit"
       fetch-f
       ser/decode
       rand-nth
       :path
       fetch-f
       ser/decode
       :response
       :body
       pprint))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Why is generation using up all my memory?
;;

;; Answer: because test.check was generating with a max-length of 100,
;; which is too big for complicated specs.

(as-> "http://localhost:8888/v1/petstore-full.json" ?
  (slurp ?)
  (json/read-str ?)
  (generate ? 30)
  (drop 100 ?)
  ;;(take 100 ?)
  ;;(dorun ?)
  (first ?)
  (pprint ?)
  ;;(time ?)
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; What would a causatum spec look like?


(main/generate
 {:generator-uri "file:resources/petstore-uniform.edn"
  :destination  "file:///tmp/requestinator/petstore-uniform/requests"
  :agent-count  3
  :duration-sec 60}
 nil)

(gen/read-generator "file:resources/petstore-uniform.edn"
                    (swagger/readers "file:resources/petstore-uniform.edn"))

(let [fetcher (ser/create-fetcher "file:resources/petstore-uniform.edn")]
  (->> (fetcher "")
       String.
       (clojure.edn/read-string {:readers (swagger/readers "file:resources/petstore-uniform.edn")})))


(let [reader (get (swagger/readers "file:resources/petstore-uniform.edn") 'com.cognitect.requestinator.spec)]
  (reader {:spec "http://petstore.swagger.io/v2/swagger.json"
           :amendments nil}))

(main/generate
 {:generator-uri
  "file:resources/petstore-markov.edn"
  #_"file:resources/petstore-uniform.edn"
  :destination  "file:///tmp/requestinator/petstore-markov-2/requests"
  :agent-count  3
  :duration-sec 60}
 nil)

(defn foo [x]
  (tcgen/sample-seq
   (tcgen/let [b (tcgen/return 3)
               a (if (even? x)
                   (tcgen/elements [1 2])
                   (tcgen/elements [3 4]))]
     [a b])))

(take 5 (foo 2))

(as-> (swagger/read-spec "" {:base "http://petstore.swagger.io/v2/swagger.json"}) ?
  (swagger/request-generator ? {:path "/pet/findByStatus"})
  (tcgen/sample-seq ?)
  (drop 10 ?)
  (take 10 ?)
  (pprint ?))


(->> (markov/generate
      (swagger/read-spec "" {:base "http://petstore.swagger.io/v2/swagger.json"})
      {:query-by-status {:path "/pet/findByStatus"
                         :method "get"}
       :query-by-tags   {:path "/pet/findByTags"
                         :method "get"}
       :pet-by-id       {:path   "/pet/{petId}"
                         :method "get"}}
      {:start           [{:query-by-status {:weight 1
                                            :delay '(constant 1)}
                          :query-by-tags   {:weight 1
                                            :delay '(constant 2)}}]
       :query-by-status [{:pet-by-id     {:weight 1
                                          :delay  '(erlang 10)}
                          :query-by-tags {:weight 1
                                          :delay  '(erlang 10)}}]
       :query-by-tags   [{:pet-by-id {:weight 1
                                      :delay  '(erlang 10)}}]
       :pet-by-id       [{:pet-by-id {:weight 1
                                      :delay  '(erlang 10)}}]})
     (take 10)
     (pprint))

(->> (swagger/generate
      (swagger/read-spec "" {:base "http://petstore.swagger.io/v2/swagger.json"})
      1)
     (take 10)
     pprint)

;; Danger, Will Robinson!!!
(->> "/tmp/requestinator"
     io/file
     file-seq
     (remove #(#{"." ".."} (.getName %)))
     (sort-by #(-> % .getPath count))
     reverse
     (map #(.delete %))
     dorun)

(main/generate {:destination "file:///tmp/requestinator/"
                :params-uri "resources/petstore-mixed.edn"}
               [])

(pprint (main/read-params  "resources/petstore-mixed.edn"))

(main/execute {:source "file:///tmp/requestinator/"
               :destination "file:///tmp/requestinator/results"
               :recorder-concurrency 3}
              [])

(main/report {:source "file:///tmp/requestinator/results"
              :destination "file:///tmp/requestinator/reports"}
             [])

(clojure.java.shell/sh "open" "/tmp/requestinator/reports/main/html/index.html")

(let [fetcher (ser/create-fetcher
               "file://tmp/requestinator"
               #_"file://tmp/requestinator/results")]
  (->> #_"index.transit"
       "markov-0000/0000003947.transit"
       fetcher
       ser/decode
       #_(map :agent-id)
       #_distinct
       #_sort
       #_:store
       #_first
       #_val
       #_seq?
       :com.cognitect.requestinator.scheduler/request
       pprint))


(report/write-js (ser/create-recorder "file:///tmp/requestinator-test/")
                 "js")

(clojure.java.shell/sh "open" "/tmp/requestinator/reports/main/html/index.html")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def s (graphql/read-spec {:url "https://graphql-swapi.parseapp.com/?"}))


(->> 'com.cognitect.requestinator.main
     find-ns
     #_ns-aliases
     ns-refers
     vals
     (map #(.-ns %))
     (into #{}))


(swagger/map->AbstractRequest )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(as-> (swagger/read-spec "" {:base "http://petstore.swagger.io/v2/swagger.json"}) ?
  (:spec ?)
  (swagger/request-generator ? {:path "/pet/findByStatus"
                                :param-overrides {"status" ["available"]}})
  (tcgen/sample-seq ?)
  (drop 10 ?)
  (take 10 ?)
  (pprint ?))

(get-in (swagger/read-spec "" {:base "http://petstore.swagger.io/v2/swagger.json"})
        ["paths" "/pet/findByStatus"])

(pprint (swagger/read-spec "" {:base "http://petstore.swagger.io/v2/swagger.json"}))

(ser/)

(def v (let [dir "/tmp/requestinator/markov-0000/"
             fetch-f (ser/file-fetcher dir)]
         (->  "0000001000.transit"
              fetch-f
              ser/decode)))

(let [fetcher (ser/create-fetcher
               "file://tmp/requestinator")]
  (-> "markov-0000/0000002000.transit"
      fetcher
      (ser/decode {:handlers (->> main/spec-types
                                  (map ser/transit-read-handlers)
                                  (reduce merge))})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol Evaluates
  (-eval [this context]))

(extend-protocol Evaluates
  Object
  (-eval [this context] this))

(defrecord Recall [nm default]
  (-eval [this context] (get context nm default)))

(defn -fill-in [template context]
  (let [params (map #(-eval % context) (:params template))])
  )
